package Services;

import Domain.Data.OnlinePlayers;
import Repository.OnlinePlayersRepository;
import Repository.RoomRepository;
import Server.Service.ClientService;
import lombok.extern.log4j.Log4j2;

import java.net.Socket;
import java.util.List;
import java.util.UUID;

@Log4j2
public class OnlinePlayersService {
    public static List<Socket> getSocketList(){
        return OnlinePlayersRepository.getSocketList();
    }

    public static List<String> getNicknamesList(){
        return OnlinePlayersRepository.getNicknamesList();
    }

    public static String getNicknameBySocket(Socket clientSocket){
        return OnlinePlayersRepository.getNicknameBySocket(clientSocket);
    }

    public static void addPlayerToOnlineList(Socket clientSocket, String nickname, UUID uuid){
        OnlinePlayersRepository.addPlayerToOnlineList(clientSocket, nickname, uuid);
        List<Socket> clientSockets = OnlinePlayersService.getSocketList();
        ClientService.sendDataToAllPlayers(clientSockets, playerJoined(nickname));
    }

    public static String playerJoined(String nickname){
        return OnlinePlayersRepository.playerJoined(nickname);
    }

    public static String playerLeft(String nickname){
        OnlinePlayersRepository.removePlayerOfOnlineList(nickname);
        return OnlinePlayersRepository.playerLeft(nickname);
    }

    public static void sendAllPlayers(Socket clientSocket){
        //This method will send all online players to a Player.
        List<String> socketList = getNicknamesList();
        StringBuilder message = new StringBuilder("playersOnline|");
        for (String s : socketList) {
            message.append(s).append("|");
            log.info("Sent player {} to the socket {}.", s, clientSocket);
        }
        ClientService.sendDataToAPlayer(clientSocket, message.toString());
    }

    public static List<Socket> pygmysToSocketList(List<String> pygmysList) {
        return RoomRepository.pygmysToSocketList(pygmysList);
    }

    public static UUID getUUIDBySocket(Socket clientSocket) {
        return OnlinePlayers.getSocketUuidMap().get(clientSocket);
    }
}
