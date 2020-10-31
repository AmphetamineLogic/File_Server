package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34522;

    public static void main(String[] args) {
        String[] input = new String[3];
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                System.out.print("Enter action (1 - get the file, 2 - create a file, 3 - delete the file): ");
                input[0] = scanner.nextLine();
                String response;

                switch (input[0]) {
                    case "1":
                        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
                        input[1] = scanner.nextLine();
                        if (input[1].equals("1")) {
                            System.out.print("Enter filename: ");
                            input[2] = scanner.nextLine();
                            dataOutputStream.writeUTF("GET BY NAME " + input[2]);
                        }
                        if (input[1].equals("2")) {
                            System.out.print("Enter id: ");
                            input[2] = scanner.nextLine();
                            dataOutputStream.writeUTF("GET BY ID " + input[2]);
                        }
                        System.out.println("The request was sent.");
                        dataInputStream.mark(0);
                        System.out.println("Code: " + dataInputStream.readInt());
                        dataInputStream.reset();
                        System.out.println("Size: " + dataInputStream.readInt());
                        break;
                    case "2":
                        System.out.print("Enter filename: ");
                        input[1] = scanner.nextLine();
                        System.out.print("Enter file content: ");
                        input[2] = scanner.nextLine();
                        dataOutputStream.writeUTF("PUT " + input[1] + " " + input[2]);
                        System.out.println("The request was sent.");
                        response = dataInputStream.readUTF();
                        if ("200".equals(response)) {
                            System.out.println("The response says that the file was created!");
                        }
                        else {
                            System.out.println("The response says that creating the file was forbidden!");
                        }
                        break;
                    case "3":
                        System.out.print("Do you want to delete the file by name or by id (1 - name, 2 - id): ");
                        input[1] = scanner.nextLine();
                        if (input[1].equals("1")) {
                            System.out.print("Enter filename: ");
                            input[2] = scanner.nextLine();
                            dataOutputStream.writeUTF("DELETE BY NAME " + input[2]);
                        }
                        if (input[1].equals("2")) {
                            System.out.print("Enter id: ");
                            input[2] = scanner.nextLine();
                            dataOutputStream.writeUTF("DELETE BY ID " + input[2]);
                        }
                        System.out.println("The request was sent.");
                        response = dataInputStream.readUTF();
                        if ("200".equals(response)) {
                            System.out.println("The response says that the file was successfully deleted!");
                        }
                        else {
                            System.out.println("The response says that the file was not found!");
                        }
                        break;
                    case "exit":
                        dataOutputStream.writeUTF("exit");
                        return;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
