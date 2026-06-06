package com.footballgm;

import com.footballgm.model.Player;
import com.footballgm.ui.PlayerCardView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Player samplePlayer = Player.createRandomPlayer("WR", 1.0);
        samplePlayer.setFirstName("Deandre");
        samplePlayer.setLastName("Washington");
        samplePlayer.setJerseyNumber(12);
        samplePlayer.setOverall();

        Label title = new Label("Football GM");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        PlayerCardView playerCard = new PlayerCardView(samplePlayer);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.getChildren().addAll(title, playerCard);

        Scene scene = new Scene(root, 700, 550);

        stage.setTitle("Football GM - Player Card");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
