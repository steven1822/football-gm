package com.footballgm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// Class modeling a single football play
public class Play {

    private static final String BLOCK_SHED = "Block Shed";
    private static final String BLOCK_HELD = "Block Held";

    private final Random random = new Random();

    // Offensive and defensive play calls
    public enum PlayType {
        // Offensive plays
        DEEP_THROW,
        MED_THROW,
        SHORT_THROW,
        QB_SCRAMBLE,
        INSIDE_RUN,
        OUTSIDE_RUN,

        // Defensive plays
        ZONE_BLITZ,
        ZONE,
        MAN_BLITZ,
        MAN
    }

    public enum PlayOutcome {
        OUT_OF_BOUNDS,
        TACKLE,
        FUMBLE,
        PASS_INCOMPLETE,
        INTERCEPTION,
        FUMBLE_RECOVERY,
        SACK
    }

    public enum CoverageZone {
        DEEP_LEFT,
        DEEP_MIDDLE,
        DEEP_RIGHT,
        MED_LEFT,
        MED_MIDDLE,
        MED_RIGHT,
        SHORT_LEFT,
        SHORT_MIDDLE,
        SHORT_RIGHT
    }

    private enum RunDirection {
        LEFT,
        RIGHT
    }

    private final Player emptyPlayer = new Player("EMPTY", "PLAYER", "QB");

    private Team offense;
    private Team defense;

    private PlayType offensivePlayType;
    private PlayType defensivePlayType;
    private PlayOutcome playOutcome;

    // Time from this snap to the next snap, in seconds
    private int timeOfPlay;

    // Yards gained by the offense; can be negative
    private int yardsGained;

    private boolean turnoverOnPlay;

    private Player tackler;
    private Player ballHandler;
    private Player fumbler;
    private Player forcedFumbler;

    private HashMap<CoverageZone, Player> assignedCoverages;
    private HashMap<Player, Player> assignedManCoverages;

    public Play(Team offense, Team defense) {
        this.offense = offense;
        this.defense = defense;
        this.assignedCoverages = createEmptyCoverageMap();
        this.assignedManCoverages = new HashMap<>();
    }

    /**
     * Decides offensive and defensive play calls, then simulates the selected play.
     */
    public int simulate() {
        if (offensivePlayType == null || defensivePlayType == null) {
            decidePlay();
        }

        return switch (offensivePlayType) {
            case INSIDE_RUN -> insideRun();
            case OUTSIDE_RUN -> outsideRun();
            case DEEP_THROW, MED_THROW, SHORT_THROW, QB_SCRAMBLE -> passPlay();
            default -> passPlay();
        };
    }

    /**
     * Simulates an inside run play.
     */
    public int insideRun() {
        return runPlay(PlayType.INSIDE_RUN);
    }

    /**
     * Placeholder outside run.
     * For now this uses the same run engine as insideRun(), but keeps the play type separate.
     */
    public int outsideRun() {
        return runPlay(PlayType.OUTSIDE_RUN);
    }

    private int runPlay(PlayType runType) {
        resetPlayResult();

        this.offensivePlayType = runType;
        this.ballHandler = getOffensePlayer(Team.LineupSlot.RB);

        int yards = -3;
        RunDirection direction = randomDirection();

        switch (getDefensiveFormation()) {
            case D3_4 -> yards = simulateRunAgainst34(yards, direction);
            case D4_3 -> yards = simulateRunAgainst43(yards, direction);
        }

        return finishPlay(yards);
    }

    private int simulateRunAgainst34(int yards, RunDirection direction) {
        int base = 5;

        // First level: defensive line
        while (yards < 3) {
            yards++;
            base += 2;

            if (direction == RunDirection.LEFT) {
                if (tryShedAndTackle(Team.LineupSlot.C, Team.LineupSlot.DT, base, yards)) return yards;
                if (tryShedAndTackle(Team.LineupSlot.LT, Team.LineupSlot.LE, base, yards)) return yards;
                if (tryShedAndTackle(Team.LineupSlot.RT, Team.LineupSlot.RE, base, yards)) return yards;
            } else {
                if (tryShedAndTackle(Team.LineupSlot.C, Team.LineupSlot.DT, base, yards)) return yards;
                if (tryShedAndTackle(Team.LineupSlot.RT, Team.LineupSlot.RE, base, yards)) return yards;
                if (tryShedAndTackle(Team.LineupSlot.LT, Team.LineupSlot.LE, base, yards)) return yards;
            }

            if (rbVisionTest()) {
                yards++;
            }
        }

        // Second level: linebackers
        base = 23;
        while (yards < 10) {
            yards++;

            if (tryShedAndTackle(Team.LineupSlot.LG, Team.LineupSlot.MLB1, base, yards)) return yards;
            if (tryShedAndTackle(Team.LineupSlot.RG, Team.LineupSlot.MLB2, base, yards)) return yards;

            if (rbVisionTest()) {
                yards++;
            }
        }

        return simulateSafetyPursuit(yards);
    }

