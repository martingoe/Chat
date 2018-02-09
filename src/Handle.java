import java.io.IOException;
import java.net.Socket;

class Handle implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                Socket connection = Server.serverSocket.accept();
                Server.clients.add(connection);
                System.out.println("New Client");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}