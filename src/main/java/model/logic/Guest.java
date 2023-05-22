package model.logic;

import model.data.Tile.*;

import java.io.*;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class Guest {
    private Socket SocketToHost;
    private BufferedReader reader;
    private PrintWriter writer;


    private String ipAddress;
    public int score;
    public String NickName;
    public Bag bag;


    public Guest(String NickName){
        this.NickName = NickName;
        this.score = 0;
        this.bag = Bag.getBagModel();
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
    }

    public String readInput() throws IOException {
        return reader.readLine();
    }

    public void sendOutput(String message) {
        writer.println(message);
    }

}