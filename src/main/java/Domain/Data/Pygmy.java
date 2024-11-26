package Domain.Data;

import lombok.*;

import java.util.UUID;

@Data
@Builder
public class Pygmy {
    private UUID uuid;
    private String name;

    @Override
    public String toString() {
        return "Pygmy{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                '}';
    }
}
