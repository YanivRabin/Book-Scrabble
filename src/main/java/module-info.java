module view.bookscrabble {
    requires javafx.controls;
    requires javafx.fxml;
//    requires junit;


    opens view to javafx.fxml;
    exports view;
}