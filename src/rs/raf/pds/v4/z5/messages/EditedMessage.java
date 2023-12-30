package rs.raf.pds.v4.z5.messages;

public class EditedMessage {
	String original;
	String edited;
	public EditedMessage()
	{
		
	};
	public EditedMessage(String original, String edited) {
		
		this.original = original;
		this.edited = edited;
	}
	public String getOriginal() {
		return original;
	}
	public void setOriginal(String original) {
		this.original = original;
	}
	public String getEdited() {
		return edited;
	}
	public void setEdited(String edited) {
		this.edited = edited;
	}
	
	
}
