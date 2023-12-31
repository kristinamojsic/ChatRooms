package rs.raf.pds.v4.z5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import application.Controller;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import rs.raf.pds.v4.z5.messages.ChatMessage;
import rs.raf.pds.v4.z5.messages.CreateRoom;
import rs.raf.pds.v4.z5.messages.EditedMessage;
import rs.raf.pds.v4.z5.messages.GetMoreMessages;
import rs.raf.pds.v4.z5.messages.InfoMessage;
import rs.raf.pds.v4.z5.messages.InviteToChatRoom;
import rs.raf.pds.v4.z5.messages.JoinChatRoom;
import rs.raf.pds.v4.z5.messages.KryoUtil;
import rs.raf.pds.v4.z5.messages.ListRooms;
import rs.raf.pds.v4.z5.messages.ListRoomsRequest;
import rs.raf.pds.v4.z5.messages.ListUsers;
import rs.raf.pds.v4.z5.messages.Login;
import rs.raf.pds.v4.z5.messages.ReplyMessage;
import rs.raf.pds.v4.z5.messages.WhoRequest;

public class ChatClient implements Runnable{

	public static int DEFAULT_CLIENT_READ_BUFFER_SIZE = 1000000;
	public static int DEFAULT_CLIENT_WRITE_BUFFER_SIZE = 1000000;
	
	private volatile Thread thread = null;
	
	volatile boolean running = false;
	private static TextField messageTextField;
	final Client client;
	final String hostName;
	final int portNumber;
	final String userName;
	private Controller controller;
	
	public ChatClient(String hostName, int portNumber, String userName,Controller controller,TextField messageTextField) {
		this.client = new Client(DEFAULT_CLIENT_WRITE_BUFFER_SIZE, DEFAULT_CLIENT_READ_BUFFER_SIZE);
		
		this.hostName = hostName;
		this.portNumber = portNumber;
		this.userName = userName;
		this.controller = controller;
		this.messageTextField = messageTextField;
		controller.setChatClient(this);
		KryoUtil.registerKryoClasses(client.getKryo());
		registerListener();
	}
	private void registerListener() {
		client.addListener(new Listener() {
			public void connected (Connection connection) {
				Login loginMessage = new Login(userName);
				client.sendTCP(loginMessage);
			}
			
			public void received (Connection connection, Object object) {
				
				if (object instanceof ListUsers) {
					ListUsers listUsers = (ListUsers)object;
					String users = showOnlineUsers(listUsers.getUsers());
					Platform.runLater(() -> {
				        controller.showMessage(users,"Server");
				    });
					return;
				}
				
				if(object instanceof ListRooms)
				{
					ListRooms listrooms = (ListRooms) object;
					String rooms = showRooms(listrooms.getRooms());
					Platform.runLater(() -> {
					        controller.showMessage(rooms,"Server");
					        });
					return;
				}
				
				if (object instanceof InfoMessage) {
					InfoMessage message = (InfoMessage)object;
					String text = message.getTxt();
				    
				    Platform.runLater(() -> {
				        controller.showMessage(text,"Server");
				    });
				    return;
				}
				
				if (object instanceof ChatMessage) {
					ChatMessage message = (ChatMessage)object;
					
					Platform.runLater(() -> {
						String room = message.getRoomName();
						String user = message.getUser();
						if(message.getEdited())
						{
							controller.showMessage(message.getTxt(), room != null ? room + ": " + user : user,message.getOriginalMessage().getTxt());	
						}
						else
						{
							controller.showMessage(message.getTxt(), room != null ? room + ": " + user : user);	
						}
				    });
					return;
				}
			}
			
			public void disconnected(Connection connection) {
				
			}
		});
	}
	
	private String showOnlineUsers(String[] users) {
		
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<users.length; i++) {
			String user = users[i];
			sb.append(user);
			sb.append((i==users.length-1?"\n":", "));
			
		}
		return sb.toString();
	}
	private String showRooms(String[] rooms) {
		
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<rooms.length; i++) {
			String room = rooms[i];
			sb.append(room);
			sb.append((i==rooms.length-1?"\n":", "));
		}
		return sb.toString();
	}
	public void sendEditedMessage(String originalMessage,String editedMessage) {
		
        client.sendTCP(new EditedMessage(originalMessage,editedMessage));
       
    }
	
	public void sendReplyMessage(String messageRepliedTo, String reply)
	{
		client.sendTCP(new ReplyMessage(messageRepliedTo,reply));
	}
	public void start() throws IOException {
		client.start();
		connect();
		
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void sendMessage(String userInput)
	{
		if (userInput == null || "BYE".equalsIgnoreCase(userInput)) 
    	{
    		running = false;
    	}
    	else if ("WHO".equalsIgnoreCase(userInput)){
    		client.sendTCP(new WhoRequest());
    	}
    	else if("ListRooms".equalsIgnoreCase(userInput))
    	{
    		client.sendTCP(new ListRoomsRequest());
    	}
    	else if(userInput.startsWith("Create"))
    	{
    		String[] parts = userInput.split(" ");
            if (parts.length == 2) {
                client.sendTCP(new CreateRoom(parts[1]));
            } else {
            	client.sendTCP(new InfoMessage("Invalid command. Use 'Create @roomName'."));
            }
    	}
    	else if(userInput.startsWith("Join"))
    	{
    		String[] parts = userInput.split(" ");
            if (parts.length == 2) {
                client.sendTCP(new JoinChatRoom(parts[1]));
            } else {
            	client.sendTCP(new InfoMessage("Invalid command. Use 'Create @roomName'."));
            }
    	}
    	else if(userInput.startsWith("Invite"))
    	{
    		String[] parts = userInput.split(" ");
    		if(parts.length==3)
    		{
    			client.sendTCP(new InviteToChatRoom(parts[1],parts[2]));
    		}
    		else
    		{
    			client.sendTCP(new InfoMessage("Invalid command. Use 'Invite @roomName @username'."));
    		}
    	}
    	else if (userInput.startsWith("GetMoreMessages")) {
        
            String[] parts = userInput.split(" ");
            if (parts.length == 2) {
                client.sendTCP(new GetMoreMessages(parts[1]));
            } else {
            	client.sendTCP(new InfoMessage("Invalid command. Use 'GetMoreMessages @roomName'."));
            }
        }
    	else {
    		String[] parts = userInput.split(" ", 2);
            if (parts.length == 2) {
                String recipient = parts[0];
                String messageText = parts[1];
                client.sendTCP(new ChatMessage(userName, messageText, recipient));
            } else {
            	client.sendTCP(new ChatMessage(userName,userInput,""));            	
            }
    	}
    	
    	this.messageTextField.clear();
	}
	
	public void stop() {
		Thread stopThread = thread;
		thread = null;
		running = false;
		if (stopThread != null)
			stopThread.interrupt();
	}
	
	public void connect() throws IOException {
		client.connect(1000, hostName, portNumber);
	}
	public void run() {
		
		try (BufferedReader stdIn = new BufferedReader(
	                    new InputStreamReader(System.in))) {
					            
			    
				running = true;
	            while (running) {
	         
	            	if (!client.isConnected() && running)
	            		connect();
	            	}
	            
	    } catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			running = false;
			System.out.println("CLIENT SE DISCONNECTUJE");
			client.close();;
		}
	}
	
}