    private int simulateRunAgainst43(int yards, RunDirection direction) {
        int base = 5;

        // First level: defensive tackles
        while (yards < 3) {
            yards++;
            base += 2;

            if (direction == RunDirection.LEFT) {
                if (tryShedAndTackle(Team.LineupSlot.LG, Team.LineupSlot.DT1, base, yards)) return yards;
                if (tryShedAndTackle(Team.LineupSlot.RG, Team.LineupSlot.DT2, base, yards)) return yards;
            } else {
                if (tryShedAndTackle(Team.LineupSlot.RG, Team.LineupSlot.DT1, base, yards)) return yards;
                if (tryShedAndTackle(Team.LineupSlot.LG, Team.LineupSlot.DT2, base, yards)) return yards;
            }

            if (rbVisionTest()) {
                yards++;
            }
        }

        // Second level: linebackers
        base = 13;
        while (yards < 10) {
            yards++;

            if (tryShedAndTackle(Team.LineupSlot.C, Team.LineupSlot.MLB, base, yards)) return yards;
            if (tryShedAndTackle(Team.LineupSlot.RT, Team.LineupSlot.ROLB, base, yards)) return yards;
            if (tryShedAndTackle(Team.LineupSlot.LT, Team.LineupSlot.LOLB, base, yards)) return yards;

            if (rbVisionTest()) {
                yards++;
            }
        }

        return simulateSafetyPursuit(yards);
    }

    private boolean tryShedAndTackle(Team.LineupSlot blockerSlot,
                                     Team.LineupSlot defenderSlot,
                                     int base,
                                     int currentYards) {
        String shedResult = shedBlockAttempt(
                getOffensePlayer(blockerSlot),
                getDefensePlayer(defenderSlot),
                base
        );

        if (BLOCK_SHED.equals(shedResult)) {
            tackleAttempt(ballHandler, getDefensePlayer(defenderSlot));
            yardsGained = currentYards;
            return playOutcome != null;
        }

        return false;
    }

    private int simulateSafetyPursuit(int yards) {
        int base = 50;

        while (yards < 99) {
            if (pursuit(getDefensePlayer(Team.LineupSlot.FS), base)) {
                tackleAttempt(ballHandler, getDefensePlayer(Team.LineupSlot.FS));
                if (playOutcome != null) {
                    return yards;
                }
            }

            if (pursuit(getDefensePlayer(Team.LineupSlot.SS), base)) {
                tackleAttempt(ballHandler, getDefensePlayer(Team.LineupSlot.SS));
                if (playOutcome != null) {
                    return yards;
                }
            }

            base += 20;
            yards += 3;
        }

        playOutcome = PlayOutcome.OUT_OF_BOUNDS;
        return yards;
    }

    /**
     * Early pass-play skeleton.
     * This method assigns routes, pass protection, rushers, and coverage.
     * Full pass-completion/interception logic can be added later.
     */
    public int passPlay() {
        resetPlayResult();

        if (offensivePlayType == null || isRunPlay(offensivePlayType)) {
            offensivePlayType = PlayType.SHORT_THROW;
        }

        if (defensivePlayType == null || isOffensivePlay(defensivePlayType)) {
            defensivePlayType = PlayType.ZONE;
        }

        HashMap<Player, Integer> routes = assignRoutes();

        ArrayList<Player> playersInCoverage = new ArrayList<>();
        ArrayList<Player> playersPassRushing = getBasePassRushers();
        ArrayList<Player> playersPassBlocking = new ArrayList<>();
        ArrayList<Player> playersInRoutes = new ArrayList<>();

        for (Map.Entry<Player, Integer> route : routes.entrySet()) {
            if (route.getValue() == 0) {
                addIfMissing(playersPassBlocking, route.getKey());
            } else {
                addIfMissing(playersInRoutes, route.getKey());
            }
        }

        addOffensiveLineToPassProtection(playersPassBlocking);

        if (isBlitzCall()) {
            addBlitzers(playersPassRushing);
        }

        playersInCoverage.addAll(getCoveragePlayers(playersPassRushing));

        if (defensivePlayType == PlayType.MAN || defensivePlayType == PlayType.MAN_BLITZ) {
            assignedManCoverages = assignManCoverage(playersInRoutes, playersInCoverage);
            assignedCoverages = createEmptyCoverageMap();
        } else {
            assignedManCoverages = new HashMap<>();
            assignedCoverages = assignZoneCoverage(playersInCoverage);
        }

        boolean pressureArrived = passRush(playersPassBlocking, playersPassRushing, 3);

        if (pressureArrived) {
            playOutcome = PlayOutcome.SACK;
            yardsGained = -random.nextInt(1, 9);
            return yardsGained;
        }

        // Placeholder until the passing-result logic is fully built.
        playOutcome = PlayOutcome.PASS_INCOMPLETE;
        yardsGained = 0;
        return yardsGained;
    }

