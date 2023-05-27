package model.logic;

import model.data.Tile.Bag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
    public  void SendToHost(String text) {
        this.writer.println(text);
    }
    public String readInput() throws IOException {
        return reader.readLine();
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
}