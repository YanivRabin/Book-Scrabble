package model.logic;

import model.data.Tile;
import model.data.Tile.Bag;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Guest {


    //Logic Members
    private Socket SocketToHost;
    private BufferedReader reader;
    private PrintWriter writer;
    private String ipAddress;
    Host HostServer; // The Host this Guest connected to

    //Data-Game Members
    public String NickName;
    public Bag bag;
    public int score;
    public ArrayList<Tile> currentTiles = new ArrayList();

    public Guest(String NickName){
        this.NickName = NickName;
        this.score = 0;
        this.bag = Bag.getBagModel();
        this.GenerateTiles(7); // remove outside
        getCurrentTiles();
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
        Thread clientThread = new Thread(() -> {
            try {
                GetFromHost();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clientThread.start();
    }

    public void Disconnect(){
        if (HostServer != null) {
            for (Socket host : HostServer.GuestList) {
                if(host.getPort()==SocketToHost.getLocalPort()) {
                    HostServer.GuestList.remove(host);
                    break;
                }
            }
        }
    }

    // add validate string to host

    public  void SendToHost(String line) {
        String[] text = line.split(",");
        String word = text[1];
        int c = 0;
        int counterNull = 0;
        for(int i = 0 ; i < word.length();i++){
            if(word.charAt(i) != '_'){
                // checking null
                for(Tile t : this.currentTiles){
                    if(t.letter == word.charAt(i)){
                        c++;
                    }
                }
            }
            else{
                counterNull++;
            }
        }
        if(c == word.length() - counterNull){
            this.writer.println(text);
        }
        else{
            System.out.println(this.NickName + "You don't have the tiles, try again");
        }
    }
    public void GetFromHost() throws IOException {
        String line = this.reader.readLine();
        System.out.println(line);
        String[] text = line.split(",");
        if (line == "Not Legal") {
            System.out.println(this.NickName + "Didn't success, try again");
        } else {
            switch (text[0]) {
                case "Success":
                    //Handle score
                    int placeWordScore = Integer.parseInt(text[2]);
                    this.score += placeWordScore;
                    //Handle Bag
                    //reduce the tiles from currentTiles
                    //Add tiles from getRand at bag  == word.size() without nulls


                    break;
                case "Not":
                    break;

            }

        }

    }
    public void GenerateTiles(int number){
        for(int i = 0 ; i < number ; i++){
            this.currentTiles.add(this.bag.getRand());
        }
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

    public void getCurrentTiles() {
        System.out.print("current tiles: ");
        for(int i = 0 ; i < this.currentTiles.size(); i++){
            System.out.print(this.currentTiles.get(i).letter);
        }
        System.out.println();
    }
}