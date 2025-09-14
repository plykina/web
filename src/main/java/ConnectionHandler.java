import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler {
    // ЗАГОЛОВКИ И ТЕЛО ОТВЕТА ЗАХАРДКОДЕНЫ
    private static final String HTTP_HEADERS = "HTTP/1.1 200 OK\n" +
            "Date: Mon, 08 Sep 2025 11:08:55 +0200\n" +
            "HttpServer: Simple Webserver\n" +
            "Content-Length: 180\n" +
            "Content-Type: text/html\n"; // <- Важно: сейчас мы всегда возвращаем HTML

    private static final String HTTP_BODY = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\">\n" +
            "<title>Simple Http Server</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>Hi!</h1>\n" +
            "<p>This is a simple line in html.</p>\n" +
            "</body>\n" +
            "</html>\n" +
            "\n";

    private static final String HTTP_BODY_DOWNLOAD = "This is a text file for download" + "\n";

    private static final String HTTP_HEADERS_DOWNLOAD = "HTTP/1.1 200 OK\n" +
            "Date: Mon, 08 Sep 2025 11:08:55 +0200\n" +
            "HttpServer: Simple Webserver\n" +
            "Content-Length: " + HTTP_BODY_DOWNLOAD.length() + "\n" +
            "Content-Type: application/pdf\n" +
            "Content-Disposition: attachment; filename=download.txt\n";

    private static final String HTTP_BODY_JSON = "{\"success\": true, \"message\": \"Hello from server!\"}" + "\n";

    private static final String HTTP_HEADERS_JSON = "HTTP/1.1 200 OK\n" +
            "Date: Mon, 08 Sep 2025 11:08:55 +0200\n" +
            "HttpServer: Simple Webserver\n" +
            "Content-Length:" + HTTP_BODY_JSON.length() + "\n" +
            "Content-Type: application/json\n";

    private Socket socket;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
        handle(); // Сразу начинаем обработку
    }

    public void handle() {
        try {
            // ... код для создания потоков чтения и записи ...
            var inputStreamReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            var outputStreamWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));

            String root = parseRequest(inputStreamReader); // Читаем и выводим запрос в консоль
            writeResponse(outputStreamWriter, root); // Отправляем жестко заданный ответ

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Этот метод просто читает запрос и печатает его в консоль
    private String parseRequest(BufferedReader inputStreamReader) throws IOException {
        var request = inputStreamReader.readLine();
        if (request==null){
            return "/";
        }
        String[] root = request.split(" ");
        while (request != null && !request.isEmpty()) {
            System.out.println(request);
            request = inputStreamReader.readLine();
        }
        return root[1];
    }

    // Этот метод отправляет клиенту заранее подготовленные заголовки и тело
    private void writeResponse(BufferedWriter outputStreamWriter, String root) {
        try {
            switch (root){
                case "/json":
                    outputStreamWriter.write(HTTP_HEADERS_JSON);
                    outputStreamWriter.newLine(); // Пустая строка отделяет заголовки от тела
                    outputStreamWriter.write(HTTP_BODY_JSON);
                    break;
                case "/download":
                    outputStreamWriter.write(HTTP_HEADERS_DOWNLOAD);
                    outputStreamWriter.newLine(); // Пустая строка отделяет заголовки от тела
                    outputStreamWriter.write(HTTP_BODY_DOWNLOAD);
                    break;
                default:
                    outputStreamWriter.write(HTTP_HEADERS);
                    outputStreamWriter.newLine(); // Пустая строка отделяет заголовки от тела
                    outputStreamWriter.write(HTTP_BODY);
                    break;
            }

            outputStreamWriter.newLine();
            outputStreamWriter.flush(); // Отправляем буферизированные данные клиенту

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

