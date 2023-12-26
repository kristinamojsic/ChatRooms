package rs.raf.pds.v4.z5.messages;

public class InviteToChatRoom {
	private String username;
	private String roomname;
	
	public InviteToChatRoom(String roomname, String username) {
		
		this.username = username;
		this.roomname = roomname;
	}
	
	public InviteToChatRoom()
	{
		
	}
	public String getUsername() {
		return username;
	}

	public String getRoomname() {
		return roomname;
	}
	
	
}
