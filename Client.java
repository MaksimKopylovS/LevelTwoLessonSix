
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

class Client {

    private Socket socketClient;
    private String serverAdress = "127.0.0.1";
    private int serverPort = 8189;
    private BufferedReader reader;
    private BufferedReader in;
    private BufferedWriter out;

    public void startClient() throws ConnectException {

        try {
            //Подключение к адресу и прослушивание порта
            socketClient = new Socket(serverAdress, serverPort);
            System.out.println("OK Connect \nWrite to Exit exit");
        } catch (IOException ioException) {
            System.out.println("Disconnect");
            System.exit(0);
        }


        new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {
                    try {
                        System.out.println("Write Message");
                        //Буффер считывающий сообщение из консоли
                        reader = new BufferedReader(new InputStreamReader(System.in));
                        //Перенос написанного сообщения из консоли в строку
                        String word = reader.readLine();
                        if (word.equals("exit")) {
                            throw new InterruptedException();
                        } else {
                            //Подготовка сообщения для отправки клиенту
                            out.write("Client write: " + word + "\n");
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

        new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {
                    try {
                        //Поток чтения входящего сообщение из сокета(от сервера)
                        in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                        //Поток записи исходящего сообщения в сокет для отправки на сервер
                        out = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
                        //Перевод входящего потока в строку
                        String serverWord = in.readLine();
                        //отображение строки в консоль
                        System.out.println(serverWord);
                    } catch (IOException e) {
                        //e.printStackTrace();
                        System.out.println("Disconnect");
                        System.exit(0);
                    }
                }
            }
        }).start();

    }
	
	public static void main(String argv[]) throws ConnectException {
		new Client().startClient();
	}
}
