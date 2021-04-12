module project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports com.example.main;
    opens com.example.controller;
}