    public boolean passRush(ArrayList<Player> passBlockers, ArrayList<Player> passRushers, int second) {
        int base = second * 5;
        HashMap<Player, Player> passBlockAssignments = assignPassBlock(passBlockers, passRushers);

        for (Map.Entry<Player, Player> assignment : passBlockAssignments.entrySet()) {
            Player blocker = assignment.getKey();
            Player rusher = assignment.getValue();

            if (rusher == null || rusher == emptyPlayer) {
                continue;
            }

            if (blocker == null || blocker == emptyPlayer) {
                return true;
            }

            if (passRushAttempt(blocker, rusher, base)) {
                return true;
            }
        }

        return false;
    }

    public boolean passRushAttempt(Player offensivePlayer, Player defensivePlayer, int base) {
        int passBlockRushDiff = defensivePlayer.getPassRushRTG() - offensivePlayer.getPassBlockRTG();
        int adjustedChance = clampChance(base + passBlockRushDiff);

        return rollPercent() <= adjustedChance;
    }

    public HashMap<Player, Player> assignPassBlock(ArrayList<Player> passBlockers, ArrayList<Player> passRushers) {
        HashMap<Player, Player> assignments = new HashMap<>();

        ArrayList<Player> unassignedRushers = new ArrayList<>(passRushers);

        for (Player blocker : passBlockers) {
            assignments.put(blocker, emptyPlayer);
        }

        // Try to set sensible base matchups first.
        if (getDefensiveFormation() == DScheme.DefensiveFormation.D3_4) {
            assignPassBlockIfAvailable(assignments, unassignedRushers, Team.LineupSlot.LT, Team.LineupSlot.EDGE);
            assignPassBlockIfAvailable(assignments, unassignedRushers, Team.LineupSlot.LG, Team.LineupSlot.LE);
            assignPassBlockIfAvailable(assignments, unassignedRushers, Team.LineupSlot.C, Team.LineupSlot.DT);
            assignPassBlockIfAvailable(assignments, unassignedRushers, Team.LineupSlot.RG, Team.LineupSlot.RE);
        } else {
            assignPassBlockIfAvailable(assignments, unassignedRushers, Team.LineupSlot.LT, Team.LineupSlot.EDGE1);
            assignPassBlockIfAvailable(assignments, unassignedRushers, Team.LineupSlot.LG, Team.LineupSlot.DT1);
            assignPassBlockIfAvailable(assignments, unassignedRushers, Team.LineupSlot.C, Team.LineupSlot.DT2);
            assignPassBlockIfAvailable(assignments, unassignedRushers, Team.LineupSlot.RT, Team.LineupSlot.EDGE2);
        }

        // Assign remaining rushers to remaining blockers.
        for (Player rusher : new ArrayList<>(unassignedRushers)) {
            Player blocker = firstUnassignedBlocker(assignments);

            if (blocker == null) {
                assignments.put(emptyPlayer, rusher);
                break;
            }

            assignments.put(blocker, rusher);
            unassignedRushers.remove(rusher);
        }

        return assignments;
    }

    private void assignPassBlockIfAvailable(HashMap<Player, Player> assignments,
                                            ArrayList<Player> unassignedRushers,
                                            Team.LineupSlot blockerSlot,
                                            Team.LineupSlot rusherSlot) {
        Player blocker = getOffensePlayer(blockerSlot);
        Player rusher = getDefensePlayer(rusherSlot);

        if (assignments.containsKey(blocker)
                && assignments.get(blocker) == emptyPlayer
                && unassignedRushers.contains(rusher)) {
            assignments.put(blocker, rusher);
            unassignedRushers.remove(rusher);
        }
    }

