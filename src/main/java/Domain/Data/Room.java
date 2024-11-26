package Domain.Data;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Room {
    private int roomId;
    private String roomName;
    private Map<String, Boolean> playersInRoom;
}
