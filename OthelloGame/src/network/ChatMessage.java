package network;

import java.io.Serializable;

public class ChatMessage extends Message implements Serializable  {
    private String message;
    private String sender;

    public ChatMessage(String sender, String message) {
        this.message = message;
        this.sender = sender;
        // Podemos adicionar o nome do jogador como remetente
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
