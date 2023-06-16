module view.bookscrabble {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;
//    requires com.jfoenix;



    opens view to javafx.fxml;
    exports view;
}