    private Player firstUnassignedBlocker(HashMap<Player, Player> assignments) {
        for (Map.Entry<Player, Player> assignment : assignments.entrySet()) {
            if (assignment.getValue() == emptyPlayer) {
                return assignment.getKey();
            }
        }

        return null;
    }

    public HashMap<Player, Player> assignManCoverage(ArrayList<Player> playersInRoutes,
                                                     ArrayList<Player> playersInCoverage) {
        HashMap<Player, Player> manAssignments = new HashMap<>();
        ArrayList<Player> availableDefenders = new ArrayList<>(playersInCoverage);

        for (Player routeRunner : playersInRoutes) {
            manAssignments.put(routeRunner, emptyPlayer);
        }

        assignManIfPossible(manAssignments, availableDefenders, Team.LineupSlot.WR1, Team.LineupSlot.CB1);
        assignManIfPossible(manAssignments, availableDefenders, Team.LineupSlot.WR2, Team.LineupSlot.CB2);

        // Assign remaining route runners to remaining coverage players.
        for (Player routeRunner : playersInRoutes) {
            if (manAssignments.get(routeRunner) != emptyPlayer) {
                continue;
            }

            if (!availableDefenders.isEmpty()) {
                manAssignments.put(routeRunner, availableDefenders.remove(0));
            }
        }

        return manAssignments;
    }

    private void assignManIfPossible(HashMap<Player, Player> manAssignments,
                                     ArrayList<Player> availableDefenders,
                                     Team.LineupSlot offensiveSlot,
                                     Team.LineupSlot defensiveSlot) {
        Player routeRunner = getOffensePlayer(offensiveSlot);
        Player defender = getDefensePlayer(defensiveSlot);

        if (manAssignments.containsKey(routeRunner) && availableDefenders.contains(defender)) {
            manAssignments.put(routeRunner, defender);
            availableDefenders.remove(defender);
        }
    }

    public HashMap<CoverageZone, Player> assignZoneCoverage(ArrayList<Player> playersInCoverage) {
        HashMap<CoverageZone, Player> coverageMap = createEmptyCoverageMap();

        int safetyCount = 0;
        int cornerCount = 0;
        int linebackerCount = 0;

        for (Player player : playersInCoverage) {
            switch (player.getPosition()) {
                case "S" -> {
                    if (safetyCount == 0) {
                        coverageMap.put(CoverageZone.DEEP_LEFT, player);
                    } else if (safetyCount == 1) {
                        coverageMap.put(CoverageZone.DEEP_RIGHT, player);
                    } else {
                        coverageMap.put(CoverageZone.DEEP_MIDDLE, player);
                    }
                    safetyCount++;
                }
                case "CB" -> {
                    if (cornerCount == 0) {
                        coverageMap.put(random.nextBoolean() ? CoverageZone.MED_RIGHT : CoverageZone.SHORT_RIGHT, player);
                    } else if (cornerCount == 1) {
                        coverageMap.put(random.nextBoolean() ? CoverageZone.MED_LEFT : CoverageZone.SHORT_LEFT, player);
                    } else {
                        coverageMap.put(CoverageZone.MED_MIDDLE, player);
                    }
                    cornerCount++;
                }
                case "LB", "EDGE" -> {
                    if (linebackerCount == 0) {
                        coverageMap.put(CoverageZone.SHORT_MIDDLE, player);
                    } else if (linebackerCount == 1) {
                        coverageMap.put(CoverageZone.MED_MIDDLE, player);
                    } else {
                        coverageMap.put(random.nextBoolean() ? CoverageZone.SHORT_LEFT : CoverageZone.SHORT_RIGHT, player);
                    }
                    linebackerCount++;
                }
                default -> {
                    // Defensive linemen normally are not assigned zone coverage.
                }
            }
        }

        return coverageMap;
    }

    public boolean rbVisionTest() {
        Player rb = getOffensePlayer(Team.LineupSlot.RB);

        int baseChanceOfYardJump = 10;
        int adjustedChance = clampChance(baseChanceOfYardJump + rb.getVisionRTG() / 2);

        return rollPercent() <= adjustedChance;
    }

