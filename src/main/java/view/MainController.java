package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import viewModel.VM_Guest;
import viewModel.VM_Host;
import java.io.IOException;
import java.util.Random;

public class MainController {

    Stage stage;
    Scene scene;
    Parent root;

    static String name;
    VM_Host vm_host;
    VM_Guest vm_guest;

    @FXML
    TextField playerName;

    @FXML
    AnchorPane anchorPane;

    @FXML
    protected void hostButton(ActionEvent event) throws IOException {

        name = playerName(playerName.getText().trim());
        vm_host = new VM_Host(name); // create the server
        String ip = vm_host.getIp();
        int port = vm_host.getPort();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("host-view.fxml"));
        root = fxmlLoader.load();

        // send the vm to the next window and display the ip and port
        HostController hostController = fxmlLoader.getController();
        hostController.display(ip, String.valueOf(port));
        hostController.setVM_Host(vm_host);

        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void guestButton(ActionEvent event) throws IOException {

        name = playerName(playerName.getText().trim());
        vm_guest = new VM_Guest(name);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("guest-view.fxml"));
        root = fxmlLoader.load();

        // send the vm to the next window
        GuestController guestController = fxmlLoader.getController();
        guestController.setVM_Guest(vm_guest);

        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static String getName() { return name; }

    public String playerName(String name) {

        if (!name.equals("")) {
            return name;
        }
        else {

            // Generates a guest with random 4-digit number
            Random random = new Random();
            int randomNumber = random.nextInt(9000) + 1000;
            return "Guest" + randomNumber;
        }
    }
}