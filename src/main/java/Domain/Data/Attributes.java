package Domain.Data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class Attributes {
    private UUID uuid;
    private int level;
    private int baseAttack;
    private int baseDefense;
    private int baseAgility;
    private int baseLuck;
    private int currentAttack;
    private int currentDefense;
    private int currentAgility;
    private int currentLuck;
    private int critRate;
    private int lifeSteal;
    private int armorPen;
    private int statusResist;

    private int maxHP;
    private int currentHP;
    private int vigor;
    private int currentVigor;

    private int currentRoom;
}
