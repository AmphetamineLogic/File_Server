package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34522;
    private static final String rootPath = System.getProperty("user.dir") +
            File.separator + "src" + File.separator + "client" + File.separator + "data" + File.separator;

    public static void main(String[] args) {
        String[] input = new String[3];
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                System.out.print("Enter action (1 - get the file, 2 - save the file, 3 - delete the file): ");
                input[0] = scanner.nextLine();
                String response;
                switch (input[0]) {
                    case "1":
                        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
                        input[1] = scanner.nextLine();
                        if (input[1].equals("1")) {
                            System.out.print("Enter filename: ");
                            input[2] = scanner.nextLine();
                            dataOutputStream.writeUTF("GET BY_NAME " + input[2]);
                        }
                        if (input[1].equals("2")) {
                            System.out.print("Enter id: ");
                            input[2] = scanner.nextLine();
                            dataOutputStream.writeUTF("GET BY_ID " + input[2]);
                        }
                        System.out.println("The request was sent.");
                        int responseCode = dataInputStream.readInt();
                        if (responseCode == 200) {
                            int arraySize = dataInputStream.readInt();
                            byte[] fileAsBytes = dataInputStream.readNBytes(arraySize);
                            System.out.print("The file was downloaded! Specify a name for it: ");
                            String fileName = scanner.nextLine();
                            FileOutputStream fileOutputStream = new FileOutputStream(rootPath + fileName);
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                            bufferedOutputStream.write(fileAsBytes);
                            bufferedOutputStream.close();
                            System.out.println("File saved on the hard drive!");
                        } else {
                            System.out.println("The response says that this file is not found!");
                        }
                        break;
                    case "2":
                        System.out.print("Enter name of the file: ");
                        input[1] = scanner.nextLine();
                        System.out.print("Enter name of the file to be saved on server: ");
                        input[2] = scanner.nextLine();
                        if (input[2].equals("")) {
                            dataOutputStream.writeUTF("PUT");
                        } else {
                            dataOutputStream.writeUTF("PUT " + input[2]);
                        }
                        FileInputStream fileInputStream = new FileInputStream(rootPath + input[1]);
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                        byte[] fileAsBytes = bufferedInputStream.readAllBytes();
                        dataOutputStream.writeInt(fileAsBytes.length);
                        dataOutputStream.write(fileAsBytes);
                        bufferedInputStream.close();
                        System.out.println("The request was sent.");
                        response = dataInputStream.readUTF();
                        if ("200".equals(response)) {
                            System.out.println("The response says that the file is saved! ID = " + dataInputStream.readUTF());
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
                            dataOutputStream.writeUTF("DELETE BY_NAME " + input[2]);
                        }
                        if (input[1].equals("2")) {
                            System.out.print("Enter id: ");
                            input[2] = scanner.nextLine();
                            dataOutputStream.writeUTF("DELETE BY_ID " + input[2]);
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
