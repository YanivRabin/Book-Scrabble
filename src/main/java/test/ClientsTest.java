package test;

import model.logic.BookScrabbleHandler;
import model.logic.Guest;
import model.logic.Host;
import model.logic.MyServer;

import java.io.IOException;

public class ClientsTest {
    // need to write test for Host Guest and my server connection and
    // send strings
    // get score
    // connect 4 guests
    // connect multiple hosts
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create the GameServer
        MyServer gameServer = new MyServer(1234, new BookScrabbleHandler());
        // Start the GameServer
        gameServer.start();

        try {
            // Create multiple Hosts and connect them to the GameServer
            for (int i = 0; i < 1; i++) {
                Host host = new Host();
                host.CreateSocketToServer(gameServer);
                host.start();
                Thread.sleep(5000);
//                System.out.println("Host IP: " + host.getIpAddress());
//                System.out.println("Host Port: " + host.getPort());

                // Create 5 Guests for each Host
                for (int j = 0; j < 5; j++) {
                    Guest guest = new Guest("Guest" + (i * 5 + j + 1));
                    guest.CreateSocketToHost(host.getIpAddress(), host.getPort());
                    // Thread.sleep(5000);
                    // System.out.println("Guest Connected: "+guest.NickName +"Port: "+host.getPort());
                }
                Thread.sleep(5000);
                host.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Perform your test operations...

        // Close the GameServer
        gameServer.close();
        System.out.println("Done");

        // Test Scenario 1: Test Server Initialization
//        MyServer server = MyServer.getServer(1234, new BookScrabbleHandler());
//        server.start();
//
//        // Test Scenario 2: Test Host Connection
//        Host host = new Host();
//        host.CreateSocketToServer(server);
//        host.start();
//        Thread.sleep(2000);
//        System.out.println("Host IP: "+host.getIpAddress());
//        System.out.println("Host connected to server.");
//
//        // Test Scenario 3: Test Guest Connection
//        Guest guest = new Guest("Guest1");
//        guest.CreateSocketToHost(host.getIpAddress(), host.getPort());
//        System.out.println("Guest connected to host.");
//
//        // Test Scenario 4: Test Word Placement
//        String randomWord = generateRandomWord();
//        String request = randomWord + ",0,0,true"; // Example word placement request
//        guest.sendOutput(request);
//        String response = guest.readInput();
//        System.out.println("Response from host: " + response);
//
//        // Clean up and close connections
//        guest.getSocketToHost().close();
//        host.getHostServer().close();
//        server.close();
//    }
//
//    public static String generateRandomWord() {
//        int wordLength = 5; // Length of the random word
//        Random random = new Random();
//        StringBuilder word = new StringBuilder();
//        for (int i = 0; i < wordLength; i++) {
//            char randomChar = (char) (random.nextInt(26) + 'a');
//            word.append(randomChar);
//        }
//        return word.toString();


    }
}

