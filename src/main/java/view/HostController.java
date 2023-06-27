package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import viewModel.VM_Host;
import java.io.IOException;
import java.util.Objects;

public class HostController  {

    private VM_Host vm_host;

    @FXML
    Label ipLabel, portLabel, players;

    /**
     * The setVM_Host function binds the amount of players to the player label.
     *
     *
     * @param  vm Bind the amount of players to the player label
     *
     * @return A vm_host
     *
     * @docauthor Trelent
     */
    public void setVM_Host(VM_Host vm) {

        vm_host = vm;

        // bind the amount of players to the player label
        players.textProperty().bind(vm_host.playersProperty().asString());

    }

    /**
     * The display function is used to display the IP and port of the server.
     *
     *
     * @param  ip Set the text of the iplabel
     * @param  port Set the text of portlabel
     *
     * @return A jpanel object
     *
     * @docauthor Trelent
     */
    public void display(String ip, String port) {

        ipLabel.setText("IP: " + ip);
        portLabel.setText("Port: " + port);
    }

    /**
     * The backToMenuButton function is a function that allows the user to go back to the main menu.
     *
     *
     * @param  event Get the source of the event
     *
     * @return The user to the main menu
     *
     * @docauthor Trelent
     */
    @FXML
    protected void backToMenuButton(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The StartGameButton function is responsible for loading the board view and sending it to the BoardViewController.
     *
     *
     * @param  actionEvent Get the source of the event
     *
     * @return A void, so the return type is void
     *
     * @docauthor Trelent
     */
    public void StartGameButton(ActionEvent actionEvent) {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Board-view.fxml"));
            Parent root = fxmlLoader.load();

            // send the vm to the board controller
            BoardViewController boardController = fxmlLoader.getController();
            boardController.setViewModel(vm_host);

            Scene scene = new Scene(root);
            Node source = (Node) actionEvent.getSource();
            Stage stage = (Stage) source.getScene().getWindow();

            stage.setX(250);
            stage.setY(50);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
