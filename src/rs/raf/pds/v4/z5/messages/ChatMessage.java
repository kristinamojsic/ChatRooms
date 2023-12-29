package rs.raf.pds.v4.z5.messages;

public class ChatMessage {
	String user;
	String txt;
	//primalac moze biti jedan korisnik ili grupa
	String recipient;
	String roomName;
	boolean edited = false;
	
	
	protected ChatMessage() {
		
	}
	
	public void setEdited(boolean state)
	{
		edited = state;
	}
	public boolean getEdited()
	{
		return edited;
	}
	public ChatMessage(String user, String txt,String recipient) {
		this.user = user;
		this.txt = txt;
		this.recipient = recipient;
	}

	public String getRoomName()
	{
		return roomName;
	}
	
	public void setRoomName(String roomName)
	{
		this.roomName = roomName;
	}
	public String getRecipient() {
		return recipient;
	}
	public String getUser() {
		return user;
	}

	public String getTxt() {
		return txt;
	}
	
	public void setTxt(String txt)
	{
		this.txt = txt;
	}
	
}
