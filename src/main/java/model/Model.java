package model;

import model.logic.Guest;
import model.logic.Host;
import model.logic.MyServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;


public class Model extends Observable {

    Host hostModel;
    Guest guestModel;
    int playerScore;
    List<String> playerHand = new ArrayList<>();
    String playerQuery = new String();

    public Model(){
        this.hostModel = Host.getModel();
    }


    /**
     * The getHostServer function returns the hostModel object.
     *
     *
     *
     * @return The host model
     *
     * @docauthor Trelent
     */
    public Host getHostServer() {return hostModel;}
    private  static class ModelHolder{ public static final Model m = new Model();}
    /**
     * The getModel function is a static function that returns the Model object.
     * This allows other classes to access the model without having to create their own instance of it.

     *
     *
     * @return The model object
     *
     * @docauthor Trelent
     */
    public static Model getModel() {return ModelHolder.m;}

}