package com.footballgm.debug;

import com.footballgm.model.Player;
import com.footballgm.model.Team;
import com.footballgm.model.Team.LineupSlot;
import javafx.scene.paint.Color;

import java.util.Random;

public class DebugTeamFactory {

    private static final Random random = new Random();

    private static final String[] FIRST_NAMES = {
            "Jake", "Marcus", "Chris", "Tyler", "Ryan", "Darius",
            "Anthony", "James", "Malik", "Brandon", "Luke", "Eric"
    };

    private static final String[] LAST_NAMES = {
            "Miller", "Johnson", "King", "Brooks", "Adams", "Walker",
            "Carter", "Thomas", "Wilson", "Brown", "Davis", "Moore"
    };

    public static Team createRandomTeam(String cityName, String teamName) {
        Team team = new Team();

        team.setCityName(cityName);
        team.setTeamName(teamName);
        team.setColor1(Color.DARKBLUE);
        team.setColor2(Color.WHITE);
        team.setColor3(Color.GRAY);

        fillStartingOffense(team);
        fillStartingDefense34(team);
        fillStartingDefense43(team);

        return team;
    }

    private static void fillStartingOffense(Team team) {
        putStarter(team, team.getStartingO(), LineupSlot.QB, "QB");
        putStarter(team, team.getStartingO(), LineupSlot.RB, "RB");

        putStarter(team, team.getStartingO(), LineupSlot.WR1, "WR");
        putStarter(team, team.getStartingO(), LineupSlot.WR2, "WR");
        putStarter(team, team.getStartingO(), LineupSlot.WR3, "WR");

        putStarter(team, team.getStartingO(), LineupSlot.TE, "TE");

        putStarter(team, team.getStartingO(), LineupSlot.LT, "OT");
        putStarter(team, team.getStartingO(), LineupSlot.RT, "OT");

        putStarter(team, team.getStartingO(), LineupSlot.LG, "OG");
        putStarter(team, team.getStartingO(), LineupSlot.RG, "OG");

        putStarter(team, team.getStartingO(), LineupSlot.C, "C");
    }

    private static void fillStartingDefense34(Team team) {
        putStarter(team, team.getD3_4startingD(), LineupSlot.MLB1, "LB");
        putStarter(team, team.getD3_4startingD(), LineupSlot.MLB2, "LB");
        putStarter(team, team.getD3_4startingD(), LineupSlot.OLB, "LB");

        putStarter(team, team.getD3_4startingD(), LineupSlot.EDGE, "EDGE");

        putStarter(team, team.getD3_4startingD(), LineupSlot.DT, "DT");
        putStarter(team, team.getD3_4startingD(), LineupSlot.LE, "DE");
        putStarter(team, team.getD3_4startingD(), LineupSlot.RE, "DE");

        putStarter(team, team.getD3_4startingD(), LineupSlot.CB1, "CB");
        putStarter(team, team.getD3_4startingD(), LineupSlot.CB2, "CB");

        putStarter(team, team.getD3_4startingD(), LineupSlot.FS, "S");
        putStarter(team, team.getD3_4startingD(), LineupSlot.SS, "S");
    }

    private static void fillStartingDefense43(Team team) {
        putStarter(team, team.getD4_3startingD(), LineupSlot.MLB, "LB");
        putStarter(team, team.getD4_3startingD(), LineupSlot.ROLB, "LB");
        putStarter(team, team.getD4_3startingD(), LineupSlot.LOLB, "LB");

        putStarter(team, team.getD4_3startingD(), LineupSlot.EDGE1, "EDGE");
        putStarter(team, team.getD4_3startingD(), LineupSlot.EDGE2, "EDGE");

        putStarter(team, team.getD4_3startingD(), LineupSlot.DT1, "DT");
        putStarter(team, team.getD4_3startingD(), LineupSlot.DT2, "DT");

        putStarter(team, team.getD4_3startingD(), LineupSlot.CB1, "CB");
        putStarter(team, team.getD4_3startingD(), LineupSlot.CB2, "CB");

        putStarter(team, team.getD4_3startingD(), LineupSlot.FS, "S");
        putStarter(team, team.getD4_3startingD(), LineupSlot.SS, "S");
    }

    private static void putStarter(Team team,
                                   java.util.HashMap<LineupSlot, Player> lineup,
                                   LineupSlot slot,
                                   String position) {
        Player player = createNamedPlayer(position);
        lineup.put(slot, player);
        team.addPlayer(player);
    }

    private static Player createNamedPlayer(String position) {
        Player player = Player.createRandomPlayer(position, 1.0);

        player.setFirstName(randomFirstName());
        player.setLastName(randomLastName());
        player.setJerseyNumber(random.nextInt(1, 100));

        return player;
    }

    private static String randomFirstName() {
        return FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
    }

    private static String randomLastName() {
        return LAST_NAMES[random.nextInt(LAST_NAMES.length)];
    }
}