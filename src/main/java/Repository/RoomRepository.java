package Repository;

import Connection.ConnectionFactory;

import Domain.Data.OnlinePlayers;
import Domain.Data.Room;
import Services.OnlinePlayersService;
import lombok.extern.log4j.Log4j2;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class RoomRepository {
    public static String roomListToStrings(List<Room> rooms) {
        StringBuilder message = new StringBuilder("roomSearch|");
        for (Room room : rooms) {
            message.append(room.getRoomId()).append("|").append(room.getRoomName()).append("|");
        }
        return message.toString();
    }

    public static List<Socket> pygmysToSocketList(List<String> pygmysList) {
        List<Socket> socketList = new ArrayList<>();
        Map<String, Socket> socketMap = OnlinePlayers.getPlayersSockets();
        for (String pygmy : pygmysList) {
            socketList.add(socketMap.get(pygmy));
        }
        return socketList;
    }

    //this method returns a total of 9 rooms.
    public static List<Room> getRooms(int page) {
        int offset = (page - 1) * 9;
        String sql = "SELECT id,name FROM public.rooms ORDER BY id OFFSET ? LIMIT 9";
        List<Room> rooms = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, offset);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Room room = Room.builder()
                        .roomId(result.getInt("id"))
                        .roomName(result.getString("name"))
                        .build();
                rooms.add(room);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rooms;
    }

    public static List<Room> getAllRooms() {
        String sql = "SELECT id,name FROM public.rooms ORDER BY id";
        List<Room> rooms = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Room room = Room.builder()
                        .roomId(result.getInt("id"))
                        .roomName(result.getString("name"))
                        .build();
                rooms.add(room);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rooms;
    }

    public static List<Integer> getUsedFrames(int roomId) {
        String sql = "SELECT frame FROM roomplayers WHERE roomid = ?";
        List<Integer> framesList = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                framesList.add(resultSet.getInt("frame"));
            }
            return framesList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRoomNameById(int roomId) {
        String sql = "SELECT name FROM rooms WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);

            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            return resultSet.getString("name");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getRoomPygmys(int roomId) {

        String sql = "SELECT pygmyname FROM roomplayers WHERE roomid = ?";
        List<String> namesList = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, roomId);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                namesList.add(result.getString("pygmyname"));
            }
            return namesList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean getPygmyReadiness(int roomId, String pygmy) {
        String sql = "SELECT readiness FROM roomplayers WHERE roomid = ? AND pygmyname = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            statement.setString(2, pygmy);

            ResultSet resultSet = statement.executeQuery();
            log.info("Returning readiness for the pygmy {}.", pygmy);
            resultSet.next();
            return resultSet.getBoolean("readiness");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void roomJoin(int roomId, Socket clientSocket) {

        String sql = "INSERT INTO roomplayers (roomid,pygmyname,readiness) VALUES (?,?,?)";

        String pygmy = OnlinePlayersService.getNicknameBySocket(clientSocket);

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            statement.setString(2, pygmy);
            statement.setBoolean(3, false);
            // Executa a query
            int rowsAffected = statement.executeUpdate();
            if (!(rowsAffected > 0)) {
                log.info("Update Failed!!!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void roomCreate(int roomId, String roomName, String roomPswd) {

        String sql = "INSERT INTO rooms (id,name,password) VALUES (?,?,?)";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            statement.setString(2, roomName);
            statement.setInt(3, roomPswd.hashCode());
            // Executa a query
            int rowsAffected = statement.executeUpdate();
            if (!(rowsAffected > 0)) {
                log.info("Room creation has failed!!!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void roomLeft(int roomId, Socket clientSocket) {

        String sql = "DELETE FROM roomplayers WHERE roomid = ? AND pygmyname = ?";

        String pygmy = OnlinePlayersService.getNicknameBySocket(clientSocket);

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            statement.setString(2, pygmy);
            // Executa a query
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Room updated!!!");
            } else {
                log.info("Update Failed!!!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void roomKicked(int roomId, String nickname) {

        String sql = "DELETE FROM roomplayers WHERE roomid = ? AND pygmyname = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            statement.setString(2, nickname);
            // Executa a query
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Room updated!!!");
            } else {
                log.info("Update Failed!!!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void roomDelete(int roomId) {
        String sql = "DELETE FROM rooms WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);

            // Executa a query
            int rowsAffected = statement.executeUpdate();
            if (!(rowsAffected > 0)) {
                log.info("Error when deleting room!!!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean roomSetReadiness(int roomId, Socket clientSocket) {
        String pygmy = OnlinePlayersService.getNicknameBySocket(clientSocket);
        boolean readiness = getPygmyReadiness(roomId, pygmy);

        String sql = "UPDATE roomplayers SET readiness = ? WHERE roomid = ? and pygmyname = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, !readiness);
            statement.setInt(2, roomId);
            statement.setString(3, pygmy);
            // Executa a query
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Room updated!!!");
            } else {
                log.info("Update Failed!!!");
            }
            return !readiness;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void roomSetFrame(String nickname, String nextFrame) {
        String sql = "UPDATE roomplayers SET frame = ? WHERE pygmyname = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Integer.parseInt(nextFrame));
            statement.setString(2, nickname);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void roomSetLeader(int roomId, String nickname) {
        String sql = "UPDATE roomplayers SET leader = CASE WHEN pygmyname = ? THEN true ELSE false END WHERE roomid = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nickname);
            statement.setInt(2, roomId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getRoomIdByPygmy(String nickname) {
        String sql = "SELECT roomid FROM roomplayers WHERE pygmyname = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nickname);

            log.info("Looking for room id related to pygmy {}.", nickname);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("roomid");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //TODO include new columns in rooms and roomplayers to have running field and roomtype
    //new methods below
    public static Map<String, Boolean> roomGetReadiness(int roomId) {
        Map<String, Boolean> readinessMap = new ConcurrentHashMap<>();
        String sql = "SELECT readiness, pygmyname FROM roomplayers WHERE roomid = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);

            // Executa a query
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                readinessMap.put(resultSet.getString("pygmyname"), resultSet.getBoolean("readiness"));
            }
            return readinessMap;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getType(int roomId) {
        String sql = "SELECT type FROM rooms WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);

            ResultSet resultSet = statement.executeQuery();
            log.info("Returning room type for room {}.", roomId);
            resultSet.next();
            return resultSet.getString("type");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void roomGameStart(int roomId) {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnections();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "playing");
            statement.setInt(2, roomId);

            statement.executeUpdate();
            log.info("Game in room {} is now running...", roomId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
