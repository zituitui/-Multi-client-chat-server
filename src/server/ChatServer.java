import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    public static final int PORT = 6033; // the port number
    private ServerSocket serverSocket; // Socker at the server end
    private List<String> MessageList; // message to be to each user
    private List<Connection> connections;// a list to record the mainteinning connections

    private Thread connectorThread;
    private Thread senderThread;

    public ChatServer() throws IOException {

        serverSocket = new ServerSocket(PORT);
        connections = new ArrayList<>();
        MessageList = new ArrayList<>();

        connectorThread = new Connector(serverSocket, MessageList, connections);
        senderThread = new Sender(connections, MessageList);
    }

    public void start() {
        connectorThread.start(); // start listen for connection
        senderThread.start(); // start sender for all connection
        System.out.println("Server started " + serverSocket);
    }

    public static void BroadCast(List<String> MessageList, String sendMsg) {
        synchronized (MessageList) {
            MessageList.add(sendMsg);
            MessageList.notifyAll();
        }
    }

    public static void main(String[] args) throws IOException {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }
}

class Sender extends Thread {

    private List<Connection> connections;
    private List<String> MessageList;

    public Sender(List<Connection> connections, List<String> MessageList) {
        this.connections = connections;
        this.MessageList = MessageList;
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            String msg = null;
            synchronized (MessageList) {
                if (MessageList.isEmpty()) {
                    try {
                        MessageList.wait();
                    } catch (InterruptedException e) {
                    }
                }
                if (!MessageList.isEmpty()) {
                    msg = MessageList.get(0);
                    MessageList.remove(0);
                }
            }

            // for each connetion, ought to send message
            if (msg != null) {
                for (Connection c : connections) {
                    c.sendMessage(msg);
                }
            }
        }
    }
}

class Connector extends Thread {
    private ServerSocket serverSocket; // Socker at the server end
    private List<String> MessageList;
    private List<Connection> connections;// a list to record the mainteinning connections

    public Connector(ServerSocket serverSocket, List<String> MessageList, List<Connection> connections) {
        this.serverSocket = serverSocket;
        this.MessageList = MessageList;
        this.connections = connections;
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept(); // a new user!
                System.out.println("Server Connected: " + socket);
                Connection NewConnection = new Connection(socket, MessageList); // allocate new connection for the new
                                                                                // user
                connections.add(NewConnection);
                NewConnection.start(); // run the connection program
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}