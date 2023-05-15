package model.logic;

import java.io.*;
import java.net.Socket;

public class Client implements ClientHandler {

    private Socket clientSocket;
    private InputStream in;
    private OutputStream out;
    public String ipClient;

    public Client(int port) {

//        clientSocket = socket;
        try {
            clientSocket = new Socket("localhost", port);
        } catch (IOException e) {
            System.out.println("Client cannot connect to server");
        }

//        this.ipClient = clientSocket.getInetAddress().getHostAddress();
//        try {
//            in = clientSocket.getInputStream();
//            out = clientSocket.getOutputStream();
//            // get ip for client init into ipClient
//        } catch (IOException e) {
//            System.err.println("Error creating input/output streams: " + e.getMessage());
//        }
    }

//    @Override
//    public void run() {
//        try {
//            while (true) {
//                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                String message = bufferReader.readLine();
//                if (message == null) {
//                    System.out.println("Client disconnected.");
//                    break;
//                }
//                System.out.println("Received message from client: " + message);
//                this.handleClient(this.in, this.out);
//            }
//        } catch (IOException e) {
//            System.err.println("Error handling client: " + e.getMessage());
//        } finally {
//            this.close();
//        }
//    }

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        // send to yaniv
    }

    @Override
    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing client: " + e.getMessage());
        }
    }
}