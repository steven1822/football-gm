package com.footballgm;

import com.footballgm.debug.DebugTeamFactory;
import com.footballgm.model.Player;
import com.footballgm.model.Play;
import com.footballgm.model.Team;
import com.footballgm.ui.PlayerCardView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private VBox contentArea;

    @Override
    public void start(Stage stage) {
        Label title = new Label("Football GM Debug Center");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Button playerDebugButton = new Button("Random Player Tool");
        Button playDebugButton = new Button("Random Play Tool");

        playerDebugButton.setOnAction(event -> showRandomPlayerTool());
        playDebugButton.setOnAction(event -> showRandomPlayTool());

        HBox menu = new HBox(12);
        menu.getChildren().addAll(playerDebugButton, playDebugButton);

        contentArea = new VBox(15);
        contentArea.setPadding(new Insets(10));

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.getChildren().addAll(title, menu, contentArea);

        showRandomPlayerTool();

        Scene scene = new Scene(root, 900, 700);

        stage.setTitle("Football GM Debug Center");
        stage.setScene(scene);
        stage.show();
    }

    private void showRandomPlayerTool() {
        contentArea.getChildren().clear();

        Label sectionTitle = new Label("Random Player Generator");
        sectionTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        ComboBox<String> positionDropdown = new ComboBox<>();
        positionDropdown.getItems().addAll(
                "QB", "RB", "WR", "TE",
                "OT", "OG", "C",
                "DT", "DE", "EDGE", "LB", "CB", "S"
        );
        positionDropdown.setValue("QB");

        Button generateButton = new Button("Generate Player");

        VBox playerCardHolder = new VBox();

        generateButton.setOnAction(event -> {
            Player player = Player.createRandomPlayer(positionDropdown.getValue(), 1.0);
            playerCardHolder.getChildren().clear();
            playerCardHolder.getChildren().add(new PlayerCardView(player));
        });

        HBox controls = new HBox(10);
        controls.getChildren().addAll(positionDropdown, generateButton);

        Player startingPlayer = Player.createRandomPlayer("QB", 1.0);
        playerCardHolder.getChildren().add(new PlayerCardView(startingPlayer));

        contentArea.getChildren().addAll(sectionTitle, controls, playerCardHolder);
    }

    private void showRandomPlayTool() {
        contentArea.getChildren().clear();

        Label sectionTitle = new Label("Random Play Debugger");
        sectionTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Button generateTeamsButton = new Button("Generate Random Teams");
        Button simulatePlayButton = new Button("Simulate Play");

        Label offenseLabel = new Label("Offense: none");
        Label defenseLabel = new Label("Defense: none");

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(300);

        final Team[] offenseTeam = new Team[1];
        final Team[] defenseTeam = new Team[1];

        generateTeamsButton.setOnAction(event -> {
            offenseTeam[0] = DebugTeamFactory.createRandomTeam("Pittsburgh", "Iron");
            defenseTeam[0] = DebugTeamFactory.createRandomTeam("Chicago", "Wolves");

            offenseLabel.setText("Offense: " +
                    offenseTeam[0].getCityName() + " " +
                    offenseTeam[0].getTeamName() +
                    " | Roster Size: " + offenseTeam[0].getRoster().size());

            defenseLabel.setText("Defense: " +
                    defenseTeam[0].getCityName() + " " +
                    defenseTeam[0].getTeamName() +
                    " | Roster Size: " + defenseTeam[0].getRoster().size());

            outputArea.setText("Random teams generated.\nPress Simulate Play to test a play.");
        });

        simulatePlayButton.setOnAction(event -> {
            if (offenseTeam[0] == null || defenseTeam[0] == null) {
                outputArea.setText("Generate teams first.");
                return;
            }

            Play play = new Play(offenseTeam[0], defenseTeam[0]);

            play.decidePlay();

            switch (play.getOPlayType()) {
                case INSIDE_RUN -> play.insideRun();

                case OUTSIDE_RUN -> {
                    // Outside run is not built yet, so route it to insideRun temporarily.
                    play.insideRun();
                }

                case SHORT_THROW, MED_THROW, DEEP_THROW, QB_SCRAMBLE -> play.passPlay();

                default -> play.insideRun();
            }

            outputArea.setText(
                    "Play Result\n" +
                    "-------------------------\n" +
                    play.toString() + "\n\n" +
                    "Yards Gained: " + play.getYardsGained() + "\n" +
                    "Outcome: " + play.getPlayOutcome() + "\n" +
                    "Turnover: " + play.isTurnoverOnPlay()
            );
        });

        HBox controls = new HBox(10);
        controls.getChildren().addAll(generateTeamsButton, simulatePlayButton);

        contentArea.getChildren().addAll(
                sectionTitle,
                controls,
                offenseLabel,
                defenseLabel,
                outputArea
        );
    }

    public static void main(String[] args) {
        launch();
    }
}