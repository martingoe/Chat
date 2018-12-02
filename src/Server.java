import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Application {
    //Stores the clients connected to the server
    static ArrayList<Socket> clients = new ArrayList<>();
    //The ServerSocket acting as the Server
    static ServerSocket serverSocket;

    public static void main(String[] args) {
        //Launch the GUI and the logic
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Create a ServerSocket on the Port 5555
        serverSocket = new ServerSocket(5555);

        Button button = new Button("Stop Server");
        button.setOnAction(event -> System.exit(0));
        primaryStage.setScene(new Scene(new StackPane(button), 200, 100));
        primaryStage.show();


        //Handles new users in a different thread as it also uses a while loop
        new Thread(new Handle()).start();

        //Create a new Thread in which the messages are handled
        new Thread(() -> {
            try {
                //Constantly do the following algorithm
                while (true) {
                    //Convert the ArrayList to a Array to avoid Exceptions
                    Socket[] clientsArr = clients.toArray(new Socket[0]);

                    //Iterate over all of the users
                    for (Socket socket : clientsArr) {

                        //Create a reader to read the message the user has maybe sent
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        //Find out if the user selected has sent a message
                        if (reader.ready()) {
                            //Read the message using the reader
                            String message = reader.readLine();
                            System.out.println("Received: " + message);

                            //Iterate over every connected client
                            for (Socket client : clientsArr) {
                                //Create a PrintWriter to write the new message to the selected client
                                PrintWriter writer = new PrintWriter(client.getOutputStream());
                                writer.println(message);
                                writer.flush();

                                //Check if the client has disconnected and remove him if so
                                if(writer.checkError()){
                                    Server.clients.remove(client);
                                }
                            }
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
