package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORT = 34522;

    public static void main(String[] args) {
        System.out.println("Server started!");
        try (
                ServerSocket server = new ServerSocket(PORT)
        ) {
            Session session = new Session(server.accept());
            session.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Session extends Thread {
    private final Socket socket;

    public Session(Socket socketForClient) {
        this.socket = socketForClient;
    }

    public void run() {
        try (
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream()))
        {
            String msg = "All files were sent!";
            System.out.println("Received: " + dataInputStream.readUTF());
            System.out.println("Sent: " + msg);
            dataOutputStream.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
