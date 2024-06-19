package paxos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Proposer extends Node {

    private int proposalId;
    private int proposedValue;
    private int majority;
    private Map<Node, Integer> promises = new HashMap<>();
    private Map<Node, Integer> acceptedValues = new HashMap<>();
    private boolean acceptSent = false;

    public Proposer(int nodeId, List<Node> peers, String hostname, int port) {
        super(nodeId, peers, hostname, port);
        this.majority = (peers.size() / 2) + 1;
    }

    private int generateProposalId() {
        return (int) (Math.random() * 1000);
    }

    public void propose(int value) {
        this.proposalId = generateProposalId();
        this.proposedValue = value;
        System.out.println("I'm proposer, my id: " + nodeId);
        System.out.println("In the propose | proposal id is: " + proposalId);
        sendMessageToAll(new Message(Message.Type.PREPARE, proposalId, value, nodeId));
    }

    public void receivePromise(Acceptor acceptor, int proposalId, int acceptedId, int acceptedValue) {
        if (proposalId == this.proposalId) {
            promises.put(acceptor, acceptedId);
            if (acceptedId != -1) {
                acceptedValues.put(acceptor, acceptedValue);
            }

            if (promises.size() >= majority && !acceptSent) {
                acceptSent = true;
                int maxAcceptedId = this.proposalId;
                int maxAcceptedValue = this.proposedValue;

                for (Map.Entry<Node, Integer> entry : acceptedValues.entrySet()) {
                    if (entry.getKey().earlyAcceptedProposalId > maxAcceptedId) {
                        maxAcceptedId = entry.getKey().earlyAcceptedProposalId;
                        maxAcceptedValue = entry.getValue();
                    }
                }

                sendMessageToAll(new Message(Message.Type.ACCEPT_REQUEST, maxAcceptedId, maxAcceptedValue, nodeId));
            }
        }
    }

    @Override
    public void handleMessage(Message message) {
        if (!hasReceived(message)) {
            switch (message.getType()) {
                case PROMISE:
                    receivePromise((Acceptor) findNodeById(message.getSenderId()), message.getProposalId(), message.getProposalId(), message.getValue());
                    break;
                default:
                    break;
            }
        }
    }

    private Node findNodeById(int nodeId) {
        for (Node node : peers) {
            if (node.nodeId == nodeId) {
                return node;
            }
        }
        return null;
    }
}
