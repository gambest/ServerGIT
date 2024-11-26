package Domain.Itens;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RessonanceStone extends Item {
    private int quantity;
    private int energy;
    private int grainValue;
    private int minimumGaiaValue;
}
