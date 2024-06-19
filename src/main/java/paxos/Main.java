package paxos;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int numNodes = 5;
        List<Node> nodes = new ArrayList<>();

        List<Node> peers = new ArrayList<>();
        String hostname = "localhost";
        int basePort = 8000;

        for (int i = 0; i < numNodes; i++) {
            Node node;
            int port = basePort + i;
            if (i == 0) {
                node = new Proposer(i, peers, hostname, port);
            } else if (i == 1) {
                node = new Learner(i, peers, hostname, port);
            } else {
                node = new Acceptor(i, peers, hostname, port);
            }
            peers.add(node);
            nodes.add(node);
            new NodeServer(node, port).start();
        }

        // Simulate proposing a value
        Proposer proposer = (Proposer) nodes.get(0);
        proposer.propose(42);

        // Give some time for asynchronous messages
        try {
            Thread.sleep(1000); // Sleep for 1 second to simulate the time taken for messages
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
