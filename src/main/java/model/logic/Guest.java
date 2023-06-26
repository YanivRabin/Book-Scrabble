package model.logic;

import com.google.gson.JsonObject;
import model.data.Tile;
import model.data.Tile.Bag;
import model.data.Word;

import java.io.*;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Guest extends Observable {

    //Logic Members
    private Socket SocketToHost;
    private BufferedReader reader;
    private PrintWriter writer;
    private String ipAddress;
    Host HostServer; // The Host this Guest connected to
    static ExecutorService executorService = Executors.newFixedThreadPool(8); // only for one host
    BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    //Data-Game Members
    public Player player;
    public String NickName;

    /**
     * The Guest function is a constructor for the Guest class.
     * It takes in a String NickName and sets it as the NickName of the Guest object.

     *
     * @param NickName NickName Set the nickname of the guest object
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public Guest(String NickName){
        this.NickName = NickName;
    }

    /**
     * The getSocketToHost function returns the SocketToHost variable.
     *
     *
     *
     * @return The socket to the host
     *
     * @docauthor Trelent
     */
    public Socket getSocketToHost() {
        return SocketToHost;
    }
    /**
     * The getNickName function returns the NickName of the user.
     *
     *
     *
     * @return The value of the nickname variable
     *
     * @docauthor Trelent
     */
    public String getNickName() {
        return NickName;
    }
    /**
     * The getReader function returns the BufferedReader object that was created in the constructor.
     *
     *
     *
     * @return The reader variable, which is a bufferedreader
     *
     * @docauthor Trelent
     */
    public BufferedReader getReader() {
        return reader;
    }

    /**
     * The getWriter function returns the PrintWriter object that is used to write
     * to the output stream.

     *
     *
     * @return The writer object
     *
     * @docauthor Trelent
     */
    public PrintWriter getWriter() {
        return writer;
    }

    /**
     * The getIpAddress function returns the ipAddress of a given object.
     *
     *
     *
     * @return The ipaddress variable
     *
     * @docauthor Trelent
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * The setNickName function sets the value of NickName to a new value.
     *
     *
     * @param NickName NickName Set the nickname of the player
     *
     * @return Nothing, so it is void
     *
     * @docauthor Trelent
     */
    public void setNickName(String NickName){
        this.NickName = NickName;
    }

    /**
     * The CreateSocketToHost function creates a socket to the host and sets up the input and output streams.
     *
     *
     * @param HostIp HostIp Connect to the host
     * @param Port Port Specify the port number of the host
     *
     * @return The sockettohost object, which is a socket to the host
     *
     * @docauthor Trelent
     */
    public void CreateSocketToHost(String HostIp, int Port) throws IOException {
        if (this.validateIP(HostIp)){
            this.SocketToHost = new Socket(HostIp, Port);
            this.reader = new BufferedReader(new InputStreamReader(SocketToHost.getInputStream()));
            this.writer = new PrintWriter(SocketToHost.getOutputStream(), true);
            this.ipAddress = SocketToHost.getInetAddress().getHostAddress();
            sendNewPlayerJoinedMessage();
            executorService.submit(this::handleRequests);
            executorService.execute(()->{
                try {
                    handleHost(this.SocketToHost.getInputStream(), this.SocketToHost.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else {
            System.out.println(this.NickName + " can't connect to host");
        }
    }

    /**
     * The sendNewPlayerJoinedMessage function is used to send a message to the host that a new player has joined.
     * This function is called when the client connects to the server.

     *
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public void sendNewPlayerJoinedMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.SocketToHost.getInetAddress());
        stringBuilder.append(":");
        stringBuilder.append(this.SocketToHost.getLocalPort());
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.createNewPlayerJoinedMessage(this.NickName);
        this.SendToHost(messageHandler.jsonHandler);
    }

    // create all options Messages
    /**
     * The SendTryPlaceWordMessage function is used to send a message to the host
     * that contains information about the word that was just placed on the board.
     * The function takes in a source, destination, word, row and column of where it was placed on the board and whether or not it is vertical.
     * It then creates a new MessageHandler object which will be used to create our JSON string. We then call CreateTryPlaceWordMessage from MessageHandler which will take in all of our parameters and create an appropriate JSON string for us. Finally we call SendToHost with this newly created JSON String as its parameter so that we can send it off
     *
     * @param source source Identify the player who sent the message
     * @param destination destination Specify the destination of the message
     * @param word word Send the word that is being placed on the board
     * @param row row Specify the row of the first letter in a word
     * @param column column Determine the column of the first letter in a word
     * @param vertical vertical Determine the orientation of the word
     *
     * @return A void
     *
     * @docauthor Trelent
     */
    public void SendTryPlaceWordMessage(String source, String destination, String word,
                                          int row, int column, boolean vertical){
        System.out.println(this.player.getNickName()+": try place word");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.SocketToHost.getInetAddress());
        stringBuilder.append(":");
        stringBuilder.append(this.SocketToHost.getLocalPort());
        String socketSource = stringBuilder.toString();
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateTryPlaceWordMessage(source, destination, word, this.player.prevScore,row, column,
                vertical, this.player.getCurrentTiles(), socketSource);
        this.SendToHost(messageHandler.jsonHandler);
    }
    /**
     * The SendChallengeMessage function is used to send a challenge message to the host.
     *
     *
     * @param prevBoard prevBoard Send the previous board to the opponent
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void SendChallengeMessage(String prevBoard){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.SocketToHost.getInetAddress());
        stringBuilder.append(":");
        stringBuilder.append(this.SocketToHost.getLocalPort());
        String socketSource = stringBuilder.toString();
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateChallengeMessage(socketSource, prevBoard);
        this.SendToHost(messageHandler.jsonHandler);
    }

    /**
     * The sendPassTurnMessage function is used to send a message to the host that the player has passed their turn.
     *
     *
     *
     * @return A json string with the following structure:
     *
     * @docauthor Trelent
     */
    public void sendPassTurnMessage() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.SocketToHost.getInetAddress());
        stringBuilder.append(":");
        stringBuilder.append(this.SocketToHost.getLocalPort());
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.createPassTurnMessage();
        this.SendToHost(messageHandler.jsonHandler);
    }

    /**
     * The SendToHost function is used to send a JsonHandler object to the host.
     *
     *
     * @param json json Send a jsonhandler object to the host
     *
     * @return A void
     *
     * @docauthor Trelent
     */
    public  void SendToHost(JsonHandler json) {
        this.writer.println(json.toJsonString());
        this.writer.flush();
    }

    /**
     * The sendEndGame function is used to send a message to the host that the game has ended.
     * The function takes no parameters and returns nothing.

     *
     *
     * @return A json object
     *
     * @docauthor Trelent
     */
    public void sendEndGame() {
        String winner = "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.SocketToHost.getInetAddress());
        stringBuilder.append(":");
        stringBuilder.append(this.SocketToHost.getLocalPort());
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.createEndGameMessage(winner);
        this.SendToHost(messageHandler.jsonHandler);
    }

    /**
     * The handleHost function is a function that reads the input stream from the host and puts it into a queue.
     *
     *
     * @param inputStream inputStream Read the data from the server
     * @param outputStream outputStream Write objects to the server
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    public void handleHost(InputStream inputStream, OutputStream outputStream) {
        while (!this.SocketToHost.isClosed()) {
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
     * The handleRequests function is a function that handles all the requests from the server.
     * It takes in a json string and converts it to a JsonObject, then checks what type of message it is.
     * Depending on what type of message it is, different actions are taken. For example if the messageType was &quot;start game&quot;,
     * then we create a new player object with information about our tiles and host nickname etc... Then we notify observers that they should start their game now.

     *
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    public void handleRequests() {
        while (!this.SocketToHost.isClosed()) {
            try {
                String jsonString = inputQueue.take(); //blocking call
                System.out.println(jsonString);
                JsonObject json = JsonHandler.convertStringToJsonObject(jsonString);
                switch (json.get("MessageType").getAsString()){
                    case "start game":
                        this.player = new Player(this.ipAddress, this.NickName, 0);
                        this.player.addTiles(json.get("StartTiles").getAsString());
                        this.player.hostNickName = json.get("Source").getAsString();
                        this.player.playerIndex = json.get("PlayerIndex").getAsInt();
                        setChanged();
                        notifyObservers("start game," + this.player.getHostNickName());
                        break;
                    case "success":
                        switch (json.get("Action").getAsString()) {
                            case "try place word":
                                System.out.println(this.NickName + "Try Place Word: " + "Success");
                                this.player.addScore(Integer.parseInt(json.get("NewScore").getAsString()));
                                this.player.setCurrentTiles(json.get("NewCurrentTiles").getAsString());
                                break;
                            case "challenge":
                                // no use
                                System.out.println(this.NickName + "Challenge: " + "Success");
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
                                setChanged();
                                notifyObservers("challenge fail");
                                break;
                        }
                        break;
                    case "succeeded in challenging you":
                        System.out.println(this.NickName + ": i have been complicated");
                        this.player.currentScore = json.get("PrevScore").getAsInt();
                        this.player.prevScore = json.get("PrevScore").getAsInt();
                        setChanged();
                        notifyObservers("update score");
                        break;
                    case "update board":
                        this.player.setCurrentBoard(json.get("Board").getAsString());
                        setChanged();
                        notifyObservers("update board");
                        break;
                    case "pass turn":
                        setChanged();
                        notifyObservers("pass turn");
                        break;
                    case "end game":
                        setChanged();
                        notifyObservers("end game,"+json.get("Message").getAsString());
                        Thread.sleep(5000);
                        break;
                    case "challenge alive":
                        setChanged();
                        notifyObservers("challenge alive");
                        break;
                    case "update prev to current":
                        this.player.prevScore = this.player.currentScore;
                        this.player.prevBoard = this.player.currentBoard;
                        break;
                    case "challenge success":
                        setChanged();
                        notifyObservers("challenge success");
                        break;
                }
            }
            catch (InterruptedException e) { throw new RuntimeException(e); }
        }
    }

    /**
     * The validateIP function takes a String as an argument and returns true if the string is a valid IP address.
     *
     *
     * @param ipAddress ipAddress Pass in the ip address to be validated
     *
     * @return A boolean value
     *
     * @docauthor Trelent
     */
    public boolean validateIP(String ipAddress) {
        String[] parts = ipAddress.split("\\.");

        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * The Disconnect function is used to disconnect the client from the server.
     * It closes all of the streams and sockets that were opened during connection,
     * and removes itself from its host's guest list.

     *
     *
     * @return A boolean
     *
     * @docauthor Trelent
     */
    public void Disconnect(){
        executorService.shutdownNow();
        if (HostServer != null) {
            for (Socket host : HostServer.GuestList) {
                if(host.getPort()==SocketToHost.getLocalPort()) {
                    HostServer.GuestList.remove(host);
                    break;
                }
            }
        }
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (SocketToHost != null) {
                SocketToHost.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}