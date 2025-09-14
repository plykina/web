public class Main {
    public static void main(String[] args) {
        // Создаем экземпляр нашего сервера, указывая ему слушать порт 8585
        new HttpServer(8585).startServer();
    }
}
