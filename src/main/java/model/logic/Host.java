package model.logic;

import model.data.Board;
import model.data.Tile;
import model.data.Word;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Host implements ClientHandler{

    int HostPort;
    String HostIP;
    ServerSocket HostServer;
    Socket HostSocket;
    boolean HostStop;
    final int MaxGuests = 4;
    public List<Socket> guests;
    Board board ; // singleton and get instance model

    public String NickName; // how we get it toledo ?
    public Tile.Bag bag;

    Socket HostToMyServer;


    public Host(){
        this.board = new Board();
        this.HostPort = GeneratePort();
        this.HostStop = false;
        this.bag = Tile.Bag.getBagModel();
    }

    //
    private static class HostModelHelper {
        public static final Host model_instance = new Host();
    }

    public static Host getModel() {
        return HostModelHelper.model_instance;
    }

    public void CreateSocketToServer(MyServer server) throws IOException {
        this.HostToMyServer = new Socket(server.IP, server.port);
    }

    public void CreateProfile(String NickName){
        this.NickName = NickName;
        // add Photo or avatar
    }


    // up a host server
    //start server
    public void start() {

        //run server in the background
        new Thread(() -> {

            try {
                runServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void runServer() throws IOException {
        //open server with the port that given
        this.HostServer = new ServerSocket(this.HostPort);
        System.out.println("Server started on port :" + this.HostPort);
//        this.server.setSoTimeout(1000);
        this.HostIP = this.HostServer.getInetAddress().getHostAddress();
        this.HostSocket = new Socket(this.HostIP, this.HostPort);
        this.guests.add(HostSocket);
        while (!this.HostStop && this.guests.size() < this.MaxGuests) {

            Socket guest = this.HostServer.accept();
            this.guests.add(guest);
            // Implement with thread pool
        }
    }

    // the host need to try place word
    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {

        // get input the text contain ['word' ',' 'start' ',' 'end' ',' 'vertical/not']
        Scanner in = new Scanner(inFromClient);
        PrintWriter out = new PrintWriter(outToClient);
        String[] text = in.nextLine().split(",");
        boolean vertical = text[3].equals("true");

        Word word = new Word(this.getTileArray(text[0]), Integer.parseInt(text[1]), Integer.parseInt(text[2]), vertical);

        // try place
        int score =  this.board.tryPlaceWord(word);

        //if true go to my server, else out try again to the guest
        if (score == 0){
            // ignore to guest
            out.println("false");
            out.flush();
        }
        else {
            // ack , score to guest
            // query , challenge in my server

        }


//        out.flush();
    }

    public  void OutToServer(String text){
        try {
            PrintWriter printWriter = new PrintWriter(this.HostToMyServer.getOutputStream());
            printWriter.println(text);
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Tile[] getTileArray(String s) {

        Tile[] ts = new Tile[s.length()];
        int i = 0;
        for(char c: s.toCharArray()) {
            ts[i] = Tile.Bag.getBagModel().getTile(c);
            i++;
        }
        return ts;
    }

    public int GeneratePort(){
        int minPort = 1024;
        int maxPort = 65535;

        int randomPort = generateRandomPort(minPort, maxPort);
        return randomPort;
    }

    public static int generateRandomPort(int minPort, int maxPort) {
        Random random = new Random();
        return random.nextInt(maxPort - minPort + 1) + minPort;
    }


    @Override
    public void close() {

        this.HostStop = true;
        for (Socket g : this.guests) {
            try { g.close(); }
            catch (IOException e) { e.printStackTrace(); }
        }

        this.guests.clear();

        if (this.HostServer != null && !this.HostServer.isClosed()) {
            try {
                this.HostServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Server closed.");
        }
    }

}
