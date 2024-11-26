package Services;

import Repository.ChatRepository;
import Server.Service.ClientService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ChatService {
    public static void newMessage(String nickname, String message){
        ChatRepository.addChatMessage(message);
        message = "chatMessage|" + nickname + ": "+ message;
        log.info(message);
        ClientService.sendDataToAllPlayers(OnlinePlayersService.getSocketList(),message);
    }
}
