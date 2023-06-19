module view.bookscrabble {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires junit;
//    requires junit;


    opens view to javafx.fxml;
    exports view;
}