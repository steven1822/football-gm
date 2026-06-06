package com.footballgm.ui;

import com.footballgm.model.Player;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class PlayerCardView extends VBox {

    public PlayerCardView(Player player) {
        setSpacing(12);
        setPadding(new Insets(20));
        setStyle("""
                -fx-background-color: #2c08fc9d;
                -fx-border-color: #fd0000;
                -fx-border-width: 2;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);

        Label nameLabel = new Label(player.getFirstName() + " " + player.getLastName());
        nameLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label basicInfoLabel = new Label(player.getPosition() + " | " + player.getOverall() + " OVR");
        basicInfoLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label physicalHeader = new Label("Physical Ratings");
        physicalHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane physicalGrid = new GridPane();
        physicalGrid.setHgap(20);
        physicalGrid.setVgap(8);

        addRatingRow(physicalGrid, 0, "Speed", player.getSpeed());
        addRatingRow(physicalGrid, 1, "Strength", player.getStrength());
        addRatingRow(physicalGrid, 2, "Agility", player.getAgility());

        Label skillHeader = new Label("Key Ratings");
        skillHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane skillGrid = new GridPane();
        skillGrid.setHgap(20);
        skillGrid.setVgap(8);

        addKeyRatingsForPosition(skillGrid, player);

        getChildren().addAll(
                nameLabel,
                basicInfoLabel,
                physicalHeader,
                physicalGrid,
                skillHeader,
                skillGrid
        );
    }

    private void addRatingRow(GridPane grid, int row, String ratingName, int ratingValue) {
        Label name = new Label(ratingName + ":");
        name.setStyle("-fx-font-weight: bold;");

        Label value = new Label(String.valueOf(ratingValue));

        grid.add(name, 0, row);
        grid.add(value, 1, row);
    }

    private void addKeyRatingsForPosition(GridPane grid, Player player) {
        String position = player.getPosition();

        switch (position) {
            case "QB" -> {
                addRatingRow(grid, 0, "Throw Power", player.getThrowPowerRTG());
                addRatingRow(grid, 1, "Short Accuracy", player.getShortAcRTG());
                addRatingRow(grid, 2, "Medium Accuracy", player.getMediumAcRTG());
                addRatingRow(grid, 3, "Deep Accuracy", player.getDeepAcRTG());
                addRatingRow(grid, 4, "Pass Recognition", player.getPassRecognitionRTG());
            }
            case "RB" -> {
                addRatingRow(grid, 0, "Vision", player.getVisionRTG());
                addRatingRow(grid, 1, "Carry", player.getCarryRTG());
                addRatingRow(grid, 2, "Catch", player.getCatchRTG());
            }
            case "WR", "TE" -> {
                addRatingRow(grid, 0, "Catch", player.getCatchRTG());
                addRatingRow(grid, 1, "Short Route", player.getShortRtRTG());
                addRatingRow(grid, 2, "Medium Route", player.getMedRtRTG());
                addRatingRow(grid, 3, "Deep Route", player.getDeepRtRTG());

                if (position.equals("TE")) {
                    addRatingRow(grid, 4, "Run Block", player.getRunBlockRTG());
                }
            }
            case "OT", "OG", "C" -> {
                addRatingRow(grid, 0, "Run Block", player.getRunBlockRTG());
                addRatingRow(grid, 1, "Pass Block", player.getPassBlockRTG());
            }
            case "DT", "DE", "EDGE" -> {
                addRatingRow(grid, 0, "Tackle", player.getTackleRTG());
                addRatingRow(grid, 1, "Block Shed", player.getBlockShedRTG());
                addRatingRow(grid, 2, "Pass Rush", player.getPassRushRTG());
            }
            case "LB" -> {
                addRatingRow(grid, 0, "Tackle", player.getTackleRTG());
                addRatingRow(grid, 1, "Block Shed", player.getBlockShedRTG());
                addRatingRow(grid, 2, "Pass Rush", player.getPassRushRTG());
                addRatingRow(grid, 3, "Zone Coverage", player.getZoneRTG());
                addRatingRow(grid, 4, "Man Coverage", player.getManRTG());
            }
            case "CB", "S" -> {
                addRatingRow(grid, 0, "Man Coverage", player.getManRTG());
                addRatingRow(grid, 1, "Zone Coverage", player.getZoneRTG());
                addRatingRow(grid, 2, "Tackle", player.getTackleRTG());
                addRatingRow(grid, 3, "Catch", player.getCatchRTG());
            }
            default -> {
                addRatingRow(grid, 0, "Overall", player.getOverall());
            }
        }
    }
}
