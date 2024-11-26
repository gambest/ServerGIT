package Services;

import Domain.Data.Statistics;
import Repository.StatisticsRepository;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Log4j2
public class StatisticsService {
    public static Statistics createStatistics(UUID uuid){
        return StatisticsRepository.createStatistics(uuid);
    }

    public static void saveStatistics(Statistics statistics){
        StatisticsRepository.saveStatistics(statistics);
    }
}
