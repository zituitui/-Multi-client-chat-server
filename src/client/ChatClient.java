//ZJU zituitui 2021 11 26
//i love you jyf

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

//the ChatClient
public class ChatClient {
    public static final int PORT = 6033;
    Scanner scanner = new Scanner(System.in);

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;

    public ChatClient(Socket socket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        this.socket = socket;
    }

    public void start() {
        new ReadinThread(reader, socket).start();
        new PrintThread(writer, socket).start();
    }

    public static void main(String[] args) throws IOException {
        InetAddress addr = null;
        // InetAddress addr = InetAddress.getByName(null);
        System.out.println("Input your targeting server address");
        System.out.println("Input \"local\" to indicate localhost");
        Scanner sc = new Scanner(System.in);
        String host = sc.nextLine();
        if (host.equals("local"))
            addr = InetAddress.getByName(null);
        else
            addr = InetAddress.getByName(host);
        System.out.println("Targeting Server addr: " + addr);
        Socket socket = new Socket(addr, PORT);
        System.out.println("socket connected");

        ChatClient client = new ChatClient(socket);
        client.start();
    }

}

// the thread aiming to read message in
class ReadinThread extends Thread {
    private BufferedReader reader;
    private Socket socket;

    public ReadinThread(BufferedReader reader, Socket socket) {
        this.reader = reader;
        this.socket = socket;
    }

    public void run() {
        // dead loop
        while (true) {
            try {
                if (socket.isClosed())
                    break;
                String received = reader.readLine();
                System.out.println(received);
                if (received.equals("--END--")) {
                    System.out.println("closing...");
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
                System.exit(-1);
            }

        }
    }
}

// the thread aiming to give out message
class PrintThread extends Thread {
    private PrintWriter writer;
    private Socket socket;

    public PrintThread(PrintWriter writer, Socket socket) {
        this.writer = writer;
        this.socket = socket;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        // dead loop
        while (true) {
            // read next line from console
            if (socket.isClosed())
                break;
            String str = scanner.nextLine();
            // give this message to server
            writer.println(str);
        }
    }
}
