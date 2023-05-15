package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class GuestController {

    String ip, port;
    @FXML
    TextField ipTextField, portTextField;
    @FXML
    Label guestLabel;

    public void initIpPort(String ip, String port) {

        this.ip = ip;
        this.port = port;
    }

    public void checkIpPort() {

        if (ip == null || port == null)
            guestLabel.setText("Error!");

        if (ip.equals(ipTextField.getText()) && port.equals(portTextField.getText()))
            guestLabel.setText("Connected to game");

        else
            guestLabel.setText("Error!\nplease check again the IP and Port");
    }

    @FXML
    protected void backToMenuButton(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
