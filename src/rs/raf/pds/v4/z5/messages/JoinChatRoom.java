package rs.raf.pds.v4.z5.messages;

public class JoinChatRoom {
	private String roomName;
	public JoinChatRoom()
	{
		
	}
	public JoinChatRoom(String roomName)
	{
		this.roomName = roomName;
	}
	public String getRoomName()
	{
		return roomName;
	}
}
