package model.logic;

import com.google.gson.JsonObject;
import model.data.Tile;
import model.data.Word;
import model.data.Board;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Host extends Observable implements ClientHandler {

    //Logic Members
    int Port;
    String IP;
    ServerSocket LocalServer;
    Socket HostSocketToLocalServer;
    boolean Stop;
    Socket currentSuccessMessageSocket;
    String currentSuccessMessagePrevScore;
    Word currentSuccessMessageWord;

    final int MaxGuests = 4;
    public List<Socket> GuestList;
    Socket SocketToMyServer;
    public Map<String,Integer> NameToScore;

    public Future<String> getStringFuture() {
        return stringFuture;
    }

    public void setStringFuture(Future<String> stringFuture) {
        this.stringFuture = stringFuture;
    }

    public Future<String> stringFuture ;
    MyServer GameServer; // The MyServer this host connected to
    static ExecutorService executorService = Executors.newFixedThreadPool(10); // only for one host
    BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    public BlockingQueue<String> inputQueueFromGameServer = new LinkedBlockingQueue<>();

    //Data-Game Members
    public String NickName;
    public Tile.Bag bag;
    public Guest hostPlayer;
    public Player player;
    Board board ; // singleton and get instance model
    private BufferedReader reader;
    private PrintWriter writer;
    String prevScore;

    //Default CTOR
    /**
     * The Host function is the main function of the Host class. It creates a new thread for each client that connects to it, and then
     * runs a loop which waits for messages from clients. When it receives one, it checks if its an end game message or not. If so,
     * then the host will send out all scores to all players and close down their threads (and thus disconnect them). Otherwise,
     * if its just a normal move message from one player to another (or itself), then we simply update our board model with this move
     * and send out an updated board state to everyone else in the game.
     *
     *
     * @return The port number of the host
     *
     * @docauthor Trelent
     */
    public Host() {

        this.board = Board.getBoardModel();
        this.bag = Tile.Bag.getBagModel();
        this.Port = GeneratePort();
        this.Stop = false;
        this.GuestList =new ArrayList<>();
        this.NickName="Host "+ getPort();
        this.hostPlayer = new Guest(NickName);
        this.NameToScore = new HashMap<>();
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

    /**
     * The getSocketToMyServer function returns the socket that is connected to the server.
     *
     *
     *
     * @return The socket to my server
     *
     * @docauthor Trelent
     */
    public Socket getSocketToMyServer() {
        return SocketToMyServer;
    }

    //Connects Host to Main Server
    /**
     * The CreateSocketToServer function creates a socket to the server.
     *
     *
     * @param server server Get the ip and port of the server
     *
     * @return A socket object
     *
     * @docauthor Trelent
     */
    public void CreateSocketToServer(MyServer server) throws IOException {
        GameServer=server;
        this.SocketToMyServer = new Socket(server.getIP(), server.getPort());
//        executorService.submit(this::HandleMessageFromGameServer);

        executorService.execute(()->{
            try {
                handleGameServer(this.SocketToMyServer.getInputStream(), this.SocketToMyServer.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

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
    public void setNickName(String NickName){
        this.NickName = NickName;
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
     * The getLocalNetworkAddress function returns the local network address of the device.
     *
     *
     *
     * @return The ip address of the local network interface
     *
     * @docauthor Trelent
     */
    private String getLocalNetworkAddress() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // filters out 127.0.0.1 and inactive interfaces
            if (iface.isLoopback() || !iface.isUp())
                continue;

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while(addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address) return addr.getHostAddress();
            }
        }
        return null;  // or throw an exception
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
//        this.LocalServer = new ServerSocket(this.Port,4, InetAddress.getLocalHost());
        this.LocalServer = new ServerSocket(this.Port, 0, InetAddress.getByName("0.0.0.0"));
        executorService.submit(this::handleRequests);
//        this.IP = this.LocalServer.getInetAddress().getHostAddress();
        this.IP = this.getLocalNetworkAddress();  // The method to get your local network address
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
        int c = 0;
        for(Socket socket : this.GuestList){
            try {
                MessageHandler messageHandler = new MessageHandler();
                List<Character> StartGameTiles = this.GenerateTiles(8);
                messageHandler.CreateStartGameMessage(this.CharavterslistToString(StartGameTiles),
                        hostNickName, c, this.GuestList.size());
                OutputStream outToClient = socket.getOutputStream();
                PrintWriter out = new PrintWriter(outToClient);
                out.println(messageHandler.jsonHandler.toJsonString());
                out.flush();
                c++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The sendPassTurnMessage function is used to send a message to all players in the game that it is their turn.
     * This function will be called when the host player passes his/her turn.

     *
     *
     * @return A message that is sent to all players
     *
     * @docauthor Trelent
     */
    public void sendPassTurnMessage() {
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.createPassTurnMessage();
        for(Socket socket : this.GuestList) {

            try {
                if (socket.getPort() == this.hostPlayer.getSocketToHost().getLocalPort()){
                    this.hostPlayer.inputQueue.put(messageHandler.jsonHandler.toJsonString());
                }
                else{
                    OutputStream outToClient = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(outToClient);
                    out.println(messageHandler.jsonHandler.toJsonString());
                    out.flush();
                }
            }
            catch (IOException | InterruptedException e) {throw new RuntimeException(e);}
        }
    }
    /**
     * The sendStopChallengeAlive function is used to send a message to all the players in the game,
     * telling them that they should stop sending challenge alive messages. This function is called when
     * a player has been found cheating and therefore needs to be removed from the game. The host will then
     * call this function so that no more challenge alive messages are sent by any of the players in order for
     * them not to get confused about which player was kicked out of the game.

     *
     *
     * @return A boolean
     *
     * @docauthor Trelent
     */
    public void sendStopChallengeAlive() {
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.createStopChallengeAlive();
        for(Socket socket : this.GuestList) {

            try {
                if (socket.getPort() == this.hostPlayer.getSocketToHost().getLocalPort()){
                    this.hostPlayer.inputQueue.put(messageHandler.jsonHandler.toJsonString());
                }
                else{
                    OutputStream outToClient = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(outToClient);
                    out.println(messageHandler.jsonHandler.toJsonString());
                    out.flush();
                }
            }
            catch (IOException | InterruptedException e) {throw new RuntimeException(e);}
        }
    }

    /**
     * The sendEndGame function is used to send a message to all players in the game that the game has ended.
     *
     *
     * @param winner winner Identify the winner of the game
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public void sendEndGame(String winner) {
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.createEndGameMessage(winner);
        for(Socket socket : this.GuestList) {

            try {
                if (socket.getPort() == this.hostPlayer.getSocketToHost().getLocalPort()){
                    this.hostPlayer.inputQueue.put(messageHandler.jsonHandler.toJsonString());
                }
                else{
                    OutputStream outToClient = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(outToClient);
                    out.println(messageHandler.jsonHandler.toJsonString());
                    out.flush();
                }
            }
            catch (IOException | InterruptedException e) {throw new RuntimeException(e);}
        }
    }

    /**
     * The getWinner function returns the name of the player with the highest score.
     *
     *
     *
     * @return The name of the player with the highest score
     *
     * @docauthor Trelent
     */
    public String getWinner() {
        int maxScore = 0;
        String winner = "";
        for (String name : this.NameToScore.keySet()) {
            if (this.NameToScore.get(name) > maxScore) {
                maxScore = this.NameToScore.get(name);
                winner = name;
            }
        }
        return winner;
    }

    /**
     * The sendUpdatePrevToCurrent function is used to send the previous state of the game to all players.
     * This function is called when a player has just joined and needs to be updated on what has happened in the game so far.

     *
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public void sendUpdatePrevToCurrent() {
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.updatePrevToCurrent();
        for(Socket socket : this.GuestList) {
            try {
                if (socket.getPort() == this.hostPlayer.getSocketToHost().getLocalPort()){
                    this.hostPlayer.inputQueue.put(messageHandler.jsonHandler.toJsonString());
                }
                else{
                    OutputStream outToClient = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(outToClient);
                    out.println(messageHandler.jsonHandler.toJsonString());
                    out.flush();
                }
            }
            catch (IOException | InterruptedException e) {throw new RuntimeException(e);}
        }
    }

    /**
     * The CreateMessageToGameServer function takes in a message and socketSource,
     * creates a MessageHandler object, calls the CreateMessageToGameServer function
     * from the MessageHandler class with the given parameters, and returns a JSON string.

     *
     * @param message message Send a message to the game server
     * @param socketSource socketSource Determine which socket to send the message to
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public String CreateMessageToGameServer(String message, String socketSource){
        // only serverHost
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateMessageToGameServer(message, socketSource);
        return messageHandler.jsonHandler.toJsonString();
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
     * The SendSucceededChallengeYouMessage function is used to send a message to the client that
     * indicates that the challenge has been accepted. The function takes in two parameters, hostNickName and prevScore.
     * It then creates a new MessageHandler object and calls its CreateSucceededChallengeYouMessage function with these two parameters.
     * Finally, it returns the json string created by this call as its return value.

     *
     * @param hostNickName hostNickName Identify the host of the game
     * @param prevScore prevScore Send the previous score of the host to the client
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public String SendSucceededChallengeYouMessage(String hostNickName, String prevScore){
        // only serverHost
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateSucceededChallengeYouMessage(hostNickName, prevScore);
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
    public void SendUpdateBoardMessage(String board, String hostNickName){
        // only serverHost
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateUpdateBoardMessage(board, hostNickName);
        for(Socket socket : this.GuestList){
            try {
                if (socket.getPort() == this.hostPlayer.getSocketToHost().getLocalPort()){
                    this.hostPlayer.inputQueue.put(messageHandler.jsonHandler.toJsonString());
                }
                else {
                    OutputStream outToClient = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(outToClient);
                    out.println(messageHandler.jsonHandler.toJsonString());
                    out.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void SendNewTilesMessage(String tiles, String source, Socket currentGuest){
        // only serverHost
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateGenerateNewTilesMessage(source, tiles);
        try {
            if (currentGuest.getPort() == this.hostPlayer.getSocketToHost().getLocalPort()){
                this.hostPlayer.inputQueue.put(messageHandler.jsonHandler.toJsonString());
            }
            else {
                OutputStream outToClient = currentGuest.getOutputStream();
                PrintWriter out = new PrintWriter(outToClient);
                out.println(messageHandler.jsonHandler.toJsonString());
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
     * The handleRequests function is responsible for handling all the requests that are sent to the host.
     * It handles them by using a switch case, and then sending back an appropriate response.

     *
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    public void handleRequests() {
        while (!this.LocalServer.isClosed()) {
            int score = 0;
            boolean flagChallenge = true;
            Word Q_word = null;
            try {
                String jsonString = inputQueue.take(); //blocking call
                System.out.println(jsonString);
                if (jsonString != null) {
                    JsonObject json = JsonHandler.convertStringToJsonObject(jsonString);
                    switch (json.get("MessageType").getAsString()){
                        case "try place word":
                            String q_word = json.get("Word").getAsString();
                            boolean q_vertical = json.get("Vertical").getAsString().equals("true");
                            int q_row = Integer.parseInt(json.get("Row").getAsString());
                            int q_column = Integer.parseInt(json.get("Column").getAsString());
                            Q_word = new Word(getTileArray(q_word), q_row, q_column, q_vertical);
                            score = this.board.tryPlaceWord(Q_word);
                            prevScore = json.get("PrevScore").getAsString();
                            break;
                        case "challenge":
                            this.sendStopChallengeAlive();
                            Thread.sleep(2000);

                            ArrayList<Word> challengeAllTheWords = this.board.getWordsForChallenge(this.currentSuccessMessageWord);
                            int counterChallenge = 0;
                            for(Word w : challengeAllTheWords){
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("C,");
                                stringBuilder.append(w.toString());

                                StringBuilder stringBuilderSocket = new StringBuilder();
                                stringBuilderSocket.append(Host.getModel().getSocketToMyServer().getInetAddress());
                                stringBuilderSocket.append(":");
                                stringBuilderSocket.append(Host.getModel().getSocketToMyServer().getLocalPort());
                                String socketSource = stringBuilderSocket.toString();

                                String jsonStringChallenge = Host.getModel().CreateMessageToGameServer(stringBuilder.toString(),socketSource);
                                Host.getModel().SendMessageToGameServer(jsonStringChallenge);

//                                this.SendMessageToGameServer(stringBuilder.toString());
                                boolean res = this.inputQueueFromGameServer.take().equals("true");
                                if(!res){
                                    counterChallenge++;
                                }
                            }
                            if(counterChallenge != challengeAllTheWords.size()){
                                flagChallenge = false;
                            }
                            else {
                                this.HandleChallenge(true, this.currentSuccessMessagePrevScore, this.currentSuccessMessageWord);
                                continue;
                            }
                            break;
                        case "update board":
                            this.hostPlayer.inputQueue.put(jsonString);
                            continue;
                        case "pass turn":
                            this.sendPassTurnMessage();
                            continue;
                        case "new player joined":
                            this.NameToScore.put(json.get("Message").getAsString(), 0);
                            continue;
                        case "end game":
                            this.sendEndGame(getWinner());
                            setChanged();
                            notifyObservers("end game");
                            continue;
                        case "generate new tiles":
                            String currentTiles = json.get("CurrentTiles").getAsString();
                            for (char c: currentTiles.toCharArray()) {
                                bag.put(bag.getTileForTileArray(c));
                            }
                            List<Character> newTiles = this.GenerateTiles(8);
                            StringBuilder sb = new StringBuilder();
                            for (Character c: newTiles) {
                                sb.append(c.charValue());
                            }
                            String socketSource = json.get("SocketSource").getAsString();
                            Socket currentGuest = getSocket(socketSource);
                            SendNewTilesMessage(sb.toString(),socketSource,currentGuest);
                            continue;

                    }

                    String socketSource = json.get("SocketSource").getAsString();
                    Socket currentGuest = getSocket(socketSource);// here
                    PrintWriter out = new PrintWriter(currentGuest.getOutputStream());
                    if (!flagChallenge){
                        String tryAgainString = this.SendTryAgainMessage(currentGuest.toString(), 0,
                                "challenge", this.getNickName());
                        if (currentGuest.getPort() == this.hostPlayer.getSocketToHost().getLocalPort()){
                            this.hostPlayer.inputQueue.put(tryAgainString);
                        }
                        else {
                            out.println(tryAgainString);
                            out.flush();
                        }

                        continue;
                    }
                    if (score == 0){
                        if(Objects.equals(json.get("Source").getAsString(), this.NickName)){
                            this.hostPlayer.inputQueue.put(this.SendTryAgainMessage(json.get("Source").getAsString(), 0,
                                    "try place word" , this.NickName));
                            continue;
                        }
                        // ignore to guest Create try again message
                        out.println(this.SendTryAgainMessage(json.get("Source").getAsString(), 0,
                                "try place word" , this.NickName));
                        out.flush();
                    }
                    else {
                        // update player score in name to score map
                        this.NameToScore.put(json.get("Source").getAsString(), this.NameToScore.get(json.get("Source").getAsString()) + score);
                        setChanged();
                        notifyObservers("update map");

                        this.currentSuccessMessageSocket = currentGuest;
                        this.currentSuccessMessagePrevScore = json.get("PrevScore").getAsString();
                        this.currentSuccessMessageWord = Q_word;
                        String guestCurrentTiles = json.get("CurrentTiles").getAsString();
                        List<Character> NewCurrentTiles = this.reduceTilesFromCurrentTiles(json.get("Word").getAsString(),
                                this.ConvertCurrentTilesToList(guestCurrentTiles));
                        System.out.println("New Score to add: "+score);
                        String jsonSuccess = this.SendSuccessMessage(json.get("Source").getAsString(), score,
                                "try place word", this.CharavterslistToString(NewCurrentTiles), this.NickName);
                        if(Objects.equals(json.get("Source").getAsString(), this.NickName)){
                            this.hostPlayer.inputQueue.put(jsonSuccess);
                            this.SendUpdateBoardMessage(this.board.parseBoardToString(this.board.getTiles()), this.NickName);
                            continue;
                        }
                        out.println(jsonSuccess);
                        out.flush();
                        // notify all
                        this.SendUpdateBoardMessage(this.board.parseBoardToString(this.board.getTiles()), this.NickName);
                    }
                }
            }
            catch (InterruptedException e) {e.printStackTrace();}
            catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    /**
     * The HandleChallenge function is called when a player challenges another player's word.
     * If the challenge succeeds, then the tiles that were used to form the challenged word are returned to their respective bag and removed from the board.
     * The board is updated accordingly and sent back to all players in order for them to update their boards as well.
     * A message is also sent back indicating that a challenge has succeeded, which will be handled by each client individually (see Client class).

     *
     * @param res res Determine whether the challenge was successful or not
     * @param prevScore prevScore Update the score of the player who challenged you
     * @param w w Get the row and column of the word
    public void handlechallenge(boolean res , string prevscore, word w) {

            if(res) {
                system
     *
     * @return A boolean, which is the result of the challenge
     *
     * @docauthor Trelent
     */
    public void HandleChallenge(boolean res , String prevScore, Word w) {

        if(res) {
            System.out.println("challenge success");
            int row = w.getRow();
            int col = w.getCol();
            Character[][] toUpdateBoard = this.hostPlayer.player.prevBoard;
            for(Tile t : w.getTiles()){
                if (t != null) {
                    Tile.Bag.getBagModel().put(t);
                    board.removeTile(row, col);
                }
                if (w.isVertical()) {
                    row++;
                }
                else {
                    col++;
                }
            }
            this.SendUpdateBoardMessage(board.parseCharacterArrayToString(toUpdateBoard), this.NickName);
            String jsonChallengingYou = this.SendSucceededChallengeYouMessage(this.NickName, prevScore);
            try {Thread.sleep(1000);}
            catch (InterruptedException e) {e.printStackTrace();}
            try {
                if (currentSuccessMessageSocket.getPort() == this.hostPlayer.getSocketToHost().getLocalPort()) {
                    this.hostPlayer.inputQueue.put(jsonChallengingYou);
                }
                else {
                    PrintWriter printWriter = new PrintWriter(this.currentSuccessMessageSocket.getOutputStream());
                    printWriter.println(jsonChallengingYou);
                    printWriter.flush();
                }
            }
            catch (IOException e) {throw new RuntimeException(e);}
            catch (InterruptedException e) {e.printStackTrace();}
        }
        else{
            System.out.println("Challenge didn't success, the word is legal");
        }
    }

    /**
     * The sendChallengeSuccess function is called when the host player has successfully created a challenge.
     * It sends a message to all players in the game that they have been challenged and are now waiting for
     * other players to join. The message contains information about who sent it, what type of message it is,
     * and what data should be displayed on screen (in this case &quot;Waiting for Players&quot;). This function also sets up
     * an input queue for each guest player so that they can receive messages from the host while waiting.


     *
     *
     * @return A void, but it is not used anywhere
     *
     * @docauthor Trelent
     */
    private void sendChallengeSuccess() {
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.createChallengeSuccessMessage();
        for(Socket socket : this.GuestList) {

            try {
                if (socket.getPort() == this.hostPlayer.getSocketToHost().getLocalPort()){
                    this.hostPlayer.inputQueue.put(messageHandler.jsonHandler.toJsonString());
                }
                else{
                    OutputStream outToClient = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(outToClient);
                    out.println(messageHandler.jsonHandler.toJsonString());
                    out.flush();
                }
            }
            catch (IOException | InterruptedException e) {throw new RuntimeException(e);}
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
     * The handleGameServer function is a function that handles the input and output streams of the game server.
     * It reads an object from the server, puts it in a queue, and then sends an object to the game server.

     *
     * @param inputStream inputStream Read the data from the server
     * @param outputStream outputStream Send data to the server
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void handleGameServer(InputStream inputStream, OutputStream outputStream) {
        while (!this.SocketToMyServer.isClosed()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String jsonString = bufferedReader.readLine();
                if (jsonString != null)// Read an object from the server
                {
                    try {
                        inputQueueFromGameServer.put(jsonString); // Put the received object in the queue
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
     * The GetMessageFromGameServer function is used to get a message from the game server.
     *
     *
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public void GetMessageFromGameServer(String jsonString){
        System.out.println(jsonString);
        Future<String> stringFuture = executorService.submit(()->{
           return jsonString;
        });
        this.setStringFuture(stringFuture);

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
     * The getSocket function takes a string as an argument and returns the socket that corresponds to it.
     *
     *
     * @param source source Get the port number of the socket
     *
     * @return The socket of the client who sent the message
     *
     * @docauthor Trelent
     */
    public Socket getSocket(String source){
        System.out.println(source);
        String[] socketSplited = source.split(":");
        String ipSource = socketSplited[0].split("/")[1];
        String portSource = socketSplited[1];
        for(Socket s : this.GuestList){
            if (s.getPort() == Integer.parseInt(portSource)){
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
            // add End game if bag is empty
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
