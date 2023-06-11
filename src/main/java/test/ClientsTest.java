package test;

import model.logic.BookScrabbleHandler;
import model.logic.Guest;
import model.logic.Host;
import model.logic.MyServer;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

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

            System.out.println("threads: " + Thread.activeCount());
            // Create 3 Guests for each Host
            Guest guest1 = new Guest("Guest" + (1));
            guest1.CreateSocketToHost(host.getIpAddress(), host.getPort());
            System.out.println("threads: " + Thread.activeCount());
            Guest guest2 = new Guest("Guest" + (1 + 1));
            guest2.CreateSocketToHost(host.getIpAddress(), host.getPort());
            System.out.println("threads: " + Thread.activeCount());
//            guest1.SendToHost("Q,WIN,7,7,true");
            Thread.sleep(3000);
            host.SendStartGameMessage(host.NickName);
            System.out.println();
            System.out.println();
            System.out.println("Start Game !!");
            Thread.sleep(3000);
            System.out.println();
            System.out.println(host.player.getNickName());
            System.out.println(host.player.getCurrentScore());
            host.player.printCurrentTiles();
            System.out.println();
            System.out.println(guest1.player.getNickName());
            System.out.println(guest1.player.getCurrentScore());
            guest1.player.printCurrentTiles();
            System.out.println();
            System.out.println(guest2.player.getNickName());
            System.out.println(guest2.player.getCurrentScore());
            guest2.player.printCurrentTiles();


            /*Scanner scanner = new Scanner(System.in);
            System.out.println();
            // Prompt the user to enter a string
            System.out.print("Enter a string: ");

            // Read the string input from the console
            String input = scanner.nextLine();

            // Print the input string
            System.out.println("You entered: " + input);
            Thread.sleep(5000);*/
            Thread.sleep(10000);
            System.out.println("threads: " + Thread.activeCount());
            for(Socket socket : host.GuestList){
                if(socket.isConnected()){
                    System.out.println("Alive");
                }
            }

            guest1.player.setCurrentTiles("WINAGGED");
            guest1.SendTryPlaceWordMessage(guest1.NickName, host.NickName, "WIN", 7,7,true);
            Thread.sleep(5000);
//            guest1.SendTryPlaceWordMessage(guest1.NickName, host.NickName, "WIN", 7,7,true);
            System.out.println(guest1.player.getCurrentScore());
//            Thread.sleep(5000);
//            scanner.close();
            // Close the Scanner object






            /*Guest guest2 = new Guest("Guest" + (1 + 1));
            guest2.CreateSocketToHost(host.getIpAddress(), host.getPort());
            guest2.SendToHost("Q,W_N,8,6,false");
            Thread.sleep(2000);
            Guest guest3 = new Guest("Guest" + (2 + 1));
            guest3.CreateSocketToHost(host.getIpAddress(), host.getPort());
            guest3.SendToHost("Q,_IN,7,7,true");
            Thread.sleep(2000);*/
            System.out.println("Toledo sharmuta");
            guest1.Disconnect();
            guest2.Disconnect();
            host.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        // Perform your test operations...

        // Close the GameServer
        gameServer.close();
        System.out.println("Done");


    }
}

