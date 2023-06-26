package viewModel;

import javafx.beans.property.IntegerProperty;
import model.data.Tile;
import model.data.Word;
import java.util.ArrayList;
import java.util.Observable;

public interface ViewModel {

    // property
    IntegerProperty scoreProperty();

    // game functions
    void startGame();
    int tryPlaceWord(Word word);
    void passTurn();
    void updateTiles();
    void updatePlayerTurn();
    void challenge();
    void updatePrev();
    void updateScore();
    void updateBoard();
    void endGame();

    // getters
    ArrayList<Tile> getCurrentTiles();
    Tile[][] getBoard();
    String getName();
    int getCurrentPlayer();
    int getMyTurn();
    Observable getObservable();
}