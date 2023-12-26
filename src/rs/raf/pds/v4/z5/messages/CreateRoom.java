package rs.raf.pds.v4.z5.messages;



public class CreateRoom {
	private String roomName;
	
	public CreateRoom()
	{
		
	}
	public CreateRoom(String roomName)
	{
		this.roomName=roomName;
		
	}
	public String getRoomName()
	{
		return roomName;
	}
	
}
