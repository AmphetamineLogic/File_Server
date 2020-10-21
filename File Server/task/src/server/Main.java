package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static final int PORT = 34522;

    public static void main(String[] args) {
        try (
                ServerSocket server = new ServerSocket(PORT)
        ) {
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
                    System.exit(0);
                } else {
                    File file = new File(System.getProperty("user.dir") +
                            File.separator + "src" + File.separator + "server" + File.separator + "data" + File.separator + request[1]);
                    System.out.println("Request: " + input);
                    System.out.println("File: " + file.getAbsolutePath());
                    switch (request[0]) {
                        case "GET":
                            if (file.exists()) {
                                Scanner scanner = new Scanner(file);
                                dataOutputStream.writeUTF("200 " + scanner.nextLine());
                                scanner.close();
                            } else {
                                dataOutputStream.writeUTF("404");
                            }
                            break;
                        case "PUT":
                            if (file.exists()) {
                                dataOutputStream.writeUTF("403");
                            } else {
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(request[2].getBytes());
                                fileOutputStream.close();
                                dataOutputStream.writeUTF("200");
                            }
                            break;
                        case "DELETE":
                            if (file.exists()) {
                                file.delete();
                                dataOutputStream.writeUTF("200");
                            } else {
                                dataOutputStream.writeUTF("404");
                            }
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