    public HashMap<Player, Integer> assignRoutes() {
        // Route values:
        // 0 = pass block
        // 1 = short route
        // 2 = medium route
        // 3 = deep route

        if (offensivePlayType == null) {
            offensivePlayType = PlayType.SHORT_THROW;
        }

        HashMap<Player, Integer> routes = new HashMap<>();

        Player wr1 = getOffensePlayer(Team.LineupSlot.WR1);
        Player wr2 = getOffensePlayer(Team.LineupSlot.WR2);
        Player wr3 = getOffensePlayer(Team.LineupSlot.WR3);
        Player te = getOffensePlayer(Team.LineupSlot.TE);
        Player rb = getOffensePlayer(Team.LineupSlot.RB);

        switch (offensivePlayType) {
            case SHORT_THROW -> {
                routes.put(wr1, 1);
                routes.put(wr2, 1);
                routes.put(wr3, 1);
                routes.put(te, random.nextInt(0, 2));
                routes.put(rb, random.nextInt(0, 2));
            }
            case MED_THROW -> {
                routes.put(wr1, 2);
                routes.put(wr2, 2);
                routes.put(wr3, random.nextInt(1, 4));
                routes.put(te, random.nextInt(0, 3));
                routes.put(rb, random.nextInt(0, 3));
            }
            case DEEP_THROW -> {
                routes.put(wr1, 3);
                routes.put(wr2, 3);
                routes.put(wr3, random.nextInt(1, 4));
                routes.put(te, random.nextInt(0, 4));
                routes.put(rb, random.nextInt(0, 2));
            }
            default -> {
                routes.put(wr1, random.nextInt(1, 4));
                routes.put(wr2, random.nextInt(1, 4));
                routes.put(wr3, random.nextInt(1, 4));
                routes.put(te, random.nextInt(0, 4));
                routes.put(rb, random.nextInt(0, 4));
            }
        }

        return routes;
    }

    public void decidePlay() {
        decideDefensivePlay();
        decideOffensivePlay();
    }

    private void decideDefensivePlay() {
        boolean zoneCall = rollPercent() <= getDefensiveScheme().getZoneFreq();
        boolean blitzCall = rollPercent() <= getDefensiveScheme().getBlitzFreq();

        if (zoneCall && blitzCall) {
            defensivePlayType = PlayType.ZONE_BLITZ;
        } else if (zoneCall) {
            defensivePlayType = PlayType.ZONE;
        } else if (blitzCall) {
            defensivePlayType = PlayType.MAN_BLITZ;
        } else {
            defensivePlayType = PlayType.MAN;
        }
    }

    private void decideOffensivePlay() {
        boolean runCall = rollPercent() <= offense.getCoach().getOffensiveScheme().getRunFreq();

        if (runCall) {
            boolean insideRun = rollPercent() <= offense.getCoach().getOffensiveScheme().getInsideRunFreq();
            offensivePlayType = insideRun ? PlayType.INSIDE_RUN : PlayType.OUTSIDE_RUN;
            return;
        }

        int passRoll = rollPercent();
        int deepFreq = offense.getCoach().getOffensiveScheme().getDeepPassFreq();
        int mediumFreq = offense.getCoach().getOffensiveScheme().getMedPassFreq();

        if (passRoll <= deepFreq) {
            offensivePlayType = PlayType.DEEP_THROW;
        } else if (passRoll <= deepFreq + mediumFreq) {
            offensivePlayType = PlayType.MED_THROW;
        } else {
            offensivePlayType = PlayType.SHORT_THROW;
        }
    }

    public String shedBlockAttempt(Player offensivePlayer, Player defensivePlayer, int base) {
        int runBlockBlockShedDiff = offensivePlayer.getRunBlockRTG() - defensivePlayer.getBlockShedRTG();
        int adjustedChanceOfShed = clampChance(base - (int) Math.round(runBlockBlockShedDiff * 1.2));

        if (rollPercent() <= adjustedChanceOfShed) {
            return BLOCK_SHED;
        }

        return BLOCK_HELD;
    }

    public boolean pursuit(Player defensivePlayer, int base) {
        int speedDiff = defensivePlayer.getSpeed() - ballHandler.getSpeed();
        int adjustedChance = clampChance(base + (int) Math.round(speedDiff * 1.1));

        return rollPercent() <= adjustedChance;
    }

    public void tackleAttempt(Player offensivePlayer, Player defensivePlayer) {
        int baseChanceOfTackle = 60;

        int tackleAgilityDiff = defensivePlayer.getTackleRTG() - offensivePlayer.getAgility();
        int tackleStrengthDiff = defensivePlayer.getTackleRTG() - offensivePlayer.getStrength();

        int agilityAdjustedChance = clampChance((int) Math.round(tackleAgilityDiff * 1.1) + baseChanceOfTackle);
        int strengthAdjustedChance = clampChance((int) Math.round(tackleStrengthDiff * 1.1) + baseChanceOfTackle);

        if (rollPercent() <= agilityAdjustedChance || rollPercent() <= strengthAdjustedChance) {
            fumbleTest(offensivePlayer, defensivePlayer);
        }
    }

