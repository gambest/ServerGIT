package Domain.Itens;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Item {
    private UUID uuid;
    private LocalDateTime creationTime;
    private String origin;
    private String id;
    private String name;
    private String rarity;
    private String type;
    private String description;
    private boolean equipable;
    private boolean stackable;
    private boolean fusible;
    private boolean auctionable;
    private int grainValue;
    private int minimumGaiaValue;
}
