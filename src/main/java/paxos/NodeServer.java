package paxos;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class NodeServer extends Thread {
    private Node node;
    private int port;

    public NodeServer(Node node, int port) {
        this.node = node;
        this.port = port;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new NodeHandler(socket, node).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String hostname, int port, Message message) {
        try (Socket socket = new Socket(hostname, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class NodeHandler extends Thread {
    private Socket socket;
    private Node node;

    public NodeHandler(Socket socket, Node node) {
        this.socket = socket;
        this.node = node;
    }

    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            Message message = (Message) in.readObject();
            node.handleMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
