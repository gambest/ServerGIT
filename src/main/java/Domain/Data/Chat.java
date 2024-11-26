package Domain.Data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Data
public class Chat {
    @Getter
    @Setter
    private static List<String> chatMessages = new ArrayList<>();
}