    public void fumbleTest(Player offensivePlayer, Player defensivePlayer) {
        int baseChanceOfFumble = 1;
        int strengthCarryDiff = defensivePlayer.getStrength() - offensivePlayer.getCarryRTG();

        int adjustedChanceOfFumble = clampChance(Math.round((float) strengthCarryDiff / 3) + baseChanceOfFumble);

        if (rollPercent() <= adjustedChanceOfFumble) {
            playOutcome = PlayOutcome.FUMBLE;
            turnoverOnPlay = true;
            forcedFumbler = defensivePlayer;
            fumbler = offensivePlayer;
        } else {
            playOutcome = PlayOutcome.TACKLE;
            turnoverOnPlay = false;
            tackler = defensivePlayer;
        }
    }

    public Player getOteamPlayer(String position) {
        return getOffensePlayer(slotFromString(position));
    }

    public Player getDteamPlayer(String position) {
        return getDefensePlayer(slotFromString(position));
    }

    private Player getOffensePlayer(Team.LineupSlot slot) {
        Player player = offense.getStartingO().get(slot);

        if (player == null) {
            throw new IllegalStateException("Missing offensive player for lineup slot: " + slot);
        }

        return player;
    }

    private Player getDefensePlayer(Team.LineupSlot slot) {
        Player player = getActiveDefense().get(slot);

        if (player == null) {
            throw new IllegalStateException("Missing defensive player for lineup slot: " + slot);
        }

        return player;
    }

    private Map<Team.LineupSlot, Player> getActiveDefense() {
        if (getDefensiveFormation() == DScheme.DefensiveFormation.D3_4) {
            return defense.getD3_4startingD();
        }

        return defense.getD4_3startingD();
    }

    private DScheme getDefensiveScheme() {
        return defense.getCoach().getDefensiveScheme();
    }

    private DScheme.DefensiveFormation getDefensiveFormation() {
        return getDefensiveScheme().getdForm();
    }

    private Team.LineupSlot slotFromString(String slotName) {
        try {
            return Team.LineupSlot.valueOf(slotName);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid lineup slot: " + slotName, exception);
        }
    }

    private RunDirection randomDirection() {
        return random.nextBoolean() ? RunDirection.LEFT : RunDirection.RIGHT;
    }

    private int rollPercent() {
        return random.nextInt(1, 101);
    }

    private int clampChance(int chance) {
        if (chance < 0) {
            return 0;
        }

        if (chance > 100) {
            return 100;
        }

        return chance;
    }

    private int finishPlay(int yards) {
        yardsGained = yards;
        return yardsGained;
    }

    private void resetPlayResult() {
        playOutcome = null;
        yardsGained = 0;
        timeOfPlay = 0;
        turnoverOnPlay = false;
        tackler = null;
        ballHandler = null;
        fumbler = null;
        forcedFumbler = null;
        assignedCoverages = createEmptyCoverageMap();
        assignedManCoverages = new HashMap<>();
    }

    private boolean isBlitzCall() {
        return defensivePlayType == PlayType.MAN_BLITZ || defensivePlayType == PlayType.ZONE_BLITZ;
    }

    private boolean isRunPlay(PlayType playType) {
        return playType == PlayType.INSIDE_RUN || playType == PlayType.OUTSIDE_RUN;
    }

    private boolean isOffensivePlay(PlayType playType) {
        return playType == PlayType.DEEP_THROW
                || playType == PlayType.MED_THROW
                || playType == PlayType.SHORT_THROW
                || playType == PlayType.QB_SCRAMBLE
                || playType == PlayType.INSIDE_RUN
                || playType == PlayType.OUTSIDE_RUN;
    }

    private ArrayList<Player> getBasePassRushers() {
        ArrayList<Player> rushers = new ArrayList<>();

        switch (getDefensiveFormation()) {
            case D3_4 -> {
                rushers.add(getDefensePlayer(Team.LineupSlot.EDGE));
                rushers.add(getDefensePlayer(Team.LineupSlot.DT));
                rushers.add(getDefensePlayer(Team.LineupSlot.LE));
                rushers.add(getDefensePlayer(Team.LineupSlot.RE));
            }
            case D4_3 -> {
                rushers.add(getDefensePlayer(Team.LineupSlot.DT1));
                rushers.add(getDefensePlayer(Team.LineupSlot.DT2));
                rushers.add(getDefensePlayer(Team.LineupSlot.EDGE1));
                rushers.add(getDefensePlayer(Team.LineupSlot.EDGE2));
            }
        }

        return rushers;
    }

