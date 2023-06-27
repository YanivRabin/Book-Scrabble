package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    /**
     * The start function is the main function of the program. It loads in a scene from an fxml file,
     * and sets it as the stage's scene. The stage is then shown to the user, and when they close it,
     * we exit out of JavaFX entirely (which also closes all other windows).

     *
     * @param  stage Set the title of the window, to set the scene and to show it
     *
     * @return Nothing, so the program will not continue until the window is closed
     *
     * @docauthor Trelent
     */
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Book Scrabble");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> {
            e.consume();  // This line consumes the close request event, so the window won't close yet
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}