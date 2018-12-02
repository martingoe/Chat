import java.io.IOException;
import java.net.Socket;

class Handle implements Runnable {

    @Override
    public void run() {
        //Constantly run the logic in the loop
        while (true) {
            try {
                //Accept any Sockets connecting to the Server
                Socket connection = Server.serverSocket.accept();
                //Add the new client to the clients ArrayList in Server
                Server.clients.add(connection);

                System.out.println("New Client");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}