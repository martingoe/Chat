import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

class Client implements Runnable{

    public static void main(String[] args) {
        new Thread(new Client()).start();
    }
    @Override
    public void run() {
        try {
            String data = "";
            Socket client = new Socket("localhost", 5555);
            System.out.println("Started Client");

            OutputStream outputStream = client.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);

            InputStream inputStream = client.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


            JFrame frame = new JFrame("Chat");
            frame.setSize(400, 500);

            JTextField in = new JTextField();
            JTextArea label = new JTextArea();
            JTextField name = new JTextField();

            label.setPreferredSize(new Dimension(400, 400));
            in.setPreferredSize(new Dimension(200, 20));
            name.setPreferredSize(new Dimension(100, 20));

            JPanel downpan = new JPanel();
            downpan.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
            downpan.add(name);
            downpan.add(in);

            in.addActionListener(e -> {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:MM");

                writer.write(String.format("<%s %s> %s \n", name.getText(), dateFormat.format(date), in.getText()));
                writer.flush();

            });

            frame.setLayout(new BorderLayout());
            frame.add(label, BorderLayout.NORTH);
            frame.add(downpan, BorderLayout.SOUTH);

            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);

            while(true){
                String s;
                if((s = reader.readLine()) != null){
                    System.out.println("New Message");
                    data= s + "\n" + data;
                    label.setText(data);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}