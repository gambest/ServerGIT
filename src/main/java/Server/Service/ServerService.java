package Server.Service;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

@Log4j2
public class ServerService {
    public static void main(String[] args) {
        int port = 8080; // Defina a porta que você deseja utilizar

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor iniciado na porta " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                LocalDateTime time = LocalDateTime.now();
                int month = time.getMonthValue();
                int day = time.getDayOfMonth();
                int hour = time.getHour();
                int minutes = time.getMinute();
                int seconds = time.getSecond();

                String timeString = " Day:" + month + "/" + day + " Hour:" + hour + ":" + minutes + ":" + seconds;

                log.info("Nova tentativa de conexão " + clientSocket.getInetAddress() + timeString);

                // Cria uma nova thread para lidar com o cliente
                new ClientService(clientSocket).start();
            }

        } catch (IOException e) {
            System.out.println("Problema ao criar socket!!!");
        }
    }

    public static void handleCheater(String nickname) {
        log.info("Inconsistent info sent from {}. Please investigate.", nickname);
    }
}
