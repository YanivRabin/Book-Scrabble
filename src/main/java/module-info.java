module view.bookscrabble {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires junit;


    opens view to javafx.fxml;
    exports view;
    exports viewModel;
    exports model.data;
    exports model.logic;
}