package org.example.persoonlijke_opdr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Correct the path here
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/persoonlijke_opdr/mainview.fxml"));
        primaryStage.setTitle("Gas Station Finder");
        primaryStage.setScene(new Scene(root, 400, 200));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
