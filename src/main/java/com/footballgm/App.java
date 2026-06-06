package com.footballgm;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Label title = new Label("Football GM");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label subtitle = new Label("JavaFX + SQLite project started.");

        Button startButton = new Button("Start New League");
        startButton.setOnAction(event -> {
            subtitle.setText("Next step: create teams and players.");
        });

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(title, subtitle, startButton);

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("Football GM");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
