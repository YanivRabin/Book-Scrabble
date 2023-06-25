package model.logic;

import com.google.gson.JsonObject;
import model.data.Board;
import model.data.Tile;
import model.data.Word;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Host extends Observable implements ClientHandler {

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
    static ExecutorService executorService = Executors.newFixedThreadPool(10); // only for one host
    BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();

    //Data-Game Members
    public String NickName;
    public Tile.Bag bag;
    public Guest hostPlayer;
    public Player player;
    Board board ; // singleton and get instance model
    private BufferedReader reader;
    private PrintWriter writer;

    //Default CTOR
    /**
     * The Host function is the main function of the Host class.
     * It creates a new thread that listens for incoming connections from clients, and then adds them to an ArrayList of guests.
     * The host also has a nickname, which can be changed by calling setNickName().

     *
     *
     * @return A string with the host's nickname and port number
     *
     * @docauthor Trelent
     */
    public Host(){
        this.board = Board.getBoard();
        this.bag = Tile.Bag.getBagModel();
        this.Port = GeneratePort();
        this.Stop = false;
        this.GuestList =new ArrayList<>();
        this.NickName="Host "+ getPort();
        this.hostPlayer = new Guest(NickName);
    }

    private static class HostModelHelper {
        public static final Host model_instance = new Host();
    }

    /**
     * The getModel function is a static function that returns the model instance of the Host class.
     *
     *
     *
     * @return The model_instance variable
     *
     * @docauthor Trelent
     */
    public static Host getModel() {
        return HostModelHelper.model_instance;
    }
    /**
     * The getPort function returns the port number of the server.
     *
     *
     *
     * @return The port number
     *
     * @docauthor Trelent
     */
    public int getPort() {
        return Port;
    }

    /**
     * The getIpAddress function returns the IP address of the server.
     *
     *
     *
     * @return The ip address of the computer
     *
     * @docauthor Trelent
     */
    public String getIpAddress() {
        return IP;
    }

    /**
     * The getNickName function returns the NickName of the user.
     *
     *
     *
     * @return The nickname variable
     *
     * @docauthor Trelent
     */
    public String getNickName() {
        return NickName;
    }

    /**
     * The getServer function returns the server socket that is created in the constructor.
     *
     *
     *
     * @return The server socket
     *
     * @docauthor Trelent
     */
    public ServerSocket getServer() {
        return LocalServer;
    }

    //Connects Host to Main Server
    /**
     * The CreateSocketToServer function creates a socket to the server.
     *
     *
     * @param  server Get the ip and port of the server
     *
     * @return A socket to the server
     *
     * @docauthor Trelent
     */
    public void CreateSocketToServer(MyServer server) throws IOException {
        GameServer=server;
        this.SocketToMyServer = new Socket(server.getIP(), server.getPort());
        System.out.println("Host Connected to Main Server");
    }
    /**
     * The CreateSocketToLocalServer function creates a socket to the local server.
     *
     *
     * @param  HostIp Connect to the local server
     * @param  Port Connect to the local server
    public void createsockettolocalserver(int port) throws ioexception {
            this
     *
     * @return A socket
     *
     * @docauthor Trelent
     */
    public void CreateSocketToLocalServer(String HostIp, int Port) throws IOException {
        this.hostPlayer.CreateSocketToHost(HostIp,Port);
        this.HostSocketToLocalServer = this.hostPlayer.getSocketToHost();
        System.out.println("Host Connected to local Host Server");
    }

    /**
     * The CreateProfile function creates a profile for the user.
     *
     *
     * @param  NickName Set the nickname of the profile
     *
     * @return A boolean value
     *
     * @docauthor Trelent
     */
    public void CreateProfile(String NickName){
        this.NickName = NickName;
        // add Photo or avatar
    }

    //start Host server
    /**
     * The start function creates a new thread and runs the runServer function in it.
     * This is done so that the server can be running in the background while other functions are being executed.

     *
     *
     * @return Void, so the server is running in the background
     *
     * @docauthor Trelent
     */
    public void start() {

        executorService.execute(()->{
            try {
                runServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * The runServer function is the main function of the server.
     * It opens a socket with a given port and waits for clients to connect.
     * When it receives a client, it adds him to its list of guests and starts handling his requests in another thread.

     *
     *
     * @return A void
     *
     * @docauthor Trelent
     */
    public void runServer() throws IOException {
        //open server with the port that given
        this.LocalServer = new ServerSocket(this.Port);
        executorService.submit(this::handleRequests);
        System.out.println("Host Server started on port :" + this.Port);
        this.IP = this.LocalServer.getInetAddress().getHostAddress();
        this.CreateSocketToLocalServer(this.IP, this.Port);

        executorService.execute(()->{
            try {
                handleClient(hostPlayer.getSocketToHost().getInputStream(), hostPlayer.getSocketToHost().getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        while (!this.Stop ) {
            try{
                if(this.GuestList.size() < this.MaxGuests) {
                    Socket guest = this.LocalServer.accept();
                    this.GuestList.add(guest);

                    // notify VM_Host
                    setChanged();
                    notifyObservers("guest connect");

                    if (guest.getPort() == hostPlayer.getSocketToHost().getLocalPort()){
                        System.out.println("Host Connected, Number of players: " + GuestList.size());
                    }
                    System.out.println("Guest Connected, Number of players: " + GuestList.size());
                    executorService.execute(()->{
                        try {
                            handleClient(guest.getInputStream(), guest.getOutputStream());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * The SendStartGameMessage function is used to send a message to all the guests in the game.
     * The message contains information about who is hosting, and what tiles they have been assigned.
     *
     *
     * @param  hostNickName Send the host's nickname to all clients
     *
     * @return A void, so it does not return anything
     *
     * @docauthor Trelent
     */
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

    public void sendPassTurnMessage() {

        for(Socket socket : this.GuestList) {

            try {
                MessageHandler messageHandler = new MessageHandler();
                messageHandler.createPassTurnMessage();

//                if (socket == this.HostSocketToLocalServer) {
//                    try {
//                        this.hostPlayer.inputQueue.put(messageHandler.jsonHandler.toJsonString());
//                    }
//                    catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                else {
                    OutputStream outToClient = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(outToClient);
                    out.println(messageHandler.jsonHandler.toJsonString());
                    out.flush();
//                }
            }
            catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    /**
     * The SendTryAgainMessage function is used to send a message to the client that they have lost and can try again.
     *
     *
     * @param  destination Identify the player that is being sent a message
     * @param  prevScore Send the previous score of the player to the server
     * @param  action Determine the action to be taken by the client
     * @param  hostNickName Identify the host of the game
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public String SendTryAgainMessage(String destination, int prevScore, String action, String hostNickName){
        // only serverHost
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateTryAgainMessage(destination, prevScore, action, hostNickName);
        return messageHandler.jsonHandler.toJsonString();
    }
    /**
     * The SendSuccessMessage function is used to send a success message to the client.
     *
     *
     * @param  destination Determine which client the message is being sent to
     * @param  newScore Update the score of a player
     * @param  action Specify the action that was performed by the player
     * @param  newCurrentTiles Send the new current tiles to the client
     * @param  hostNickName Identify the host of the game
     *
     * @return A string containing a json object
     *
     * @docauthor Trelent
     */
    public String SendSuccessMessage(String destination, int newScore, String action, String newCurrentTiles, String hostNickName){
        // only serverHost
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateSuccessMessage(destination, newScore, action, newCurrentTiles, hostNickName);
        return messageHandler.jsonHandler.toJsonString();
    }
    /**
     * The SendSucceededChallengeYouMessage function is used to send a message to the client that the challenge has been accepted.
     *
     *
     * @param  board Send the current state of the board to the client
     * @param  hostNickName Identify the player who sent the challenge
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public String SendSucceededChallengeYouMessage(Character[][] board, String hostNickName){
        // only serverHost
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateSucceededChallengeYouMessage(board, hostNickName);
        return messageHandler.jsonHandler.toJsonString();
    }
    /**
     * The SendUpdateBoardMessage function is used to send the updated board state to all of the guests in a game.
     *
     *
     * @param  board Send the board to the client
     * @param  hostNickName Identify the host of the game
     *
     * @return A boolean value
     *
     * @docauthor Trelent
     */
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
    /**
     * The SendTryPlaceWordMessage function is used to send a message to the local server
     * that contains information about the word that a player wants to place on the board.
     * The function takes in as parameters: source, destination, word, row, column and vertical.
     * Source is where this message came from (the client). Destination is where it's going (the local server). Word
     * represents what word we want to place on the board. Row and Column represent which position we want our first letter of our word placed at. Vertical represents whether or not we are placing our letters vertically or horizontally on the board. This function also checks if you
     *
     * @param  source Identify the player who sent the message
     * @param  destination Determine where the message is going to be sent
     * @param  word Determine if the player is using his current tiles
     * @param  row Determine the row on the board where a word is placed
     * @param  column Determine the column that the word is placed in
     * @param  vertical Determine if the word is vertical or horizontal
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void SendTryPlaceWordMessage(String source, String destination, String word,
                                        int row, int column, boolean vertical){
        // as a player
        if(!this.player.usingCurrentTiles(word)){
            System.out.println("You are not using your tiles, Not Sending");
        }
        else{
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(hostPlayer.getSocketToHost().getInetAddress());
            stringBuilder.append(":");
            stringBuilder.append(hostPlayer.getSocketToHost().getLocalPort());
            String socketSource = stringBuilder.toString();
            MessageHandler messageHandler = new MessageHandler();
            messageHandler.CreateTryPlaceWordMessage(source, destination, word, row, column,
                    vertical, this.player.getCurrentTiles(), socketSource);
            this.SendMessageToLocalServer(messageHandler.jsonHandler);
        }
    }
    /**
     * The SendChallengeMessage function is used to send a challenge message to the local server.
     *
     *
     * @param  source Identify the player who sent the message
     * @param  destination Determine the destination of the message
     * @param  word Store the word that is being challenged
     * @param  row Specify the row of the first letter in a word
     * @param  column Specify the column of the first letter in a word
     * @param  vertical Determine whether the word is placed horizontally or vertically
     *
     * @return A json object
     *
     * @docauthor Trelent
     */
    public void SendChallengeMessage(String source, String destination, String word,
                                     int row, int column, boolean vertical){
        // as a player
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(hostPlayer.getSocketToHost().getInetAddress());
        stringBuilder.append(":");
        stringBuilder.append(hostPlayer.getSocketToHost().getLocalPort());
        String socketSource = stringBuilder.toString();
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateChallengeMessage(source, destination, word, row, column,
                vertical, this.player.getCurrentTiles(), socketSource);
        this.SendMessageToLocalServer(messageHandler.jsonHandler);
    }


    /**
     * The handleClient function is the function that handles all of the communication between
     * the client and server. It reads in a JSON string from the input stream, then puts it into
     * an input queue for processing by another thread. The handleClient function also writes to
     * an output stream, which sends data back to the client. This is how we send messages back and forth!

     *
     * @param  inputStream Read from the server
     * @param  outputStream Write to the server
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    public void handleClient(InputStream inputStream, OutputStream outputStream) {
        while (!this.LocalServer.isClosed()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String jsonString = bufferedReader.readLine();
                if (jsonString != null)// Read an object from the server
                {
                    try {
                        inputQueue.put(jsonString); // Put the received object in the queue
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }  catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The handleRequests function is responsible for handling the requests that are sent to the server.
     * It takes in a request from the inputQueue and then processes it accordingly.

     *
     *
     * @return A void
     *
     * @docauthor Trelent
     */
    public void handleRequests() {

        while (!this.LocalServer.isClosed()) {

            int score = 0;
            try {
                String jsonString = inputQueue.take(); //blocking call
                System.out.println(jsonString);
                if (jsonString != null) {

                    JsonObject json = JsonHandler.convertStringToJsonObject(jsonString);

                    switch (json.get("MessageType").getAsString()) {

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

                        case "pass turn":
                            sendPassTurnMessage();
                            break;
                    }

                    String socketSource = json.get("SocketSource").getAsString();
                    Socket currentGuest = getSocket(socketSource);
                    PrintWriter out = new PrintWriter(currentGuest.getOutputStream());

                    if (score == 0) {
                        // ignore to guest Create try again message
                        out.println(this.SendTryAgainMessage(json.get("Source").getAsString(), 0, "try place word" , this.NickName));
                        out.flush();
                    }
                    else {
                        // ack , score to guest Create success message
                        String guestCurrentTiles = json.get("CurrentTiles").getAsString();
                        List<Character> NewCurrentTiles = this.reduceTilesFromCurrentTiles(json.get("Word").getAsString(), this.ConvertCurrentTilesToList(guestCurrentTiles));
                        System.out.println("New Score to add: "+score);
                        String jsonSuccess = this.SendSuccessMessage(json.get("Source").getAsString(), score, "try place word", this.CharavterslistToString(NewCurrentTiles), this.NickName);
                        out.println(jsonSuccess);
                        out.flush();
                        // notify all
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The SendMessageToGameServer function is used to send a message to the game server.
     *
     *
     * @param  text Send a message to the server
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public  void SendMessageToGameServer(String text){
        try {
            PrintWriter printWriter = new PrintWriter(this.SocketToMyServer.getOutputStream());
            printWriter.println(text);
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * The GetMessageFromGameServer function is used to get a message from the game server.
     *
     *
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public String GetMessageFromGameServer() throws IOException {
        Scanner in = new Scanner(new InputStreamReader(this.SocketToMyServer.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        while(in.hasNext()){
            stringBuilder.append(in.nextLine());
        }
        String resultText = stringBuilder.toString();

        return resultText;
    }

    /**
     * The SendMessageToLocalServer function is used to send a message to the local server.
     *
     *
     * @param  json Send a message to the server
     *
     * @return A jsonhandler object
     *
     * @docauthor Trelent
     */
    public  void SendMessageToLocalServer(JsonHandler json){
        writer.println(json.toJsonString());

    }
    /**
     * The GetMessageFromLocalServer function is used to get the message from local server.
     *
     *
     *
     * @return A jsonobject, which contains all the information
     *
     * @docauthor Trelent
     */
/*    public void GetMessageFromLocalServer() throws IOException {
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
                break;
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
    }*/

    public Socket getSocket(String source){
        String[] socketSplited = source.split(":");
        String ipSource = socketSplited[0].split("/")[1];
        String portSource = socketSplited[1];
        for(Socket s : this.GuestList){
            if (Objects.equals(s.getInetAddress().toString().substring(1), ipSource) && s.getPort() == Integer.parseInt(portSource)){
                return s;
            }
        }
        return null;
    }

    /**
     * The GeneratePort function generates a random port number between 1024 and 9999.
     *
     *
     *
     * @return A random port number
     *
     * @docauthor Trelent
     */
    public int GeneratePort(){
        int minPort = 1024;
        int maxPort = 9999;

        int randomPort = generateRandomPort(minPort, maxPort);
        return randomPort;
    }

    /**
     * The generateRandomPort function generates a random port number between the minPort and maxPort values.
     *
     *
     * @param  minPort Set the minimum port number that can be generated
     * @param  maxPort Set the maximum port number that can be generated
     *
     * @return A random number between the minport and maxport parameters
     *
     * @docauthor Trelent
     */
    public static int generateRandomPort(int minPort, int maxPort) {
        Random random = new Random();
        return random.nextInt(maxPort - minPort + 1) + minPort;
    }

    /**
     * The reduceTilesFromCurrentTiles function takes in a word and the current tiles of the player.
     * It then reduces the number of tiles from the currentTiles list by removing all letters that are used to form
     * a valid word. The function also generates new tiles for each letter removed from currentTiles, so that there are always 7 letters in total.

     *
     * @param  word Check if the word is valid
     * @param &lt;Character&gt; currentTiles Store the current tiles that are on the board
     *
     * @return The new current tiles after reduce and generate
     *
     * @docauthor Trelent
     */
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

    /**
     * The GenerateTiles function takes in an integer number and returns a list of characters.
     * The function generates the number of tiles specified by the input parameter, and adds them to a list.
     *
     *
     * @param  number Determine how many tiles the player gets
     *
     * @return A list of characters
     *
     * @docauthor Trelent
     */
    public List<Character> GenerateTiles(int number){
        List<Character> currentTiles = new ArrayList<>();
        for(int i = 0 ; i < number ; i++){
            currentTiles.add(this.bag.getRand().letter);
        }
        return currentTiles;
    }

    /**
     * The ConvertCurrentTilesToList function takes in a String of capital letters and converts it into a List of Characters.
     *
     *
     * @param  capitalTiles Store the current tiles that are in the player's hand
     *
     * @return A list of characters
     *
     * @docauthor Trelent
     */
    public List<Character> ConvertCurrentTilesToList(String capitalTiles) {
        List<Character> currentTiles = new ArrayList<>();
        for(int i = 0 ; i < capitalTiles.length() ; i++){
            currentTiles.add(capitalTiles.charAt(i));
        }
        return currentTiles;
    }

    /**
     * The getTileArray function takes a string and returns an array of Tiles.
     *
     *
     * @param  s Get the tiles from the bag
     *
     * @return An array of tile objects
     *
     * @docauthor Trelent
     */
    private static Tile[] getTileArray(String s) {

        Tile[] ts = new Tile[s.length()];
        int i = 0;
        for(char c: s.toCharArray()) {
            ts[i] = Tile.Bag.getBagModel().getTileForTileArray(c);
            i++;
        }
        return ts;
    }

    /**
     * The CharavterslistToString function takes a list of characters and returns a string.
     *
     *
     * @param &lt;Character&gt; characterList Pass the list of characters to the function
     *
     * @return A string of the characters in the list
     *
     * @docauthor Trelent
     */
    public String CharavterslistToString(List<Character> characterList) {
        StringBuilder sb = new StringBuilder();
        for (Character c : characterList) {
            sb.append(c);
        }
        return sb.toString();
    }



    /**
     * The close function is used to close the Host's ServerSocket and all of its Guest Sockets.
     * It also removes this Host from the Main Server's list of active hosts.

     *
     *
     * @return A boolean
     *
     * @docauthor Trelent
     */
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
