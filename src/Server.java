import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Server {
    public static ArrayList<Socket> clients = new ArrayList<>();
    public static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(5555);

            System.out.println("Started Server.");

            JFrame frame = new JFrame("Server");
            JButton stopServer = new JButton("Stop Server");
            stopServer.addActionListener(e -> System.exit(0));

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(200, 100);
            frame.setLocationRelativeTo(null);
            frame.add(stopServer);
            frame.setVisible(true);

            new Thread(new Handle()).start();
            while (true) {
                System.out.println(Arrays.toString(clients.toArray()));
                clients.forEach(socket -> {
                    System.out.println(socket.getLocalAddress());
                    String s;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        if(reader.ready()){
                            s = reader.readLine();
                            System.out.println("new Message: " + s);

                            clients.forEach(socket1 -> {

                                try {
                                    PrintWriter writer = new PrintWriter(socket1.getOutputStream());
                                    writer.write(s + "\n");
                                    writer.flush();

                                    System.out.println("Sent " + s + "back.");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        else {
                            System.out.println("Nothing to read.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
