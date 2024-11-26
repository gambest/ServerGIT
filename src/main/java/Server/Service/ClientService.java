package Server.Service;

import Domain.Data.OnlinePlayers;
import Server.Repository.ServerRepository;
import Services.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Log4j2
public class ClientService extends Thread {
    private final Socket clientSocket;

    public ClientService(Socket socket) {
        clientSocket = socket;
    }

    public void run() {

        try {
            InputStream input = clientSocket.getInputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                String message = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                log.info("Received message: {} do socket {}", message, clientSocket);
                //TODO refactor to a switch case.
                if (message.startsWith("Login|")) {
                    message = message.replace("Login|Nick:", "");
                    boolean result = AccountService.login(clientSocket, message);
                    if (!result) {
                        log.info("Retornou {} ", result);
                        clientSocket.close();
                    }
                }
                if (message.startsWith("chatMessage|")) {
                    message = message.replace("chatMessage|", "");
                    String nickname = OnlinePlayersService.getNicknameBySocket(clientSocket);
                    ChatService.newMessage(nickname, message);
                }
                if (message.startsWith("roomSearch|")) {
                    message = message.replace("roomSearch|", "");
                    RoomService.getAndSendRooms(clientSocket, Integer.parseInt(message));
                }
                if (message.startsWith("roomCreate|")) {
                    message = message.replace("roomCreate|", "");
                    RoomService.roomCreate(clientSocket, message);//The message acts as a roomName.
                }
                if (message.startsWith("roomJoin|")) {
                    int roomId = Integer.parseInt(message.replace("roomJoin|", ""));
                    RoomService.roomJoin(roomId, clientSocket);
                }

                //resolver futuramente
                if (message.startsWith("roomLeft|")) {
                    int roomId = Integer.parseInt(message.replace("roomLeft|", ""));
                    RoomService.roomLeft(roomId, clientSocket);
                }
                //resolver futuramente
                if (message.startsWith("roomKicked|")) {
                    message = message.replace("roomKick|", "");
                    String nickname = message.substring(0,message.lastIndexOf("|"));
                    log.info("player kicked: {}", nickname);
                    int roomId = Integer.parseInt(message.replace(nickname+"|",""));
                    RoomService.roomKicked(roomId, nickname);
                }

                if (message.startsWith("roomSetReadiness|")) {
                    log.info("roomid: {}", message.replace("roomSetReadiness|", ""));
                    int roomId = Integer.parseInt(message.replace("roomSetReadiness|", ""));
                    RoomService.roomSetReadiness(roomId, clientSocket);
                }
                if (message.startsWith("roomSetFrame|")) {
                    message = message.replace("roomSetFrame|", "");
                    RoomService.roomSetFrame(message, clientSocket);
                }

                //resolver futuramente
                if (message.startsWith("roomSetLeader|")) {
                    message = message.replace("roomSetLeader|", "");
                    String nickname = message.substring(0,message.lastIndexOf("|"));
                    int roomId = Integer.parseInt(message.replace(nickname+"|",""));
                    log.info("New leader: {}", message.replace("roomSetLeader|", ""));
                    RoomService.roomSetLeader(roomId, nickname);
                }

                if (message.startsWith("roomMatchStart|")) {
                    message = message.replace("roomMatchStart|", "");
                    RoomService.roomGameStart(Integer.parseInt(message));
                }
                if (message.startsWith("matchSetTurns|")) {
                    //code made to set players turn based on vigor
                }
                if (message.startsWith("matchPlayerMoved|")) {
                    message = message.replace("matchPlayerMoved|", "");
                    MatchService.matchPlayerMoved(message);
                }
                if (message.startsWith("matchPlayerShooted|")) {
                    //this should run when a player shoots
                    //it need to validate the strength, angle, and position of player
                }
                if (message.startsWith("matchShootLanded|")) {
                    //should receive where the shoots landed to save in a log
                    //should compare with the results made in other clients
                }
                if (message.startsWith("gamePlayerHited|")) {
                    //should receive players who had hited to calculate damage
                }

            }

        } catch (IOException e) {
            String message = OnlinePlayersService.playerLeft(OnlinePlayers.getOnlinePlayers().get(clientSocket));
            log.info("Message sent: {}", message);
            List<Socket> playersList = OnlinePlayersService.getSocketList();
            ClientService.sendDataToAllPlayers(playersList, message);
            log.info("Cliente {} desconectado!", clientSocket);
        }
    }

    public static boolean sendDataToAPlayer(Socket clientSocket, String data) {
        return ServerRepository.sendDataToAPlayer(clientSocket, data);
    }

    public static boolean sendDataToAllPlayers(List<Socket> clientSockets, String data) {
        return ServerRepository.sendDataToAllPlayers(clientSockets, data);
    }
}