    private void addOffensiveLineToPassProtection(ArrayList<Player> passBlockers) {
        addIfMissing(passBlockers, getOffensePlayer(Team.LineupSlot.LT));
        addIfMissing(passBlockers, getOffensePlayer(Team.LineupSlot.LG));
        addIfMissing(passBlockers, getOffensePlayer(Team.LineupSlot.C));
        addIfMissing(passBlockers, getOffensePlayer(Team.LineupSlot.RG));
        addIfMissing(passBlockers, getOffensePlayer(Team.LineupSlot.RT));
    }

    private void addBlitzers(ArrayList<Player> passRushers) {
        ArrayList<Player> availableBlitzers = new ArrayList<>();

        switch (getDefensiveFormation()) {
            case D3_4 -> {
                availableBlitzers.add(getDefensePlayer(Team.LineupSlot.MLB1));
                availableBlitzers.add(getDefensePlayer(Team.LineupSlot.MLB2));
                availableBlitzers.add(getDefensePlayer(Team.LineupSlot.OLB));
            }
            case D4_3 -> {
                availableBlitzers.add(getDefensePlayer(Team.LineupSlot.MLB));
                availableBlitzers.add(getDefensePlayer(Team.LineupSlot.ROLB));
                availableBlitzers.add(getDefensePlayer(Team.LineupSlot.LOLB));
            }
        }

        int numberOfBlitzers = Math.min(Player.getBellcurve(1, 4), availableBlitzers.size());

        for (int i = 0; i < numberOfBlitzers; i++) {
            Player blitzer = availableBlitzers.remove(random.nextInt(availableBlitzers.size()));
            addIfMissing(passRushers, blitzer);
        }
    }

    private ArrayList<Player> getCoveragePlayers(ArrayList<Player> passRushers) {
        ArrayList<Player> coveragePlayers = new ArrayList<>();

        for (Player defender : getActiveDefense().values()) {
            if (defender != null && !passRushers.contains(defender)) {
                coveragePlayers.add(defender);
            }
        }

        return coveragePlayers;
    }

    private void addIfMissing(ArrayList<Player> players, Player player) {
        if (player != null && !players.contains(player)) {
            players.add(player);
        }
    }

    private HashMap<CoverageZone, Player> createEmptyCoverageMap() {
        HashMap<CoverageZone, Player> coverageMap = new HashMap<>();

        coverageMap.put(CoverageZone.DEEP_RIGHT, emptyPlayer);
        coverageMap.put(CoverageZone.DEEP_MIDDLE, emptyPlayer);
        coverageMap.put(CoverageZone.DEEP_LEFT, emptyPlayer);
        coverageMap.put(CoverageZone.MED_RIGHT, emptyPlayer);
        coverageMap.put(CoverageZone.MED_MIDDLE, emptyPlayer);
        coverageMap.put(CoverageZone.MED_LEFT, emptyPlayer);
        coverageMap.put(CoverageZone.SHORT_RIGHT, emptyPlayer);
        coverageMap.put(CoverageZone.SHORT_MIDDLE, emptyPlayer);
        coverageMap.put(CoverageZone.SHORT_LEFT, emptyPlayer);

        return coverageMap;
    }

    // Getters and setters

    public Team getOffenseTeam() {
        return offense;
    }

    public void setOffenseTeam(Team offense) {
        this.offense = offense;
    }

    public Team getDefenseTeam() {
        return defense;
    }

    public void setDefenseTeam(Team defense) {
        this.defense = defense;
    }

    // Legacy-style getters kept so older code does not immediately break.
    public Team getoTeam() {
        return offense;
    }

    public void setoTeam(Team offense) {
        this.offense = offense;
    }

    public Team getdTeam() {
        return defense;
    }

    public void setdTeam(Team defense) {
        this.defense = defense;
    }

    public PlayType getOPlayType() {
        return offensivePlayType;
    }

    public void setOPlayType(PlayType offensivePlayType) {
        this.offensivePlayType = offensivePlayType;
    }

    public PlayType getDPlayType() {
        return defensivePlayType;
    }

    public void setDPlayType(PlayType defensivePlayType) {
        this.defensivePlayType = defensivePlayType;
    }

    public PlayOutcome getPlayOutcome() {
        return playOutcome;
    }

    public void setPlayOutcome(PlayOutcome playOutcome) {
        this.playOutcome = playOutcome;
    }

    public int getTimeOfPlay() {
        return timeOfPlay;
    }

