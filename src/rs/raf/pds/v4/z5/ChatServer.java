package rs.raf.pds.v4.z5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import rs.raf.pds.v4.z5.messages.ChatMessage;
import rs.raf.pds.v4.z5.messages.InfoMessage;
import rs.raf.pds.v4.z5.messages.InviteToChatRoom;
import rs.raf.pds.v4.z5.messages.JoinChatRoom;
import rs.raf.pds.v4.z5.messages.KryoUtil;
import rs.raf.pds.v4.z5.messages.ListRooms;
import rs.raf.pds.v4.z5.messages.ListRoomsRequest;
import rs.raf.pds.v4.z5.messages.ListUsers;
import rs.raf.pds.v4.z5.messages.Login;
import rs.raf.pds.v4.z5.messages.WhoRequest;
import rs.raf.pds.v4.z5.messages.CreateRoom;
import rs.raf.pds.v4.z5.messages.GetMoreMessages;


public class ChatServer implements Runnable{

	private volatile Thread thread = null;
	
	volatile boolean running = false;
	final Server server;
	final int portNumber;
	ConcurrentMap<String, Connection> userConnectionMap = new ConcurrentHashMap<String, Connection>();
	ConcurrentMap<Connection, String> connectionUserMap = new ConcurrentHashMap<Connection, String>();
	private final Set<String> chatRooms = new HashSet<>();
	private final Map<String, Set<Connection>> usersInRooms = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, List<ChatMessage>> roomMessages = new ConcurrentHashMap<>();
	
	public ChatServer(int portNumber) {
		this.server = new Server();
		
		this.portNumber = portNumber;
		KryoUtil.registerKryoClasses(server.getKryo());
		registerListener();
	}
	private void registerListener() {
		server.addListener(new Listener() {
			public void received (Connection connection, Object object) {
				if (object instanceof Login) {
					Login login = (Login)object;
					newUserLogged(login, connection);
					connection.sendTCP(new InfoMessage("Hello "+login.getUserName()));
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				}
				
				if (object instanceof ChatMessage) {
					ChatMessage chatMessage = (ChatMessage)object;
					System.out.println(chatMessage.getUser()+":"+chatMessage.getTxt());
					
					String recipient = chatMessage.getRecipient();
					if(recipient!=null && !recipient.isEmpty())
					{
						if(chatRooms.contains(recipient))
						{
							sendRoomMessage(chatMessage);	
						}
						else
						{
							sendPrivateMessage(chatMessage);
						}
					}
					else
					{
						broadcastChatMessage(chatMessage, connection);
					}
					 
					return;
				}

				if (object instanceof WhoRequest) {
					ListUsers listUsers = new ListUsers(getAllUsers());
					connection.sendTCP(listUsers);
					return;
				}
				if (object instanceof ListRoomsRequest) {
					ListRooms listRooms = new ListRooms(getAllRooms());
					connection.sendTCP(listRooms);
					return;
				}
				if(object instanceof InviteToChatRoom)
				{
					InviteToChatRoom inviteRequest = (InviteToChatRoom) object;
					inviteUserToRoom(connection,inviteRequest.getRoomname(),inviteRequest.getUsername());
				}
				if(object instanceof CreateRoom)
				{
					CreateRoom createRoom = (CreateRoom) object;
					
					createRoom(createRoom.getRoomName(),connection);
					return;
				}
				if(object instanceof JoinChatRoom)
				{
					JoinChatRoom joinRoom = (JoinChatRoom) object;
					
					joinRoom(joinRoom.getRoomName(),connection);
					return;
				}
				if (object instanceof GetMoreMessages) {
					GetMoreMessages getMessages = (GetMoreMessages) object;
		            handleGetMoreMessages(connection, getMessages);
		            return;
		        }
				if(object instanceof InfoMessage)
				{
					InfoMessage infoMessage = (InfoMessage) object;
					connection.sendTCP(infoMessage);
				}
			}
			private void sendRoomMessage(ChatMessage message) {
                String roomName = message.getRecipient();
                message.setRoomName(roomName);
                //String messageText = message.txt;
                //message.txt = "(room:" + roomName + ")" + messageText;
                Set<Connection> connectionsInRoom = usersInRooms.get(roomName);
                if (connectionsInRoom != null) {
                    for (Connection conn : connectionsInRoom) {
                        if (conn.isConnected() && !conn.equals(message.getUser())) {
                            conn.sendTCP(message);
                           // sendRecentMessages(conn, roomName);
                        }
                    }
                }
                addGroupMessage(roomName, message);
            }
			private void sendRecentMessages(Connection conn, String roomName) {
		        List<ChatMessage> recentMessages = roomMessages.getOrDefault(roomName, new ArrayList<>());
		        int messageCount = Math.min(recentMessages.size(), 5);
		        for (int i = 0; i < messageCount; i++) {
		            conn.sendTCP(recentMessages.get(i));
		        }
		        
		    }
			private void addGroupMessage(String roomName, ChatMessage message) {
		        roomMessages.computeIfAbsent(roomName, k -> new ArrayList<>()).add(message);
		    }
			private void sendPrivateMessage(ChatMessage message) {
                String recipient = message.getRecipient();
                Connection recipientConnection = userConnectionMap.get(recipient);
                if (recipientConnection != null && recipientConnection.isConnected()) {
                    recipientConnection.sendTCP(message);
                } else {
                	
                    System.out.println("Recipient " + recipient + " is not available.");
                }
            }
			private void inviteUserToRoom(Connection inviterConnection, String roomName, String invitedUserName) {
                if (chatRooms.contains(roomName) && userConnectionMap.containsKey(invitedUserName)) {
                    Connection invitedConnection = userConnectionMap.get(invitedUserName);
                    if (invitedConnection.isConnected()) {
                        usersInRooms.get(roomName).add(invitedConnection);
                        invitedConnection.sendTCP(new InfoMessage("You have been invited to room: " + roomName + " by " + connectionUserMap.get(inviterConnection)));
                        System.out.println(connectionUserMap.get(inviterConnection) + " invited " + invitedUserName + " to room: " + roomName);
                    } else {
                        inviterConnection.sendTCP(new InfoMessage("User " + invitedUserName + " is not online."));
                    }
                } else {
                    inviterConnection.sendTCP(new InfoMessage("Invalid room or user."));
                }
				//String info = "Room " + roomName + " , user " + invitedUserName; 
				//inviterConnection.sendTCP(new InfoMessage(info));
            }
			private void joinRoom(String roomName,Connection user)
			{
				
				if (chatRooms.contains(roomName)) {
                    usersInRooms.get(roomName).add(user);
                    user.sendTCP(new InfoMessage("Joined room: " + roomName));
                    System.out.println(connectionUserMap.get(user) + " joined room: " + roomName);
                    sendRecentMessages(user,roomName);
				} else {
                    user.sendTCP(new InfoMessage("Room does not exist: " + roomName));
                }
				
			}
			private void handleGetMoreMessages(Connection connection, GetMoreMessages getMoreMessages) {
		        String roomName = getMoreMessages.getRoomName();
		        if (usersInRooms.get(roomName).contains(connection)) {
		            List<ChatMessage> allGroupMessages = roomMessages.getOrDefault(roomName, new ArrayList<>());
		            for (ChatMessage message : allGroupMessages) {
		                connection.sendTCP(message);
		            }
		        } else {
		            connection.sendTCP(new InfoMessage("You are not a member of the group: " + roomName));
		        }
		    }
			private void createRoom(String roomName,Connection user)
			{
				chatRooms.add(roomName);
				Set<Connection> members = new HashSet<>();
			    members.add(user);
				usersInRooms.put(roomName, members);
				
                System.out.println("Room " + roomName + " created.");
				
			}
			public void disconnected(Connection connection) {
				String user = connectionUserMap.get(connection);
				connectionUserMap.remove(connection);
				userConnectionMap.remove(user);
				showTextToAll(user+" has disconnected!", connection);
			}
		});
	}
	
