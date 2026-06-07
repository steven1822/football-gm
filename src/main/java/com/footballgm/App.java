
package com.footballgm;

import com.footballgm.model.Player;
import com.footballgm.ui.PlayerCardView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Label title = new Label("Football GM");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        ComboBox<String> positionDropdown = new ComboBox<>();
        positionDropdown.getItems().addAll(
                "QB", "RB", "WR", "TE",
                "OT", "OG", "C",
                "DT", "DE", "EDGE", "LB", "CB", "S"
        );
        positionDropdown.setValue("QB");

        Button generateButton = new Button("Generate Random Player");

        VBox playerCardHolder = new VBox();

        Player firstPlayer = createPlayer(positionDropdown.getValue());
        playerCardHolder.getChildren().add(new PlayerCardView(firstPlayer));

        generateButton.setOnAction(event -> {
            String selectedPosition = positionDropdown.getValue();

            Player newPlayer = createPlayer(selectedPosition);

            playerCardHolder.getChildren().clear();
            playerCardHolder.getChildren().add(new PlayerCardView(newPlayer));
        });

        HBox controls = new HBox(10);
        controls.getChildren().addAll(positionDropdown, generateButton);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.getChildren().addAll(title, controls, playerCardHolder);

        Scene scene = new Scene(root, 700, 600);

        stage.setTitle("Football GM - Player Card");
        stage.setScene(scene);
        stage.show();
    }

    private Player createPlayer(String position) {
        Player player = Player.createRandomPlayer(position, 1.0);

        // This is safe to keep even if createRandomPlayer already calls setOverall().
        player.setOverall();

        return player;
    }

    public static void main(String[] args) {
        launch();
    }
}