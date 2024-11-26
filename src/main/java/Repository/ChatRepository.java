package Repository;

import Domain.Data.Chat;

import java.util.List;

public class ChatRepository {
    public static void addChatMessage(String message){
        List<String> chatMessages = Chat.getChatMessages();
        chatMessages.add(message);
        Chat.setChatMessages(chatMessages);
    }
}
