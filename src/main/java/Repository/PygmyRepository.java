package Repository;

import Connection.ConnectionFactory;
import Domain.Data.Pygmy;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
public class PygmyRepository {
    public static Pygmy createPygmy(UUID uuid, String name) {
        return Pygmy.builder()
                .uuid(uuid)
                .name(name)
                .build();
    }

    public static void save(Pygmy pygmy) {
        UUID uuid = pygmy.getUuid();
        String name = pygmy.getName();

        String sql = "INSERT INTO public.pygmy (uuid, name) VALUES (?, ?)";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, uuid);           // Vincula o UUID no primeiro placeholder (?)
            statement.setString(2, name);           // Vincula o nome no segundo placeholder (?)

            statement.executeUpdate();
            log.info("Pygmy sucessfully created!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(Pygmy pygmy) {
        String name = pygmy.getName();

        String sql = "DELETE FROM Pygmy WHERE name = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);

            // Executa a query
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Pygmy sucessfully deleted!{} ", rowsAffected);
            } else {
                log.info("Pygmy not found!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void update(Pygmy pygmy) {
        UUID uuid = pygmy.getUuid();
        String name = pygmy.getName();

        String sql = "UPDATE Pygmy SET name = ? WHERE uuid = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setObject(2, uuid);
            // Executa a query
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Pygmys updated!!!");
            } else {
                log.info("No Pygmy to be returned.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Pygmy> findAll() {
        log.info("Looking for all Pygmys...");

        String sql = "SELECT uuid,name FROM pygmy;";
        List<Pygmy> pygmies = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                Pygmy pygmy = Pygmy.builder()
                        .uuid((UUID) result.getObject("uuid"))
                        .name(result.getString("name"))
                        .build();
                pygmies.add(pygmy);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pygmies;
    }

    public static UUID findByName(String name) {

        String sql = "SELECT uuid FROM pygmy WHERE name = ?";
        UUID uuid;

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                uuid = (UUID) (result.getObject("uuid"));
            } else {
                uuid = null;
            }
            return uuid;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
