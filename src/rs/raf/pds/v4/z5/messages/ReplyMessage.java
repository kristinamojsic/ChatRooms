package rs.raf.pds.v4.z5.messages;

public class ReplyMessage {

	String messageRepliedTo;
	String reply;
	
	public ReplyMessage()
	{
		
	}
	public ReplyMessage(String originalMessage, String reply) {
		super();
		this.messageRepliedTo = originalMessage;
		this.reply = reply;
	}
	public String getmessageRepliedTo() {
		return messageRepliedTo;
	}
	public void setmessageRepliedTo(String originalMessage) {
		this.messageRepliedTo = originalMessage;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	
	
}
