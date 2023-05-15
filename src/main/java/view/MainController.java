package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import test.GUITest;

public class MainController {

    GUITest test;
    String ip, port;
    Stage stage;
    Scene scene;
    Parent root;

    @FXML
    Label titleLabel;

    public void hostButton() {

        test = GUITest.getGuiTest();
        ip = test.getIp();
        port = test.getPort();

        titleLabel.setText("Created new game at\nIP: " + ip + "\nPort: " + port);
    }

//    @FXML
//    protected void hostButton(ActionEvent event) throws IOException {
//
//        test = GUITest.getGuiTest();
//        ip = test.getIp();
//        port = test.getPort();
//
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("host-view.fxml"));
//        root = fxmlLoader.load();
//
//        // Get the controller associated with the FXML file
//        HostController hostController = fxmlLoader.getController();
//        hostController.display(ip, port);
//
//        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
//    }

    @FXML
    protected void guestButton(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("guest-view.fxml"));
        root = fxmlLoader.load();

        GuestController guestController = fxmlLoader.getController();
        guestController.initIpPort(ip, port);

        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}