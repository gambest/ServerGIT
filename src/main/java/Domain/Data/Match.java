package Domain.Data;

import lombok.Builder;

import java.util.*;

@Builder
public class Match {
    private Map<UUID, Pygmy> pygmyMap;//*** ***
    private List<Pygmy> turnOrder = new ArrayList<>();
    private Map<Pygmy, Attributes> pygmyAttributesMap = new HashMap<>();
    private int turn = 0;
}
