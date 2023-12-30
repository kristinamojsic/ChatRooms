package rs.raf.pds.v4.z5.messages;

import com.esotericsoftware.kryo.Kryo;

public class KryoUtil {
	public static void registerKryoClasses(Kryo kryo) {
		kryo.register(String.class);
		kryo.register(String[].class);
		kryo.register(Login.class);
		kryo.register(ChatMessage.class);
		kryo.register(WhoRequest.class);
		kryo.register(ListUsers.class);
		kryo.register(InfoMessage.class);
		kryo.register(CreateRoom.class);
		kryo.register(ListRoomsRequest.class);
		kryo.register(ListRooms.class);
		kryo.register(InviteToChatRoom.class);
		kryo.register(JoinChatRoom.class);
		kryo.register(GetMoreMessages.class);
		kryo.register(EditedMessage.class);
	}
}
