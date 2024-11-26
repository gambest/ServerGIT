package Services;

import Domain.Data.Attributes;
import Domain.Data.Match;
import Domain.Data.MatchHandler;
import Domain.Data.Pygmy;
import Server.Service.ServerService;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public class MatchService {
    public static void newMatch(int roomId) {
        List<String> roomPygmyNameList = RoomService.getRoomPygmys(roomId);

        Map<UUID, Pygmy> pygmyMap = new HashMap<>();//*** ***

        Map<Pygmy, Attributes> pygmyAttributesMap = new HashMap<>();

        for (String pygmyName : roomPygmyNameList) {
            UUID uuid = PygmyService.findByName(pygmyName);
            Pygmy pygmy = Pygmy.builder().uuid(uuid).name(pygmyName).build();

            pygmyMap.put(uuid, pygmy);//*** ***

            Attributes attributes = AttributesService.getAttributeByUUID(uuid, roomId);

            pygmyAttributesMap.put(pygmy, attributes);
        }

        List<Pygmy> turnOrder = AttributesService.getTurnOrder(pygmyMap, roomId);

        Match match = Match.builder()
                .pygmyMap(pygmyMap)
                .turnOrder(turnOrder)
                .pygmyAttributesMap(pygmyAttributesMap)
                .build();
        MatchHandler.addMatch(roomId, match);
        log.info("A new match just started in room {}.", roomId);
        log.info("Players: {}. Turn order: {}.", pygmyMap, turnOrder);

        StringBuilder message = new StringBuilder("roomMatchStarted|");
        for (Pygmy pygmy : turnOrder) {
            message.append(pygmy.getName()).append("|");
        }
        RoomService.sendDataToARoom(roomId, message.toString());
    }

    public static void matchPlayerMoved(String message) {
        String[] parts = message.split("\\|");
        if (parts.length < 4) {
            log.error("Mensagem inválida: {}", message);
            return;
        }

        String roomId = parts[0];

        String nickname = parts[1];

        String newX = parts[2];

        String newY = parts[3];

        if (matchValidateMovement(roomId, nickname, newX, newY)) {
            message = "matchPlayerMoved" + "|" + nickname + "|" + newX + "|" + newY + "|";
            RoomService.sendDataToARoom(Integer.parseInt(roomId), message);
            return;
        }
        ServerService.handleCheater(nickname);
    }

    private static boolean matchValidateMovement(String roomId, String nickname, String newX, String newY) {
        return true;
    }
}
