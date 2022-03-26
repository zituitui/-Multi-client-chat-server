import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Connection extends Thread {

    private String Username = null;
    private BufferedReader reader;
    private PrintWriter writer;
    private List<String> MessageList;

    public Connection(Socket socket, List<String> MessageList) throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        this.MessageList = MessageList;
    }

    @Override
    public void run() {
        super.run();

        // intialization of each connection
        writer.println("Please enter your Username: ");
        try {
            while (Username == null) {
                Username = reader.readLine();
            }
            Username = Username.strip();
            writer.println("Welcome to miniChat, " + Username);

            // tell everyone that we come in
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String sendMsg = "[" + formatter.format(new Date()) + "] " + Username + " entered chat, welcome.";
            ChatServer.BroadCast(MessageList, sendMsg);
            // }
        } catch (IOException e) {
            // System.out.println(e);
            System.out.println(e);
        }

        // dead loop
        while (true) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String received = reader.readLine();
                if (received.equals("QUIT")) {
                    String sendMsg = "[" + formatter.format(new Date()) + "] " + Username + " quit, goodbye";
                    ChatServer.BroadCast(MessageList, sendMsg);
                    writer.println(sendMsg);
                    writer.println("--END--");
                    break;
                }
                System.out.println("Server received from " + Username + ", content: " + received);

                String sendMsg = "[" + formatter.format(new Date()) + "] " + Username + ": " + received;

                synchronized (MessageList) {
                    MessageList.add(sendMsg);
                    MessageList.notifyAll();
                    // ChatServer.BroadCast(MessageList, sendMsg);
                }

            } catch (IOException e) {
                System.out.println(e);
                break; // exception, maybe closed by user
            }
        }
    }

    // send message to this user
    public void sendMessage(String msg) {
        writer.println(msg);
    }
}
