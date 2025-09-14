import java.io.IOException;
import java.net.ServerSocket;

public class HttpServer {
    private int tcpPort; // Порт, который будет слушать сервер

    public HttpServer(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public void startServer() {
        try (var serverSocket = new ServerSocket(this.tcpPort)) {
            System.out.println("Server accepting requests on port " + tcpPort);

            // Бесконечный цикл для приема нескольких клиентов
            while (true) {
                // Ожидаем подключения клиента. Метод .accept() блокирует выполнение.
                var acceptedSocket = serverSocket.accept();
                // Для каждого клиента создаем свой обработчик
                new ConnectionHandler(acceptedSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
