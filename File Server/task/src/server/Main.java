package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final int PORT = 34522;
    protected static Storage storage;
    static AtomicBoolean isRunning;

    public static void main(String[] args) {
        try (
                ServerSocket server = new ServerSocket(PORT)
        ) {
            storage = new Storage();
            isRunning = new AtomicBoolean();
            isRunning.set(true);
            while (isRunning.get() == true) {
                Session session = new Session(server.accept());
                session.start();
            }
            server.close();
            System.exit(0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void shutdown () {
        System.exit(0);
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
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

                String input = dataInputStream.readUTF();
                String[] request = input.split(" ");

                if ("exit".equals(input)) {
                    Main.storage.saveMap();
                    Main.isRunning.set(false);
                    socket.close();
                } else {
                    System.out.println("Request: " + input);
                    boolean result;
                    switch (request[0]) {
                        case "GET":
                            byte[] fileAsBytes = new byte[0];
                            if (request[1].equals("BY_NAME")) {
                                fileAsBytes = Main.storage.readFile(request[2]);
                            } else {
                              fileAsBytes = Main.storage.readFile(Integer.parseInt(request[2]));
                            }
                            if (fileAsBytes != null) {
                                dataOutputStream.writeInt(200);
                                dataOutputStream.writeInt(fileAsBytes.length);
                                dataOutputStream.write(fileAsBytes);
                            }
                            else {
                                dataOutputStream.writeUTF("404");
                            }
                            dataOutputStream.close();
                            break;
                        case "PUT":
                            Integer resultingID;
                            if (request.length == 1) {
                                int size = dataInputStream.readInt();
                                resultingID = Main.storage.putFile(dataInputStream.readNBytes(size));
                            } else {
                                String desiredName = request[1];
                                int size = dataInputStream.readInt();
                                resultingID = Main.storage.putFile(dataInputStream.readNBytes(size), desiredName);
                            }
                            if (resultingID == -1) {
                                dataOutputStream.writeUTF("404");
                            }
                            else {
                                dataOutputStream.writeUTF("200");
                                dataOutputStream.writeUTF(resultingID.toString());
                            }
                            dataOutputStream.close();
                            break;
                        case "DELETE":
                            if (request[1].equals("BY_NAME")) {
                                result = Main.storage.deleteFile(request[2]);
                            }
                            else {
                                result = Main.storage.deleteFile(Integer.parseInt(request[2]));
                            }
                            dataOutputStream.writeUTF(result ? "200" : "404");
                            break;
                        case "EXIT":
                            return;
                    }
                }
                } catch(IOException e){
                    e.printStackTrace();
                }

    }
}
