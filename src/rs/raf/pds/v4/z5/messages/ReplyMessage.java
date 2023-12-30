package rs.raf.pds.v4.z5.messages;

public class ReplyMessage {

	String originalMessage;
	String reply;
	public ReplyMessage(String originalMessage, String reply) {
		super();
		this.originalMessage = originalMessage;
		this.reply = reply;
	}
	public String getOriginalMessage() {
		return originalMessage;
	}
	public void setOriginalMessage(String originalMessage) {
		this.originalMessage = originalMessage;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	
	
}
