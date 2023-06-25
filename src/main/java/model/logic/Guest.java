package model.logic;

import com.google.gson.JsonObject;
import model.data.Tile;
import model.data.Tile.Bag;
import model.data.Word;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Guest {
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


    public Guest(String NickName){
        this.NickName = NickName;
    }

    public Socket getSocketToHost() {
        return SocketToHost;
    }
    public String getNickName() {
        return NickName;
    }
    public BufferedReader getReader() {
        return reader;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void CreateProfile(String NickName){
        this.NickName = NickName;

        // add Photo or avatar
    }

    public void CreateSocketToHost(String HostIp, int Port) throws IOException {
        if (this.validateIP(HostIp)){
            this.SocketToHost = new Socket(HostIp, Port);
            this.reader = new BufferedReader(new InputStreamReader(SocketToHost.getInputStream()));
            this.writer = new PrintWriter(SocketToHost.getOutputStream(), true);
            this.ipAddress = SocketToHost.getInetAddress().getHostAddress();
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

    // create all options Messages
    public void SendTryPlaceWordMessage(String source, String destination, String word,
                                          int row, int column, boolean vertical){
        System.out.println(this.player.getNickName()+": try place word");
        if(!this.player.usingCurrentTiles(word)){
            System.out.println("You are not using your tiles");
        }
        else{
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
    }
    public void SendChallengeMessage(String source, String destination, String word,
                                       int row, int column, boolean vertical){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.SocketToHost.getInetAddress());
        stringBuilder.append(":");
        stringBuilder.append(this.SocketToHost.getLocalPort());
        String socketSource = stringBuilder.toString();
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateChallengeMessage(source, destination, word, row, column,
                vertical, this.player.getCurrentTiles(), socketSource);
        this.SendToHost(messageHandler.jsonHandler);
    }

    public  void SendToHost(JsonHandler json) {
        this.writer.println(json.toJsonString());
        this.writer.flush();
    }

    // option d in {}
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
                        break;
                    case "success":
                        switch (json.get("Action").getAsString()) {
                            case "try place word":
                                System.out.println(this.NickName + "Try Place Word: " + "Success");
                                this.player.addScore(Integer.parseInt(json.get("NewScore").getAsString()));
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
                        this.player.currentScore = json.get("PrevScore").getAsInt();
                        this.player.prevScore = json.get("PrevScore").getAsInt();
                        this.player.currentBoard = this.player.prevBoard;
                        this.player.currentTiles = this.player.prevTiles;
                        break;
                    case "update board":
                        this.player.setCurrentBoard(json.get("Board").getAsString());
                        System.out.println(this.NickName + " updated Board");
                        break;
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

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