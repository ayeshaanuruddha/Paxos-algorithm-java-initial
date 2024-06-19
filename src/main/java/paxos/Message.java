package paxos;

import java.io.Serializable;

public class Message implements Serializable {
    public enum Type {
        PREPARE, PROMISE, ACCEPT_REQUEST, ACCEPTED
    }

    private Type type;
    private int proposalId;
    private int value;
    private int senderId;

    public Message(Type type, int proposalId, int value, int senderId) {
        this.type = type;
        this.proposalId = proposalId;
        this.value = value;
        this.senderId = senderId;
    }

    public Type getType() {
        return type;
    }

    public int getProposalId() {
        return proposalId;
    }

    public int getValue() {
        return value;
    }

    public int getSenderId() {
        return senderId;
    }
}
