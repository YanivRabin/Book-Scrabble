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
    void placeTile(Tile selectedTile, int row, int col);
    void removeTile(int row, int column);
    void passTurn();
    void updateTiles();
    void updateBoard();
    void updatePlayerTurn();

    // getters
    ArrayList<Tile> getCurrentTiles();
    Tile[][] getBoard();
    String getName();
    int getCurrentPlayer();
    int getMyTurn();
    Observable getObservable();
}

