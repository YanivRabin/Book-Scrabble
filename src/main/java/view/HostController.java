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

    public void setVM_Host(VM_Host vm) {

        vm_host = vm;

        // bind the amount of players to the player label
        players.textProperty().bind(vm_host.playersProperty().asString());

    }

    public void display(String ip, String port) {

        ipLabel.setText("IP: " + ip);
        portLabel.setText("Port: " + port);
    }

    @FXML
    protected void backToMenuButton(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

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
