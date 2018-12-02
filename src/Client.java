import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


public class Client extends Application {

    private String name;

    public static void main(String[] args) {
        System.out.println("Starting Client");
        //Start the GUI and run the logic
        launch();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        //The data is an Atomic as it has to be edited in a Lambda
        AtomicReference<String> data = new AtomicReference<>("");
        //Connect to the server on the localhost. Port: 5555
        Socket client = new Socket("localhost", 5555);
        System.out.println("Started Client");

        //Declare a PrintWriter using the OutputStream to write to the Server
        OutputStream outputStream = client.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        //Declare a BufferedReader using the InputStream to receive messages from the Server
        InputStream inputStream = client.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        getUsername();

        //Declare a TextField to write messages
        TextField in = new TextField();
        in.setPromptText("Send Message");

        //Declare a TextArea to display messages
        TextArea label = new TextArea();
        label.setEditable(false);

        //Resize the TextField/TextArea
        label.resize(400, 400);
        in.setPrefSize(400, 20);

        //Add a declared UI elements tó a HBox
        HBox bottomPan = new HBox();
        bottomPan.setPrefSize(400, 20);
        bottomPan.getChildren().addAll(in);

        //Set the event listener for keys being pressed and only listen for the enter key
        in.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                //Get the current time in HH:MM
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:MM");

                //Write the message to the Server
                writer.println(String.format("<%s %s> %s", name, dateFormat.format(date), in.getText()));
                writer.flush();
            }
        });

        //Create a borderPane to store the UI elements
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(label);
        borderPane.setBottom(bottomPan);


        //Set a event listener to receive closing requests
        primaryStage.setOnCloseRequest(event -> {
            try {
                //Tell the other clients that the client disconnected
                writer.write(String.format("%s: DISCONNECTED \n", name));
                writer.flush();

                //close the client
                client.close();
                //Close the program
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //Set the title to be "Chat" and display the content in a window
        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(borderPane, 400, 500));
        primaryStage.show();

        //Run the logic in another thread as it uses a while loop that cannot be used in the same thead because of the UI
        new Thread(() -> {
            while (true) {

                String message;
                try {
                    //declare the message to be what the reader received and check if it is not null
                    if ((message = reader.readLine()) != null) {
                        System.out.println("New Message");
                        //Update the data and the label displaying the Messages
                        data.set(message + "\n" + data);
                        Platform.runLater(() -> label.setText(data.get()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void getUsername() {

        //Ask the user for the username to be used
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Username");
        nameDialog.setContentText("Please enter your username: ");

        //Save the response to a Optional and see if it is existing
        Optional<String> nameResult = nameDialog.showAndWait();
        if (nameResult.isPresent()) {
            //Save the actual selected username as a String
            name = nameResult.get();
            //Return if the name is not empty
            if (!(name.equals("") || name.equals(" ")))
                return;
        }
        //Only executed if username is empty or not existing

        //Tell the user to try again using a valid username
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Username");
        alert.setHeaderText("Please enter a valid username");
        alert.showAndWait();

        //Restart the function
        getUsername();
    }
}
