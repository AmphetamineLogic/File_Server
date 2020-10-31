package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static final int PORT = 34522;
    protected static Storage storage;

    public static void main(String[] args) {
        try (
                ServerSocket server = new ServerSocket(PORT)
        ) {
            storage = new Storage();
            storage.writeSomeShit("gsom.txt", 1);
            while (true) {
                Session session = new Session(server.accept());
                session.start();
            }
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
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

                String input = dataInputStream.readUTF();
                String[] request = input.split(" ");

                if ("exit".equals(input)) {
                    Main.storage.saveMap();
                    System.exit(0);
                } else {
                    File file = new File(System.getProperty("user.dir") +
                            File.separator + "src" + File.separator + "server" + File.separator + "data" + File.separator + request[1]);
                    System.out.println("Request: " + input);
                    boolean result;
                    switch (request[0] + request[1]) {
                        case "GETBY":
                            if (request[2].equals("NAME")) {
                                byte[] fileAsBytes = Main.storage.readFile(request[3]);
                                if (fileAsBytes != null) {
                                    dataOutputStream.write(200);
                                    dataOutputStream.write(fileAsBytes.length);
                                    dataOutputStream.write(fileAsBytes);
                                }
                                else {
                                    dataOutputStream.writeUTF("404");
                                }
                            }
                            break;
                        case "PUTBY":
                            if (file.exists()) {
                                dataOutputStream.writeUTF("403");
                            } else {
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(request[2].getBytes());
                                fileOutputStream.close();
                                dataOutputStream.writeUTF("200");
                            }
                            break;
                        case "DELETEBY":
                            if (request[2].equals("NAME")) {
                                result = Main.storage.deleteFile(request[3]);
                            }
                            else {
                                result = Main.storage.deleteFile(Integer.parseInt(request[3]));
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
