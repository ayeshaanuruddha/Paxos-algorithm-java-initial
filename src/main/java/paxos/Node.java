package paxos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Node implements Serializable {
    protected int nodeId;
    protected List<Node> peers;
    protected int promisedId = -1;
    protected int earlyAcceptedProposalId = -1;
    protected int acceptedValue = -1;
    protected boolean proposalAccepted = false;
    protected int port;
    protected String hostname;
    private Set<String> receivedMessages = new HashSet<>();

    public Node(int nodeId, List<Node> peers, String hostname, int port) {
        this.nodeId = nodeId;
        this.peers = peers;
        this.hostname = hostname;
        this.port = port;
    }

    public abstract void handleMessage(Message message);

    public void sendMessageToAll(Message message) {
        for (Node peer : peers) {
            NodeServer.sendMessage(peer.hostname, peer.port, message);
        }
    }

    public boolean hasReceived(Message message) {
        String messageId = generateMessageId(message);
        if (receivedMessages.contains(messageId)) {
            return true;
        } else {
            receivedMessages.add(messageId);
            return false;
        }
    }

    private String generateMessageId(Message message) {
        return message.getType() + "-" + message.getProposalId() + "-" + message.getSenderId();
    }

    public void receivePrepare(int proposalId, Proposer proposer) {
        // To be overridden by Acceptor
    }

    public void receivePromise(Acceptor acceptor, int proposalId, int acceptedId, int acceptedValue) {
        // To be overridden by Proposer
    }

    public void receiveAcceptRequest(int proposalId, int value, Node proposer) {
        if (proposalId >= promisedId) {
            promisedId = proposalId;
            earlyAcceptedProposalId = proposalId;
            acceptedValue = value;
            sendMessageToAll(new Message(Message.Type.ACCEPTED, proposalId, value, nodeId));
        }
    }

    public void receiveAccepted(int proposalId, int value, Node accepter) {
        // To be overridden by Learner
    }
}

