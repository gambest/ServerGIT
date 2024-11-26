package Repository;

import Connection.ConnectionFactory;
import Domain.Data.Attributes;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
public class AttributesRepository {
    public static Attributes createAttributes(UUID uuid) {
        return Attributes.builder()
                .uuid(uuid)
                .build();
    }

    public static void saveAttributes(Attributes attributes) {

        String sql = "INSERT INTO public.Attributes (uuid) VALUES (?)";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, attributes.getUuid());

            int rowsAffected = statement.executeUpdate();
            log.info("Attributes sucessfully created! {} ", rowsAffected);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCurrentRoom(UUID uuid, int roomId) {
        String sql = "UPDATE attributes SET currentroom = ? WHERE uuid = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            statement.setObject(2, uuid);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Integer> getAttributeByUUID(UUID uuid) {
        List<Integer> attributesList = new ArrayList<>();
        String sql = "SELECT * FROM attributes WHERE uuid = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, uuid);

            // Executa a query
            ResultSet resultSet = statement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            resultSet.next();
            for (int i = 2; i < columnCount; i++) {
                attributesList.add(resultSet.getInt(i));
            }
            return attributesList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<UUID> getTurnOrder(int roomId) {
        List<UUID> pygmyList = new ArrayList<>();
        String sql = "SELECT uuid FROM attributes WHERE currentroom = ? ORDER BY maxVigor DESC";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);

            // Executa a query
            ResultSet resultSet = statement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                UUID result = (UUID) resultSet.getObject("uuid");
                pygmyList.add(result);
                log.info("Added {} to turn list", result);
            }
            return pygmyList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
