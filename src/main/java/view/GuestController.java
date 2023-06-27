package view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import viewModel.VM_Guest;
import java.io.IOException;
import java.util.Objects;

public class GuestController {

    private VM_Guest vm_guest;

    @FXML
    TextField ipTextField, portTextField;

    @FXML
    Label guestLabel, message ,ipLabel, portLabel, waiting;

    @FXML
    Button enter;

    /**
     * The initialize function is called when the FXML file is loaded.
     * It sets up the GUI and makes sure that it's ready to be used.

     *
     *
     * @return A void, so it cannot be used in the if statement
     *
     * @docauthor Trelent
     */
    @FXML
    public void initialize() {
        Platform.runLater(() -> ipTextField.requestFocus());
    }

    /**
     * The onIpTextFieldKeyPressed function is a function that allows the user to press tab on their keyboard
     * and move from the ipTextField to the portTextField. This function also consumes any other key presses, so
     * that they do not affect anything else in this program.

     *
     * @param  event Determine which key was pressed
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    @FXML
    public void onIpTextFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB) {
            portTextField.requestFocus();
            event.consume();
        }
    }

    /**
     * The setVM_Guest function sets the VM_Guest object for this class.
     *
     *
     * @param  vm Set the vm_guest variable to the vm_guest object passed in
     *
     * @return The vm_guest object
     *
     * @docauthor Trelent
     */
    public void setVM_Guest(VM_Guest vm) {
        vm_guest = vm;
    }

    /**
     * The backToMenuButton function is a function that allows the user to go back to the main menu.
     *
     *
     * @param  event Get the source of the button that was clicked
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
     * The checkIpPort function is responsible for checking if the IP and Port entered by the user are valid.
     * If they are, it will connect to the server and wait for a game to start.
     *
     *
     * @param  event Get the source of the event
     *
     * @return True if the connection is established and false otherwise
     *
     * @docauthor Trelent
     */
    public void checkIpPort(ActionEvent event) {

        // check if ip and port are entered
        if (Objects.equals(ipTextField.getText(), "") || Objects.equals(portTextField.getText(), "")) {
            message.setText("Error: enter IP and Port");
        }

        // if the ip and port are correct
        else if (vm_guest.connectToServer(ipTextField.getText(), Integer.parseInt(portTextField.getText()))) {

            // display wait status
            waiting.setVisible(true);
            message.setVisible(false);
            guestLabel.setVisible(false);
            ipLabel.setVisible(false);
            portLabel.setVisible(false);
            ipTextField.setVisible(false);
            portTextField.setVisible(false);
            enter.setVisible(false);

            Thread gameStartThread = new Thread(() -> {

                // check if the host start game
                if (vm_guest.isGameStart()) {

                    Platform.runLater(() -> {

                        // if game started then move to the board
                        System.out.println("Game started");
                        try {

                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Board-view.fxml"));
                            Parent root = fxmlLoader.load();

                            BoardViewController boardController = fxmlLoader.getController();
                            // send the viewModel to the board controller
                            boardController.setViewModel(vm_guest);

                            Scene scene = new Scene(root);
                            Node source = (Node) event.getSource();
                            Stage stage = (Stage) source.getScene().getWindow();
                            stage.setX(250);
                            stage.setY(50);
                            stage.setResizable(false);
                            stage.setScene(scene);
                            stage.show();
                        }
                        catch (IOException e) { e.printStackTrace(); }
                    });
                }
            });
            gameStartThread.start();
        }
        else {
            message.setText("Error: wrong IP / Port");
        }
    }
}
