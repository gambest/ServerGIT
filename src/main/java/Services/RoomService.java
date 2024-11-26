package Services;

import Domain.Data.Room;
import Repository.RoomRepository;
import Server.Service.ClientService;
import lombok.extern.log4j.Log4j2;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Log4j2
public class RoomService {
    public static void getAndSendRooms(Socket clientSocket, int page) {
        List<Room> roomList = RoomRepository.getRooms(page);
        String message = RoomRepository.roomListToStrings(roomList);
        ClientService.sendDataToAPlayer(clientSocket, message);
    }

    public static List<String> getRoomPygmys(int roomId) {
        List<String> pygmysList = new ArrayList<>();
        return RoomRepository.getRoomPygmys(roomId);
    }

    public static String getRoomNameById(int roomId) {
        return RoomRepository.getRoomNameById(roomId);
    }

    private static int getAAvailableRoom() {
        List<Room> allRooms = RoomRepository.getAllRooms();
        if (allRooms.isEmpty()) return 1;
        return allRooms.get(allRooms.size()-1).getRoomId() + 1;
    }

    private static int getAAvailabeFrame(int roomId) {
        List<Integer> frames = new ArrayList<>(List.of(1, 2, 3, 4));
        List<Integer> usedFrames = RoomRepository.getUsedFrames(roomId);
        frames.removeAll(usedFrames);

        if (frames.isEmpty()) {
            return -1;
        }
        return frames.get(0);
    }

    public static void sendDataToARoom(int roomId, String message) {
        List<String> pygmysList = RoomRepository.getRoomPygmys(roomId);
        List<Socket> socketList = OnlinePlayersService.pygmysToSocketList(pygmysList);
        ClientService.sendDataToAllPlayers(socketList, message);
    }

    public static void roomJoin(int roomId, Socket clientSocket) {
        RoomRepository.roomJoin(roomId, clientSocket);

        UUID uuid = OnlinePlayersService.getUUIDBySocket(clientSocket);
        AttributesService.setCurrentRoom(uuid,roomId);

        String pygmy = OnlinePlayersService.getNicknameBySocket(clientSocket);
        String roomName = getRoomNameById(roomId);
        String roomFrame = Integer.toString(getAAvailabeFrame(roomId));
        String nickname = OnlinePlayersService.getNicknameBySocket(clientSocket);

        RoomRepository.roomSetFrame(nickname, "1");
        String message = "roomJoined|" + pygmy + "|" + roomId + "|" + roomName + "|" + roomFrame + "|";
        sendDataToARoom(roomId, message);
    }

    public static void roomLeft(int roomId, Socket clientSocket) {
        RoomRepository.roomLeft(roomId, clientSocket);

        List<String> roomPygmys = getRoomPygmys(roomId);

        UUID uuid = OnlinePlayersService.getUUIDBySocket(clientSocket);
        AttributesService.setCurrentRoom(uuid,0);

        String player = OnlinePlayersService.getNicknameBySocket(clientSocket);
        sendDataToARoom(roomId, "roomLeft|" + player);

        if (roomPygmys.isEmpty()) {
            roomDelete(roomId);
        }
    }

    public static void roomCreate(Socket clientSocket, String message) {
        int roomId = getAAvailableRoom();
        String roomName = message.substring(0, message.lastIndexOf("roomPswd:"));
        String roomPswd = message.replace(roomName + "roomPswd:", "");
        RoomRepository.roomCreate(roomId, roomName, roomPswd);
        roomJoin(roomId, clientSocket);
        roomSetLeader(roomId, OnlinePlayersService.getNicknameBySocket(clientSocket));
    }

    public static void roomDelete(int roomId) {
        RoomRepository.roomDelete(roomId);
    }

    public static void roomKicked(int roomId, String nickname) {
        RoomRepository.roomKicked(roomId, nickname);
        sendDataToARoom(roomId, "roomKicked|" + nickname);
    }

    public static void roomSetReadiness(int roomId, Socket clientSocket) {
        boolean readiness = RoomRepository.roomSetReadiness(roomId, clientSocket);
        String pygmy = OnlinePlayersService.getNicknameBySocket(clientSocket);
        ClientService.sendDataToAllPlayers(OnlinePlayersService.getSocketList(), "roomSetReadiness|" + pygmy + "|" + readiness + "|");
        //precisa enviar o player e o estado para todos que estão na sala
    }

    public static void roomSetFrame(String message, Socket clientSocket) {
        String nickname = message.substring(0, message.indexOf("|"));
        log.info("Nickname: {}", nickname);

        String oldFrame = message.replace(nickname + "|", "");
        log.info("oldFrame 1: {}", oldFrame);
        oldFrame = oldFrame.substring(0, oldFrame.indexOf("|"));
        String replacement = oldFrame + "|";
        log.info("Replacing: {}", replacement);

        String nextFrame = message.replace(nickname + "|" + replacement, "");
        log.info("nextFrame: {}", nextFrame);


        log.info("Updating: player {} goes from frame {} to frame {}.", nickname, oldFrame,nextFrame);
        RoomRepository.roomSetFrame(nickname, nextFrame);
        RoomService.sendDataToARoom(getRoomIdByPygmy(nickname), "roomSetFrame|" + nickname + "|" + oldFrame + "|" + nextFrame + "|");
    }

    public static void roomSetLeader(int roomId, String nickname) {
        RoomRepository.roomSetLeader(roomId, nickname);
        sendDataToARoom(roomId, "roomSetLeader|"+ nickname + "|");
    }

    private static int getRoomIdByPygmy(String nickname) {
        return RoomRepository.getRoomIdByPygmy(nickname);
    }
    //new methods below
    public static void roomGameStart(int roomId) {
        if (roomGameCanStart(roomId)) {
            RoomRepository.roomGameStart(roomId);
            MatchService.newMatch(roomId);
        }
    }

    private static boolean roomGameCanStart(int roomId) {
        Map<String, Boolean> playersReadiness = RoomRepository.roomGetReadiness(roomId);
        int roomPlayersNumber = playersReadiness.size();
        if (RoomRepository.getType(roomId).equals("PVPROOM")){
            if (roomPlayersNumber == 2 || roomPlayersNumber == 4) {
                log.info("Starting a PVP game...");
                return roomCheckReadiness(playersReadiness);
            }
            log.info("Cant start now.");
            return false;
        }
        log.info("Starting a PVE game...");
        return roomCheckReadiness(playersReadiness);
    }

    public static boolean roomCheckReadiness(Map<String,Boolean> playersReadiness){
        List<String> playersList = new ArrayList<>(playersReadiness.keySet());
        int playersReady = 0;
        for (String player:playersList){
            if (playersReadiness.get(player)){
                playersReady += 1;
            }
        }
        return (playersReady == playersList.size() - 1);
    }
}
