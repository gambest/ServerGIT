package Repository;

import Connection.ConnectionFactory;
import Domain.Data.Statistics;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Log4j2
public class StatisticsRepository {
    public static Statistics createStatistics(UUID uuid){
        return Statistics.builder()
                .uuid(uuid)
                .build();
    }

    public static void saveStatistics(Statistics statistics){

        String sql = "INSERT INTO public.statistics (uuid) VALUES (?)";

        try(Connection connection = ConnectionFactory.getConnections();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setObject(1,statistics.getUuid());

            int rowsAffected = statement.executeUpdate();
            log.info("Statistics sucessfully created! {} ",rowsAffected);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
