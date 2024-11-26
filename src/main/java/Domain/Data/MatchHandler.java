package Domain.Data;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class MatchHandler {
    private static Map<Integer, Match> matches = new ConcurrentHashMap<>();

    public static void addMatch(int roomId, Match match){
        matches.put(roomId, match);
    }
}
