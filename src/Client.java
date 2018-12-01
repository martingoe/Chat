import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;


public class Client extends Application{

    public static void main(String[] args) {
        System.out.println("Starting Client");
        launch();
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        AtomicReference<String> data = new AtomicReference<>("");
        Socket client = new Socket("localhost", 5555);
        System.out.println("Started Client");

        OutputStream outputStream = client.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        InputStream inputStream = client.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        TextField in = new TextField();
        TextArea label = new TextArea();
        label.setEditable(false);


        TextField name = new TextField();

        label.resize(400, 400);
        in.setPrefSize(200, 20);
        name.setPrefSize(100, 20);

        HBox bottomPan = new HBox();
        bottomPan.setPrefSize(400, 20);
        bottomPan.getChildren().addAll(name, in);

        in.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:MM");

                writer.write(String.format("<%s %s> %s \n", name.getText(), dateFormat.format(date), in.getText()));
                writer.flush();
            }
        });

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(label);
        borderPane.setBottom(bottomPan);


        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(borderPane, 400, 500));
        primaryStage.show();

        new Thread(() -> {
            while (true) {
                String s;
                try {
                    if ((s = reader.readLine()) != null) {
                        System.out.println("New Message");
                        data.set(s + "\n" + data);
                        Platform.runLater(() -> label.setText(data.get()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
