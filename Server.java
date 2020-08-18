import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

class Server {

    private static final int PORT = 8189;
    private Socket socket;
    private ServerSocket serverSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader reader;

    public Server() throws ConnectException {
        //В данном блоке открывается прослушивание порта, пока клиент не подключится порт будет открыт
        try {
            //Прослушивание порта
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server OK Connect \nWrite to Exit exit");
            //Ожиадние подключение клиента
            socket = serverSocket.accept();
            System.out.println("Client connect");
            sendMessage();
            readMessage();
        } catch (IOException ioException) {
            System.out.println("Disconnect");
        }
    }

        public void sendMessage(){
            //Поток для отправки сообщения клиенту
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        //Буфер считывающий ввод с консоли(сервера)
                        reader = new BufferedReader(new InputStreamReader(System.in));
                        System.out.println("Write Message");
                        try {
                            //Перенос написанного сообщения из консоли в строку
                            String word = reader.readLine();
                            if (word.equals("exit")) {
                                throw new InterruptedException();
                            } else {
                                //Поток записи исходящего сообщения в сокет для отправки клиенту
                                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                //Подготовка сообщения для отправки клиенту
                                out.write("Server write: " + word + "\n");
                                //Выталкивает содержимое буфера в поток
                                out.flush();
                            }
                        } catch (IOException | InterruptedException ioException) {
                            System.out.println("Disconnect");
                            System.exit(0);
                        }
                    }
                }
            }).start();
        }

        public void readMessage(){
            //Поток для приёма сообщений от клиента
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (true) {
                        try {
                            //Запись в буфер потока передаваемого из сокета клиента
                            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            //Перевод входящего поток в строку
                            String clientWord = in.readLine();
                            System.out.println(clientWord);
                        } catch (IOException e) {
                            System.out.println("Disconnect");
                            System.exit(0);
                        }
                    }
                }
            }).start();
        }
    public static void main(String[] args) throws ConnectException {
        new Server();
    }


}
