package Server.Repository;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Log4j2
public class ServerRepository {
    public static boolean sendDataToAPlayer(Socket clientSocket, String data) {
        try {
            OutputStream output = clientSocket.getOutputStream();
            // Envia a mensagem ao cliente
            byte[] message = data.getBytes(StandardCharsets.UTF_8);
            output.write(message);
            log.info("Mensagem enviada: {}", data);
            return true;
        } catch (IOException e) {
            log.error("Erro ao enviar dados para algum cliente {}", clientSocket);
            return false;
        }
    }

    public static boolean sendDataToAllPlayers(List<Socket> clientSockets, String data) {
        for (Socket clientSocket : clientSockets) {
            try {
                OutputStream output = clientSocket.getOutputStream();
                // Envia a mensagem ao cliente
                byte[] message = data.getBytes(StandardCharsets.UTF_8); // Converte a mensagem para um array de bytes
                output.write(message);
            } catch (IOException e) {
                log.error("Erro ao enviar dados para algum cliente.");
                return false;
            }
        }
        log.info("Mensagem enviada: {}", data);
        return true;

    }
}
