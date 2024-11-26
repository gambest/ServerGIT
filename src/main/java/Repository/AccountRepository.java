package Repository;

import Connection.ConnectionFactory;
import Domain.Data.Account;
import lombok.extern.log4j.Log4j2;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Log4j2
public class AccountRepository {
    public static Account createAccount(UUID uuid, String nickname,String password){
        return Account.builder()
                .uuid(uuid)
                .nickname(nickname)
                .password(password)
                .build();
    }

    public static void saveAccount(Account account){
        UUID uuid = account.getUuid();
        String nickname = account.getNickname();
        String password = account.getPassword();

        String sql = "INSERT INTO public.account (uuid, nickname, password) VALUES (?, ?, ?)";

        try(Connection connection = ConnectionFactory.getConnections();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setObject(1, uuid);
            statement.setString(2, nickname);
            statement.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));

            int rowsAffected = statement.executeUpdate();
            log.info("Account successfully created!{} ",rowsAffected);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String findHashedPassword(String nickname){
        String sql = "SELECT * FROM public.account WHERE nickname = ?";

        try(Connection connection = ConnectionFactory.getConnections();
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setString(1, nickname);
            ResultSet result = statement.executeQuery();

            if (!result.isBeforeFirst()) return null;
            result.next();
            return result.getString("password");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}