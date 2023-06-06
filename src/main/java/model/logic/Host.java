package model.logic;

import com.google.gson.JsonObject;
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
    public String NickName;
    public Tile.Bag bag;
    public Player player;
    Board board ; // singleton and get instance model
    private BufferedReader reader;
    private PrintWriter writer;

    //Default CTOR
    public Host(){
        this.board = Board.getBoard();
        this.bag = Tile.Bag.getBagModel();
        this.Port = GeneratePort();
        this.Stop = false;
        GuestList =new ArrayList<>();
        this.NickName="Host "+ getPort();
    }

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
        this.player = new Player(HostIp ,this.NickName, 0, this.GenerateTiles(8));
        Thread clientThread = new Thread(() -> {
            try {
                this.GetMessageFromLocalServer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clientThread.start();
        System.out.println("Host Connected to local Host Server");
    }

    public void CreateProfile(String NickName){
        this.NickName = NickName;
        // add Photo or avatar
    }

    //start Host server
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
        int score = 0;
        PrintWriter out = new PrintWriter(outToClient);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inFromClient))) {
            String jsonString = reader.readLine();
            if (jsonString != null) {
                JsonObject json = JsonHandler.convertStringToJsonObject(jsonString);
                switch (String.valueOf(json.get("MessageType"))){
                    case "try place word":
                        String q_word = String.valueOf(json.get("Word"));
                        boolean q_vertical = String.valueOf(json.get("Vertical")).equals("true");
                        int q_row = Integer.parseInt(String.valueOf(json.get("Row")));
                        int q_column = Integer.parseInt(String.valueOf(json.get("Column")));
                        Word Q_word = new Word(getTileArray(q_word), q_row, q_column, q_vertical);
                        score = this.board.tryPlaceWord(Q_word);
                        break;
                    case "challenge":
                        String c_word = String.valueOf(json.get("Word"));
                        boolean c_vertical = String.valueOf(json.get("Vertical")).equals("true");
                        int c_row = Integer.parseInt(String.valueOf(json.get("Row")));
                        int c_column = Integer.parseInt(String.valueOf(json.get("Column")));
                        Word C_word = new Word(getTileArray(c_word), c_row, c_column, c_vertical);
                        // do the Challenge
                        break;
                }
                //if true go to my server, else out try again to the guest
                if (score == 0){
                    // ignore to guest Create try again message
                    out.println("Not Legal");
                    out.flush();
                    System.out.println("Not Legal");
                }
                else {
                    // ack , score to guest Create success message
                    /*String stringBuilder = "Success," + text[1] + ","+ score;
                    out.println(stringBuilder);*/
                    out.flush();

                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while handling client: " + e.getMessage(), e);
        }
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

    public  void SendMessageToLocalServer(JsonHandler json){
        writer.println(json.toJsonString());

    }
    public void GetMessageFromLocalServer() throws IOException {
        String jsonString = this.reader.readLine();
        JsonObject json = JsonHandler.convertStringToJsonObject(jsonString);
        switch (String.valueOf(json.get("MessageType"))){
            case "start game":
                this.player = new Player(this.IP, this.NickName, 0);
                this.player.addTiles(String.valueOf(json.get("StartTiles")));
                break;
            case "success":
                switch (String.valueOf(json.get("Action"))) {
                    case "try place word":
                        System.out.println(this.NickName + "Try Place Word: " + "Success");
                        this.player.addScore(Integer.parseInt(String.valueOf(json.get("NewScore"))));
                        this.player.prevScore = this.player.currentScore;
                        this.player.setCurrentTiles(String.valueOf(json.get("NewCurrentTiles")));
                        // board change in Host.notifyall
                        break;
                    case "challenge":
                        System.out.println(this.NickName + "Challenge: " + "Success");
                        // score don't change
                        // board change in Host.notifyall
                        break;
                }
            case "try again":
                switch (String.valueOf(json.get("Action"))){
                    case "try place word":
                        System.out.println(this.NickName + "Try Place Word: "+ "Didn't success, try again");
                        break;
                    case "challenge":
                        System.out.println(this.NickName + "Challenge: "+ "Didn't success, try again");
                        break;
                }
                break;
            case "succeeded in challenging you":
                System.out.println(this.NickName + ": i have been complicated");
                this.player.currentScore = this.player.prevScore;
                this.player.currentBoard = this.player.prevBoard;
                this.player.currentTiles = this.player.prevTiles;
                break;
            case "update board":
                System.out.println(this.NickName + " updated Board");
                this.player.setCurrentBoard(String.valueOf(json.get("Board")));
                break;
        }
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


    public List<Character> GenerateTiles(int number){
        List<Character> currentTiles = new ArrayList<>();
        for(int i = 0 ; i < number-1 ; i++){
            currentTiles.add(this.bag.getRand().letter);
        }
        return currentTiles;
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
