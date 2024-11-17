package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class NetworkConnection {
    protected Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;

    protected abstract void createConnection(String serverIP) throws Exception;

    public void send(Object data) throws Exception {
        out.writeObject(data);
        out.flush();
    }

    public Object receive() throws Exception {
        return in.readObject();
    }

    public void closeConnection() throws Exception {
        in.close();
        out.close();
        socket.close();
    }
}
