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
            // Create Host and connect them to the GameServer
            Host host = Host.getModel();
            host.CreateSocketToServer(gameServer);
            host.start();
            Thread.sleep(2000);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Host"+(1));
            stringBuilder.append(": The message has been received");

            // Create 3 Guests for each Host
            Guest guest1 = new Guest("Guest" + (1));
            guest1.CreateSocketToHost(host.getIpAddress(), host.getPort());
            guest1.SendToHost("Q,WIN,7,7,true");
            Thread.sleep(5000);
            System.out.println(guest1.score);


        } catch (IOException e) {
            e.printStackTrace();
        }

        // Perform your test operations...

        // Close the GameServer
//        gameServer.close();
        System.out.println("Done");


    }
}

