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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    static ExecutorService executorService = Executors.newFixedThreadPool(6); // only for one host



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
//        this.player = new Player(HostIp ,this.NickName, 0, this.GenerateTiles(8));
        /*Thread clientThread = new Thread(() -> {
            try {
                this.GetMessageFromLocalServer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clientThread.start();*/
        executorService.execute(()->{
            try {
                this.GetMessageFromLocalServer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Host Connected to local Host Server");
    }

    public void CreateProfile(String NickName){
        this.NickName = NickName;
        // add Photo or avatar
    }

    //start Host server
    public void start() {

        //run server in the background
        /*new Thread(() -> {

            try {
                runServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();*/

        executorService.execute(()->{
            try {
                runServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void runServer() throws IOException {
        //open server with the port that given
        this.LocalServer = new ServerSocket(this.Port);
        System.out.println("Host Server started on port :" + this.Port);
        this.IP = this.LocalServer.getInetAddress().getHostAddress();
        this.CreateSocketToLocalServer(this.IP, this.Port);
        /*Thread clientThread = new Thread(() -> {
            try {
                handleClient(this.HostSocketToLocalServer.getInputStream(), this.HostSocketToLocalServer.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clientThread.start();*/
        executorService.execute(()->{
            try {
                handleClient(this.HostSocketToLocalServer.getInputStream(), this.HostSocketToLocalServer.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


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
                        /*clientThread = new Thread(() -> {
                            try {
                                handleClient(guest.getInputStream(), guest.getOutputStream());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        clientThread.start();*/
                        executorService.execute(()->{
                            try {
                                handleClient(guest.getInputStream(), guest.getOutputStream());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                    }



                }
            }catch (IOException e){
                e.printStackTrace();
            }
            // Implement with thread pool
        }
    }

    public void SendStartGameMessage(String hostNickName){
        // only serverHost

        for(Socket socket : this.GuestList){
            try {
                MessageHandler messageHandler = new MessageHandler();
                List<Character> StartGameTiles = this.GenerateTiles(8);
                messageHandler.CreateStartGameMessage(this.CharavterslistToString(StartGameTiles), hostNickName);
                OutputStream outToClient = socket.getOutputStream();
                PrintWriter out = new PrintWriter(outToClient);
                out.println(messageHandler.jsonHandler.toJsonString());
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public String SendTryAgainMessage(String destination, int prevScore, String action, String hostNickName){
        // only serverHost
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateTryAgainMessage(destination, prevScore, action, hostNickName);
        return messageHandler.jsonHandler.toJsonString();
    }
    public String SendSuccessMessage(String destination, int newScore, String action, String newCurrentTiles, String hostNickName){
        // only serverHost
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateSuccessMessage(destination, newScore, action, newCurrentTiles, hostNickName);
        return messageHandler.jsonHandler.toJsonString();
    }
    public String SendSucceededChallengeYouMessage(Character[][] board, String hostNickName){
        // only serverHost
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateSucceededChallengeYouMessage(board, hostNickName);
        return messageHandler.jsonHandler.toJsonString();
    }
    public void SendUpdateBoardMessage(Character[][] board, String hostNickName){
        // only serverHost
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateUpdateBoardMessage(board, hostNickName);
        for(Socket socket : this.GuestList){
            try {
                OutputStream outToClient = socket.getOutputStream();
                PrintWriter out = new PrintWriter(outToClient);
                out.println(messageHandler.jsonHandler.toJsonString());
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void SendTryPlaceWordMessage(String source, String destination, String word,
                                        int row, int column, boolean vertical){
        // as a player
        if(!this.player.usingCurrentTiles(word)){
            System.out.println("You are not using your tiles, Not Sending");
        }
        else{
            MessageHandler messageHandler = new MessageHandler();
            messageHandler.CreateTryPlaceWordMessage(source, destination, word, row, column,
                    vertical, this.player.getCurrentTiles());
            this.SendMessageToLocalServer(messageHandler.jsonHandler);
        }
    }
    public void SendChallengeMessage(String source, String destination, String word,
                                     int row, int column, boolean vertical){
        // as a player
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateChallengeMessage(source, destination, word, row, column,
                vertical, this.player.getCurrentTiles());
        this.SendMessageToLocalServer(messageHandler.jsonHandler);
    }

    // the host need to try place word
    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        int score = 0;
        PrintWriter out = new PrintWriter(outToClient);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inFromClient))) {
            String jsonString = reader.readLine();
            System.out.println(jsonString);
            out.println(out);
            /*out.flush();*/
            if (jsonString != null) {
                JsonObject json = JsonHandler.convertStringToJsonObject(jsonString);
                switch (json.get("MessageType").getAsString()){
                    case "try place word":
                        String q_word = json.get("Word").getAsString();
                        boolean q_vertical = json.get("Vertical").getAsString().equals("true");
                        int q_row = Integer.parseInt(json.get("Row").getAsString());
                        int q_column = Integer.parseInt(json.get("Column").getAsString());
                        Word Q_word = new Word(getTileArray(q_word), q_row, q_column, q_vertical);
                        score = this.board.tryPlaceWord(Q_word);
                        break;
                    case "challenge":
                        String c_word = json.get("Word").getAsString();
                        boolean c_vertical = json.get("Vertical").getAsString().equals("true");
                        int c_row = Integer.parseInt(json.get("Row").getAsString());
                        int c_column = Integer.parseInt(json.get("Column").getAsString());
                        Word C_word = new Word(getTileArray(c_word), c_row, c_column, c_vertical);
                        // do the Challenge
                        break;
                }
                //if true go to my server, else out try again to the guest
                if (score == 0){
                    // ignore to guest Create try again message
                    out.println(this.SendTryAgainMessage(json.get("Source").getAsString(), 0,
                            "try place word" , this.NickName));
                    out.flush();
                }
                else {
                    // ack , score to guest Create success message
                    String guestCurrentTiles = json.get("CurrentTiles").getAsString();
                    List<Character> NewCurrentTiles = this.reduceTilesFromCurrentTiles(json.get("Word").getAsString(),
                            this.ConvertCurrentTilesToList(guestCurrentTiles));
                    System.out.println("New Score to add: "+score);
                    String jsonSuccess = this.SendSuccessMessage(json.get("Source").getAsString(), score,
                            "try place word", this.CharavterslistToString(NewCurrentTiles), this.NickName);
                    System.out.println(jsonSuccess);
                    out.println(jsonSuccess);
                    out.flush();
                    // notify all
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
        switch (json.get("MessageType").getAsString()){
            case "start game":
                this.player = new Player(this.IP, this.NickName, 0);
                this.player.addTiles(json.get("StartTiles").getAsString());
                break;
            case "success":
                switch (json.get("Action").getAsString()) {
                    case "try place word":
                        System.out.println(this.NickName + "Try Place Word: " + "Success");
                        this.player.addScore(Integer.parseInt(json.get("NewScore").getAsString()));
                        this.player.prevScore = this.player.currentScore;
                        this.player.setCurrentTiles(json.get("NewCurrentTiles").getAsString());
                        // board change in Host.notifyall
                        break;
                    case "challenge":
                        System.out.println(this.NickName + "Challenge: " + "Success");
                        // score don't change
                        // board change in Host.notifyall
                        break;
                }
            case "try again":
                switch (json.get("Action").getAsString()){
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
                this.player.setCurrentBoard(json.get("Board").getAsString());
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

    public List<Character> reduceTilesFromCurrentTiles(String word , List<Character> currentTiles){
        // return New current tiles after reduce and generate
        int counterUsed = 0;
        for(int i = 0 ; i < word.length();i++){
            if(word.charAt(i) != '_'){
                for(Character t : currentTiles){
                    if(t == word.charAt(i)){
                        currentTiles.remove(t);
                        counterUsed ++;
                        break;
                    }
                }
            }
            if(counterUsed == word.length()){
                break;
            }
        }
        currentTiles.addAll(this.GenerateTiles(counterUsed));
        return currentTiles;
    }

    public List<Character> GenerateTiles(int number){
        List<Character> currentTiles = new ArrayList<>();
        for(int i = 0 ; i < number-1 ; i++){
            currentTiles.add(this.bag.getRand().letter);
        }
        return currentTiles;
    }

    public List<Character> ConvertCurrentTilesToList(String capitalTiles) {
        List<Character> currentTiles = new ArrayList<>();
        for(int i = 0 ; i < capitalTiles.length() ; i++){
            currentTiles.add(capitalTiles.charAt(i));
        }
        return currentTiles;
    }

    private static Tile[] getTileArray(String s) {

        Tile[] ts = new Tile[s.length()];
        int i = 0;
        for(char c: s.toCharArray()) {
            ts[i] = Tile.Bag.getBagModel().getTileForTileArray(c);
            i++;
        }
        return ts;
    }

    public String CharavterslistToString(List<Character> characterList) {
        StringBuilder sb = new StringBuilder();
        for (Character c : characterList) {
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public void close() {

        this.Stop = true;
        executorService.shutdownNow();
        for (Socket g : this.GuestList) {
            try { g.close(); }
            catch (IOException e) {
                e.printStackTrace();
            }
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
