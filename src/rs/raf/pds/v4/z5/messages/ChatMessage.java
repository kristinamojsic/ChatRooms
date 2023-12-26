package rs.raf.pds.v4.z5.messages;

public class ChatMessage {
	String user;
	String txt;
	//primalac moze biti jedan korisnik ili grupa
	String recipient;
	
	
	protected ChatMessage() {
		
	}
	public ChatMessage(String user, String txt,String recipient) {
		this.user = user;
		this.txt = txt;
		this.recipient = recipient;
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
	
	
}
