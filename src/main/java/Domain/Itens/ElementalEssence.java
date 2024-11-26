package Domain.Itens;

import lombok.Data;

@Data
public class ElementalEssence extends Item{
    private int quantity;
    private int energy;
    private int grainValue;
    private int minimumGaiaValue;
}
