package paxos;
import java.util.List;

public class Acceptor extends Node {

    public Acceptor(int nodeId, List<Node> peers, String hostname, int port) {
        super(nodeId, peers, hostname, port);
    }

    public void receivePrepare(int proposalId, Proposer proposer) {
        if (proposalId >= promisedId) {
            promisedId = proposalId;
            NodeServer.sendMessage(proposer.hostname, proposer.port, new Message(Message.Type.PROMISE, proposalId, acceptedValue, nodeId));
        }
    }

    public void receiveAcceptRequest(int proposalId, int value, Node proposer) {
        if (proposalId >= promisedId) {
            promisedId = proposalId;
            earlyAcceptedProposalId = proposalId;
            acceptedValue = value;
            System.out.println("I'm acceptor: " + nodeId + " I'm accepting proposal: " + proposalId + " value is: " + value);
            sendMessageToAll(new Message(Message.Type.ACCEPTED, proposalId, value, nodeId));
        }
    }

    @Override
    public void handleMessage(Message message) {
        if (!hasReceived(message)) {
            switch (message.getType()) {
                case PREPARE:
                    receivePrepare(message.getProposalId(), (Proposer) findNodeById(message.getSenderId()));
                    break;
                case ACCEPT_REQUEST:
                    receiveAcceptRequest(message.getProposalId(), message.getValue(), findNodeById(message.getSenderId()));
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
