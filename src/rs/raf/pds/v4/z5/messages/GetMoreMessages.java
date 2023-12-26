package rs.raf.pds.v4.z5.messages;

public class GetMoreMessages {
	private String roomName;
	 public GetMoreMessages() {
	    }

	 public GetMoreMessages(String roomName) {
	        this.roomName = roomName;
	    }

	 public String getRoomName() {
	        return roomName;
	    }
}
