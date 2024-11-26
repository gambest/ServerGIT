package Domain.Data;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class Account {
    private UUID uuid;
    private String nickname;
    private String password;
}
