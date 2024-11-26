package Repository;

import Domain.Data.OnlinePlayers;
import lombok.extern.log4j.Log4j2;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Log4j2
public class OnlinePlayersRepository {
    public static List<Socket> getSocketList(){
        return new ArrayList<>(OnlinePlayers.getOnlinePlayers().keySet());
    }

    public static List<String> getNicknamesList(){
        return new ArrayList<>(OnlinePlayers.getOnlinePlayers().values());
    }

    public static String getNicknameBySocket(Socket clientSocket){
        Map<Socket, String> onlinePlayers = OnlinePlayers.getOnlinePlayers();
        return onlinePlayers.get(clientSocket);
    }

    public static void addPlayerToOnlineList(Socket clientSocket, String nickname, UUID uuid){
        Map<Socket, String> onlinePlayers = OnlinePlayers.getOnlinePlayers();
        Map<String, Socket> playersSockets = OnlinePlayers.getPlayersSockets();
        Map<Socket, UUID> socketUuidMap = OnlinePlayers.getSocketUuidMap();
        onlinePlayers.put(clientSocket, nickname);
        playersSockets.put(nickname, clientSocket);
        socketUuidMap.put(clientSocket,uuid);
    }

    public static void removePlayerOfOnlineList(String nickname) {
        Map<Socket, String> onlinePlayers = OnlinePlayers.getOnlinePlayers();
        Map<String, Socket> playersSockets = OnlinePlayers.getPlayersSockets();
        Socket clientSocket = playersSockets.get(nickname);
        log.info("Player {} disconected", nickname);
        onlinePlayers.remove(clientSocket, nickname);
        playersSockets.remove(nickname, clientSocket);
    }

    public static String playerJoined(String nickname){
        return "playerJoined|" + nickname;
    }

    public static String playerLeft(String nickname){
        return "playerLeft|" + nickname;
    }

}
