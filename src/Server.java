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

            //All the UI
            JFrame frame = new JFrame("Server");
            JButton stopServer = new JButton("Stop Server");
            stopServer.addActionListener(e -> System.exit(0));

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(200, 100);
            frame.setLocationRelativeTo(null);
            frame.add(stopServer);
            frame.setVisible(true);

            //Handles  new users
            new Thread(new Handle()).start();

            //Constantly checks for messages and sends them to everyone if there are some
            while (true) {
                Socket[] clientsArr = clients.toArray(new Socket[0]);
                for(Socket socket : clientsArr){
                    String s;
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        if(reader.ready()){
                            s = reader.readLine();

                            //Send back to everyone
                            for(Socket socket1 : clientsArr){

                                try {
                                    PrintWriter writer = new PrintWriter(socket1.getOutputStream());
                                    writer.write(s + "\n");
                                    writer.flush();


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
