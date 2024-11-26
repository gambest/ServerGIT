package Domain.Data;

import lombok.Data;
import lombok.Getter;

import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class OnlinePlayers {
    @Getter
    private static Map<Socket, String> onlinePlayers = new ConcurrentHashMap<>();
    @Getter
    private static Map<String, Socket> playersSockets = new ConcurrentHashMap<>();
    @Getter
    private static Map<Socket, UUID> socketUuidMap = new ConcurrentHashMap<>();
}
