package model.logic;

import model.data.Board;
import model.data.Tile;
import model.data.Word;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Host implements ClientHandler{
    //Logic Members
    int Port;
    String IP;
    ServerSocket LocalServer;
    Socket HostSocketToLocalServer;
    boolean Stop;

    final int MaxGuests = 4;
    public List<Socket> GuestList;
    Socket SocketToMyServer;
    MyServer GameServer; // The MyServer this host connected to


    //Data-Game Members
    public String NickName; // how we get it toledo ?
    public Tile.Bag bag;
    Board board ; // singleton and get instance model
    private BufferedReader reader;
    private PrintWriter writer;


    //Default CTOR
    public Host(){
        this.board = new Board();
        this.Port = GeneratePort();
        this.NickName="Host "+getPort();
        this.Stop = false;
        this.bag = Tile.Bag.getBagModel();
        GuestList =new ArrayList<>();
    }

    //
    private static class HostModelHelper {
        public static final Host model_instance = new Host();
    }

    public static Host getModel() {
        return HostModelHelper.model_instance;
    }
    public int getPort() {
        return Port;
    }

    public String getIpAddress() {
        return IP;
    }

    public String getNickName() {
        return NickName;
    }

    public ServerSocket getServer() {
        return LocalServer;
    }
    //Connects Host to Main Server
    public void CreateSocketToServer(MyServer server) throws IOException {
        GameServer=server;
        this.SocketToMyServer = new Socket(server.getIP(), server.getPort());
        System.out.println("Host Connected to Main Server");
    }
    public void CreateSocketToLocalServer(String HostIp, int Port) throws IOException {
        this.HostSocketToLocalServer = new Socket(HostIp, Port);
        this.reader = new BufferedReader(new InputStreamReader(HostSocketToLocalServer.getInputStream()));
        this.writer = new PrintWriter(HostSocketToLocalServer.getOutputStream(), true);
        System.out.println("Host Connected to local Host Server");
    }

    public void CreateProfile(String NickName){
        this.NickName = NickName;
        // add Photo or avatar
    }


    // lunch a host server
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
        this.LocalServer = new ServerSocket(this.Port);
        System.out.println("Host Server started on port :" + this.Port);
        this.IP = this.LocalServer.getInetAddress().getHostAddress();
        this.CreateSocketToLocalServer(this.IP, this.Port);
        Thread clientThread = new Thread(() -> {
            try {
                handleClient(this.HostSocketToLocalServer.getInputStream(), this.HostSocketToLocalServer.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clientThread.start();
        while (!this.Stop ) {
            try{
                if(this.GuestList.size() < this.MaxGuests) {
                    Socket guest = this.LocalServer.accept();
                    this.GuestList.add(guest);
                    if (guest.getPort() == this.HostSocketToLocalServer.getLocalPort()){
                        System.out.println("Host Connected, Number of players: " + GuestList.size());
                    }
                    else{
                        System.out.println("Guest Connected, Number of players: " + GuestList.size());
                    }
                    clientThread = new Thread(() -> {
                        try {
                            handleClient(guest.getInputStream(), guest.getOutputStream());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    clientThread.start();

                }
            }catch (IOException e){
                e.printStackTrace();
            }
            // Implement with thread pool
        }
    }

    // the host need to try place word
    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        //initialize data-game
        int score = 0;

        PrintWriter out = new PrintWriter(outToClient);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inFromClient))) {
            String line = reader.readLine();
            if (line != null) {
                System.out.println(line);
                // get input the text contain ['Q or C' ',' 'word' ',' 'start' ',' 'end' ',' 'vertical/not']
                /*String[] text = line.split(",");
                switch (text[0]) {
                    case "Q":
                        boolean Q_vertical = text[4].equals("true");
                        Word Q_word = new Word(getTileArray(text[1]), Integer.parseInt(text[2]), Integer.parseInt(text[3]), Q_vertical);
                        score = this.board.tryPlaceWord(Q_word);
                        break;
                    case "C":
                        boolean C_vertical = text[4].equals("true");
                        Word C_word = new Word(getTileArray(text[1]), Integer.parseInt(text[2]), Integer.parseInt(text[3]), C_vertical);
                        //score =
                        break;
                }
                //if true go to my server, else out try again to the guest
                if (score == 0){
                    // ignore to guest
                    out.println("Not Legal");
                    out.flush();
                }
                else {
                    // ack , score to guest
                    String stringBuilder = "Success," +
                            score;
                    out.println(stringBuilder);
                    System.out.println(stringBuilder);
                    out.flush();

                }*/
            }
            /*while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }*/
        } catch (IOException e) {
            throw new RuntimeException("Error while handling client: " + e.getMessage(), e);
        } /*finally {
            // Clean up resources, such as closing the input and output streams
            try {
                inFromClient.close();
                outToClient.close();
            } catch (IOException e) {
                // Handle the exception appropriately
                e.printStackTrace();
            }
        }*/
    }

    public  void SendMessageToGameServer(String text){
        try {
            PrintWriter printWriter = new PrintWriter(this.SocketToMyServer.getOutputStream());
            printWriter.println(text);
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String GetMessageFromGameServer() throws IOException {
        Scanner in = new Scanner(new InputStreamReader(this.SocketToMyServer.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        while(in.hasNext()){
            stringBuilder.append(in.nextLine());
        }
        String resultText = stringBuilder.toString();

        return resultText;
    }

    public  void SendMessageToLocalServer(String text){
        writer.println(text);

    }
    public String GetMessageFromLocalServer() throws IOException {
        Scanner in = new Scanner(this.reader);
        StringBuilder stringBuilder = new StringBuilder();
        while(in.hasNext()){
            stringBuilder.append(in.nextLine());
        }
        String resultText = stringBuilder.toString();

        return resultText;
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
        int maxPort = 9999;

        int randomPort = generateRandomPort(minPort, maxPort);
        return randomPort;
    }

    public static int generateRandomPort(int minPort, int maxPort) {
        Random random = new Random();
        return random.nextInt(maxPort - minPort + 1) + minPort;
    }


    @Override
    public void close() {

        this.Stop = true;
        for (Socket g : this.GuestList) {
            try { g.close(); }
            catch (IOException e) { e.printStackTrace(); }
        }
        this.GuestList.clear();

        //Delete this Host from the Main Server HostsList

        if (GameServer != null) {
            for (Socket host : GameServer.HostsList) {
                if(host.getPort()==SocketToMyServer.getLocalPort()) {
                    GameServer.HostsList.remove(host);
                    break;
                }
                }
            }


        if (this.LocalServer != null && !this.LocalServer.isClosed()) {
            try {
                this.LocalServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Host "+getNickName()+" Server closed.");
        }
    }

}
