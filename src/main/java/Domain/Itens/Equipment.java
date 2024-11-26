package Domain.Itens;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Equipment extends Item{
    private int baseAttack;
    private int baseDefense;
    private int baseAgility;
    private int baseLuck;
    private int currentAttack;
    private int currentDefense;
    private int currentAgility;
    private int currentLuck;
    private int ressonanceLevel;
    private int imbueLevel;
}
