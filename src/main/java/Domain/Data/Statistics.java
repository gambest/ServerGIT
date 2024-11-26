package Domain.Data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
public class Statistics {
    private UUID uuid;
    private LocalDateTime timePlayed;
    private LocalDateTime creationDate;
    private int shots;
    private int inflictedDamage;
    private int receivedDamage;
    private int lifeHealed;
    private int totalShield;
    private int mitigatedDamage;
    private int wins;
    private int loses;
    private int completedMissions;
    private int usedPowerUps;
    private int critShots;
    private int sucessShots;
    private int missedShots;
    private int collectedItens;
    private int killedPlayers;
    private int killedEnemies;
    private int totalPVPMatches;
    private int totalPVEMatches;
}