    public void setTimeOfPlay(int timeOfPlay) {
        this.timeOfPlay = timeOfPlay;
    }

    public int getTimeOfplay() {
        return timeOfPlay;
    }

    public void setTimeOfplay(int timeOfPlay) {
        this.timeOfPlay = timeOfPlay;
    }

    public int getYardsGained() {
        return yardsGained;
    }

    public void setYardsGained(int yardsGained) {
        this.yardsGained = yardsGained;
    }

    public boolean isTurnoverOnPlay() {
        return turnoverOnPlay;
    }

    public void setTurnoverOnPlay(boolean turnoverOnPlay) {
        this.turnoverOnPlay = turnoverOnPlay;
    }

    public boolean isTurnOverOnPlay() {
        return turnoverOnPlay;
    }

    public void setTurnOverOnPlay(boolean turnoverOnPlay) {
        this.turnoverOnPlay = turnoverOnPlay;
    }

    public Player getTackler() {
        return tackler;
    }

    public void setTackler(Player tackler) {
        this.tackler = tackler;
    }

    public Player getBallHandler() {
        return ballHandler;
    }

    public void setBallHandler(Player ballHandler) {
        this.ballHandler = ballHandler;
    }

    public Player getFumbler() {
        return fumbler;
    }

    public void setFumbler(Player fumbler) {
        this.fumbler = fumbler;
    }

    public Player getForcedFumbler() {
        return forcedFumbler;
    }

    public void setForcedFumbler(Player forcedFumbler) {
        this.forcedFumbler = forcedFumbler;
    }

    public HashMap<CoverageZone, Player> getAssignedCoverages() {
        return assignedCoverages;
    }

    public HashMap<Player, Player> getAssignedManCoverages() {
        return assignedManCoverages;
    }


    @Override
public String toString() {
    StringBuilder description = new StringBuilder();

    description.append("Play: ");

    if (offensivePlayType != null) {
        description.append(formatEnum(offensivePlayType));
    } else {
        description.append("Unknown offensive play");
    }

    description.append(" vs. ");

    if (defensivePlayType != null) {
        description.append(formatEnum(defensivePlayType));
    } else {
        description.append("Unknown defensive play");
    }

    description.append(". ");

    if (ballHandler != null) {
        description.append(ballHandler.getFirstName())
                .append(" ")
                .append(ballHandler.getLastName())
                .append(" handled the ball. ");
    }

    if (playOutcome == null) {
        description.append("The play has not been resolved yet.");
        return description.toString();
    }

    switch (playOutcome) {
        case TACKLE -> {
            description.append("He gained ")
                    .append(yardsGained)
                    .append(yardsGained == 1 ? " yard" : " yards");

            if (tackler != null) {
                description.append(" before being tackled by ")
                        .append(tackler.getFirstName())
                        .append(" ")
                        .append(tackler.getLastName());
            }

            description.append(".");
        }

        case FUMBLE -> {
            if (fumbler != null) {
                description.append(fumbler.getFirstName())
                        .append(" ")
                        .append(fumbler.getLastName())
                        .append(" fumbled");
            } else {
                description.append("The offense fumbled");
            }

            if (forcedFumbler != null) {
                description.append(" after a hit by ")
                        .append(forcedFumbler.getFirstName())
                        .append(" ")
                        .append(forcedFumbler.getLastName());
            }

            description.append(". ");

            if (turnoverOnPlay) {
                description.append("Turnover by the offense.");
            } else {
                description.append("The offense recovered the ball.");
            }
        }

        case SACK -> {
            description.append("The quarterback was sacked for a loss of ")
                    .append(Math.abs(yardsGained))
                    .append(Math.abs(yardsGained) == 1 ? " yard" : " yards")
                    .append(".");
        }

        case PASS_INCOMPLETE -> {
            description.append("The pass was incomplete. No gain.");
        }

        case INTERCEPTION -> {
            description.append("The pass was intercepted. Turnover by the offense.");
        }

        case OUT_OF_BOUNDS -> {
            description.append("The ball carrier got to the outside and went out of bounds after gaining ")
                    .append(yardsGained)
                    .append(yardsGained == 1 ? " yard" : " yards")
                    .append(".");
        }

        case FUMBLE_RECOVERY -> {
            description.append("There was a fumble recovery on the play.");
        }
    }

    return description.toString();
}

private String formatEnum(Enum<?> value) {
    String text = value.name().toLowerCase().replace("_", " ");
    return text.substring(0, 1).toUpperCase() + text.substring(1);
}



}
