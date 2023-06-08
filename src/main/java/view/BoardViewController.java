package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


import java.net.URL;
import java.util.ResourceBundle;

public class BoardViewController implements Initializable {
    @FXML
    private GridPane boardGrid;

    @FXML
    private Text Title;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // You can perform initialization tasks here
    }

    // Define additional methods and event handlers as needed

    // Example method to handle button click
    @FXML
    private void handleButtonClick() {
        System.out.println("Button clicked!");
    }

    // Example method to handle pane click
    @FXML
    public void handlePaneClick(MouseEvent event) {
        Pane clickedPane = (Pane) event.getSource();

        Integer rowIndex = GridPane.getRowIndex(clickedPane);
        Integer columnIndex = GridPane.getColumnIndex(clickedPane);

        int row = rowIndex.intValue();
        int column = columnIndex.intValue();

        System.out.println("Clicked pane at row: " + row + ", column: " + column);
        // Perform actions based on the row and column

    }

    public void EndTurnButtonClick(ActionEvent actionEvent) {
        System.out.println("End Turn Clicked");
    }

    public void EndGameButtonClick(ActionEvent actionEvent) {
        System.out.println("End Game Clicked");

    }

    public void TryPlaceWordButtonClick(ActionEvent actionEvent) {
        System.out.println("Try Place Word Clicked");
    }


}




