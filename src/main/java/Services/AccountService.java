package Services;

import Domain.Data.Account;
import Repository.AccountRepository;
import Server.Service.ClientService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.mindrot.jbcrypt.BCrypt;

import java.net.Socket;
import java.util.UUID;

@Log4j2
public class AccountService {
    public static Account createAccount(UUID uuid, String nickname, String password){
        return AccountRepository.createAccount(uuid, nickname, password);
    }

    public static void saveAccount(Account account){
        AccountRepository.saveAccount(account);
    }

    @SneakyThrows
    public static boolean login(Socket clientSocket, String message){
        String nickname = message.substring(0, message.lastIndexOf("Password:"));
        String password = message.replace(nickname + "Password:", "");
        String hashedPassword = AccountRepository.findHashedPassword(nickname);
        if (BCrypt.checkpw(password, hashedPassword)){
            ClientService.sendDataToAPlayer(clientSocket, "loginDone|");
            Thread.sleep(1000);
            OnlinePlayersService.sendAllPlayers(clientSocket);
            Thread.sleep(1000);
            OnlinePlayersService.addPlayerToOnlineList(clientSocket, nickname, PygmyService.findByName(nickname));
            return true;
        } else{
            ClientService.sendDataToAPlayer(clientSocket, "loginFailed|");
            return false;
        }
    }
}