	String[] getAllUsers() {
		String[] users = new String[userConnectionMap.size()];
		int i=0;
		for (String user: userConnectionMap.keySet()) {
			users[i] = user;
			i++;
		}
		
		return users;
	}
	String[] getAllRooms() {
		String[] rooms = new String[chatRooms.size()];
		int i=0;
		for (String room: chatRooms){
			rooms[i] = room;
			i++;
		}
		
		return rooms;
	}
	void newUserLogged(Login loginMessage, Connection conn) {
		userConnectionMap.put(loginMessage.getUserName(), conn);
		connectionUserMap.put(conn, loginMessage.getUserName());
		showTextToAll("User "+loginMessage.getUserName()+" has connected!", conn);
	}
	private void broadcastChatMessage(ChatMessage message, Connection exception) {
		for (Connection conn: userConnectionMap.values()) {
			if (conn.isConnected() && conn != exception)
				conn.sendTCP(message);
		}
	}
	private void showTextToAll(String txt, Connection exception) {
		System.out.println(txt);
		for (Connection conn: userConnectionMap.values()) {
			if (conn.isConnected() && conn != exception)
				conn.sendTCP(new InfoMessage(txt));
		}
	}
	public void start() throws IOException {
		server.start();
		server.bind(portNumber);
		
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	public void stop() {
		Thread stopThread = thread;
		thread = null;
		running = false;
		if (stopThread != null)
			stopThread.interrupt();
	}
	@Override
	public void run() {
		running = true;
		
		while(running) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) {
		
		if (args.length != 1) {
	        System.err.println("Usage: java -jar chatServer.jar <port number>");
	        System.out.println("Recommended port number is 54555");
	        System.exit(1);
	   }
	    
	   int portNumber = Integer.parseInt(args[0]);
	   try { 
		   ChatServer chatServer = new ChatServer(portNumber);
	   	   chatServer.start();
	   
			chatServer.thread.join();
	   } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	   } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }
	}
	
   
   
}
