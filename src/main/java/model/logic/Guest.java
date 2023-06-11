package model.logic;

import com.google.gson.JsonObject;
import model.data.Tile;
import model.data.Tile.Bag;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Guest {
    //Logic Members
    private Socket SocketToHost;
    private BufferedReader reader;
    private PrintWriter writer;
    private String ipAddress;
    Host HostServer; // The Host this Guest connected to
    static ExecutorService executorService = Executors.newFixedThreadPool(1); // only for one host


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
        this.SocketToHost = new Socket(HostIp, Port);
        this.reader = new BufferedReader(new InputStreamReader(SocketToHost.getInputStream()));
        this.writer = new PrintWriter(SocketToHost.getOutputStream(), true);
        this.ipAddress = SocketToHost.getInetAddress().getHostAddress();
        /*Thread clientThread = new Thread(() -> {
            try {
                GetFromHost();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clientThread.start();*/
        executorService.execute(()->{
            try {
                GetFromHost(this.SocketToHost.getInputStream(), this.SocketToHost.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // create all options Messages
    public void SendTryPlaceWordMessage(String source, String destination, String word,
                                          int row, int column, boolean vertical){
        System.out.println(this.player.getNickName()+": try place word");
        if(!this.player.usingCurrentTiles(word)){
            System.out.println("You are not using your tiles");
        }
        else{
            MessageHandler messageHandler = new MessageHandler();
            messageHandler.CreateTryPlaceWordMessage(source, destination, word, row, column,
                    vertical, this.player.getCurrentTiles());
            this.SendToHost(messageHandler.jsonHandler);
        }
    }
    public void SendChallengeMessage(String source, String destination, String word,
                                       int row, int column, boolean vertical){
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.CreateChallengeMessage(source, destination, word, row, column,
                vertical, this.player.getCurrentTiles());
        this.SendToHost(messageHandler.jsonHandler);
    }

    public  void SendToHost(JsonHandler json) {
        this.writer.println(json.toJsonString());
        this.writer.flush();
    }

    public void GetFromHost(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedReader readerFromHost = new BufferedReader(new InputStreamReader(inputStream));
        String jsonString = readerFromHost.readLine();
        System.out.println(jsonString);
        JsonObject json = JsonHandler.convertStringToJsonObject(jsonString);
        switch (json.get("MessageType").getAsString()){
            case "start game":
                this.player = new Player(this.ipAddress, this.NickName, 0);
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