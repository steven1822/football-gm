package com.footballgm.model;


import java.util.ArrayList;
import java.util.Random;

//Class that is modeling a Football Player
public class Player {
    //All player rating will be 1-99


    //Players first name
    protected String firstName;
    //Players last name
    protected String lastName;
    //Players overall rating
    protected int overall;
    //Players jersey number
    protected int jerseyNumber;

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    //players college
    private String college;

    //Players physical ratings
    //Speed rating of player
    protected int speed;
    //Strength rating of player
    protected int strength;
    //Agility rating of player
    protected int agility;


    //Players body
    //Height of player in inches
    protected int height;
    //Weight of player in pounds
    protected int weight;


    //Position of Player
    protected String position;
    //List that holds all of the possible player positions
    private static final ArrayList<String> listOfPositions = new ArrayList<String>(){{
        //Adds all the positions
        add("QB");
        add("WR");
        add("RB");
        add("TE");
        add("OT");
        add("OG");
        add("C");
        add("DT");
        add("DE");
        add("EDGE");
        add("LB");
        add("CB");
        add("S");
    }};





    //Position Specific Ratings

    //Offensive
    //Players run block rating
    protected int runBlockRTG;
    //Players pass block rating
    protected int passBlockRTG;
    //Players Catch Rating
    private int catchRTG;
    //Players Short Throw Accuracy Rating
    private int shortAcRTG;
    //Players Medium Throw Accuracy Rating
    private int mediumAcRTG;
    //Players Deep Throw Accuracy Rating
    private int deepAcRTG;
    //Players Throw Power Rating
    private int throwPowerRTG;
    //Players pass recognition rating;
    private int passRecognitionRTG;
    //Players short Route Rating
    protected int shortRtRTG;
    //Players Medium Route Rating
    protected int medRtRTG;
    //Players Deep Route Rating
    protected int deepRtRTG;





    //Defensive
    //Players zone coverage Rating
    private int zoneRTG;
    //Players man coverage Rating
    private int manRTG;
    //Players Tackle Rating
    private int tackleRTG;
    //Players Block Shed Rating
    private int blockShedRTG;
    //Players pass rush Rating
    private int passRushRTG;
    //players carry Rating
    protected int carryRTG;
    //Players vision RTG;
    protected int visionRTG;



    public Player(String firstName, String lastName,String position){
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;








    }


    public void setOverall(){
        //Method that will calculate the overall of the player based on the position of the player

        //Place holder
        double spacer;
        //Separates the ratings into 3 categories
        int physicalRating = 0;
        int skillRating = 0;


        //Sets physical Rating

        //If this player plays on the line
        switch (this.position) {
            case "OT", "OG", "C", "DE", "DT" -> {
                spacer = (this.speed * 1.2 + this.agility * 1.33 + this.strength * 0.95) / 3;
                //gives DE a little boost to speed
                if (this.position.equals("DE")) {
                    spacer = (this.speed * 1.13 + this.agility * 1.33 + this.strength) / 3;
                }
                physicalRating = (int) Math.round(spacer);
            }
            case "TE" -> {spacer = (this.speed*1.08 + this.agility * 1.08 + this.strength)/3;
                physicalRating = (int) Math.round(spacer);
            }

            //If player is a skill position
            case "WR", "RB", "CB" -> {
                spacer = (this.speed * 0.95 + this.agility + this.strength * 1.25) / 3;
                if (this.position.equals("WR")){
                    spacer = (this.speed * 0.87 + this.agility + this.strength * 1.1) / 3;
                }
                physicalRating = (int) Math.round(spacer);
            }
            //If player is a QB
            case "QB" -> {

                spacer = (this.speed * 1.09 + this.agility * 1.13 + this.strength) / 3;
                physicalRating = (int) Math.round(spacer);
            }
            //If player is a linebacker or edge rusher
            case "LB", "EDGE" -> {
                double aSpeed = this.speed * 1.1;
                double aAgility = this.agility * 1.01;
                double aStrength = this.strength;
                spacer = (aStrength + aSpeed + aAgility) / 3;
                physicalRating = (int) Math.round(spacer);
            }
            //if player is a saftey;
            case "S" -> {
                spacer = (this.speed * 1.05 + this.agility * 1.05 + this.strength * 1.1) / 3;
                physicalRating = (int) Math.round(spacer);
            }
        }

        //Sets Skill Rating

        //number of ratings that is significant to the position
        int significantRatings = 0;
        spacer = 0;

        if (isDefensive()){
            double aTackle = this.tackleRTG;
            double aZoneRTG = this.zoneRTG;
            double aManRTG = this.manRTG;
            double aPassRushRTG = this.passRushRTG;
            double aBlockShedRTG = this.blockShedRTG;
            double aCatch = this.catchRTG;



            if (this.position.equals("DT")|| this.position.equals("DE")) {
                aPassRushRTG *= 1.1;
                if (this.position.equals("DE")) {
                    aPassRushRTG = this.passRushRTG * 1.09;
                }

                spacer = aTackle + aBlockShedRTG + aPassRushRTG;
                significantRatings = 3;
            } else if (this.position.equals("LB")) {
                aZoneRTG *=1.1;
                aManRTG *=1.1;
                aPassRushRTG *=1.1;
                aCatch *= 1.33;
                spacer = aCatch + aZoneRTG + aManRTG + aPassRushRTG + aTackle + aBlockShedRTG;
                significantRatings = 6;
            } else if (this.position.equals("EDGE")) {

                spacer = aPassRushRTG + aBlockShedRTG + aTackle;
                significantRatings = 3;

            } else if (this.position.equals("CB")||this.position.equals("S")) {
                aCatch *= 1.1;
                if (this.position.equals("S")){
                    aZoneRTG *= 0.95;
                    aManRTG *= 1.05;

                }
                else {
                    aTackle *= 1.1;

                }
                spacer = aCatch + aZoneRTG + aManRTG + aTackle;
                significantRatings = 4;
            }




        }
        //Offensive positions skill calculation
        if(this.position.equals("QB")){
            spacer = shortAcRTG + mediumAcRTG + deepAcRTG + throwPowerRTG + passRecognitionRTG;
            significantRatings = 5;
        }
        else if (this.position.equals("WR")){
            spacer = shortRtRTG + medRtRTG + deepRtRTG + catchRTG;
            significantRatings = 4;
        }
        else if(this.position.equals("RB")){
            spacer = visionRTG + carryRTG + catchRTG * 1.15;
            significantRatings = 3;
        } else if (this.position.equals("TE")) {
            spacer = shortRtRTG + medRtRTG + deepRtRTG + runBlockRTG*1.05 + catchRTG*1.05;
            significantRatings = 5;
        } else if(this.position.equals("OG")|| this.position.equals("C")){
            spacer = runBlockRTG*0.97 + passBlockRTG * 1.03;
            significantRatings = 2;
        } else if (this.position.equals("OT")) {
            spacer = runBlockRTG*1.03 + passBlockRTG * 0.97;
            significantRatings = 2;
        }

        //calculates skill rating
        skillRating = (int) Math.round(spacer/significantRatings);
        //System.out.println("Skill Rating: " + String.valueOf(skillRating) +" Physical Rating: "  + String.valueOf(physicalRating));

        //calculates overall based on skill rating and physical rating
        this.overall = (int) Math.round((float) (skillRating + physicalRating*0.9) /2);

        //if ovr is over 99 it sets it back to 99
        if (this.overall> 99){this.overall = 99;}



    }

    public static Player createRandomPlayer(String position,double modifier){
        //Method that accepts a position and a stats modifier and returns a random player at that position
        //Can put "random" for random position

        //random class
        Random rand = new Random();

        if (position.equals("random")){
            position = listOfPositions.get(rand.nextInt(listOfPositions.size()));
        }

        //new random player
        Player randomPlayer = new Player("Random","Player",position);

        //sets random ints 35-45 for all ratings
        randomPlayer.setPassRecognitionRTG(rand.nextInt(35,46));
        randomPlayer.setCatchRTG(rand.nextInt(35,45));
        randomPlayer.setShortRtRTG(rand.nextInt(35,45));
        randomPlayer.setMedRtRTG(rand.nextInt(35,45));
        randomPlayer.setDeepRtRTG(rand.nextInt(35,45));
        randomPlayer.setShortAcRTG(rand.nextInt(35,45));
        randomPlayer.setMediumAcRTG(rand.nextInt(35,45));
        randomPlayer.setDeepAcRTG(rand.nextInt(35,45));
        randomPlayer.setThrowPowerRTG(rand.nextInt(35,45));
        randomPlayer.setVisionRTG(rand.nextInt(35,45));
        randomPlayer.setCarryRTG(rand.nextInt(35,45));
        randomPlayer.setPassBlockRTG(rand.nextInt(35,45));
        randomPlayer.setRunBlockRTG(rand.nextInt(35,45));
        randomPlayer.setTackleRTG(rand.nextInt(35,45));
        randomPlayer.setManRTG(rand.nextInt(35,45));
        randomPlayer.setZoneRTG(rand.nextInt(35,45));
        randomPlayer.setPassRushRTG(rand.nextInt(35,45));
        randomPlayer.setBlockShedRTG(rand.nextInt(35,45));

        //sets random ints for significant ratings for each position
        switch (position){
            case "QB":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(75,93)*modifier*1.05));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setShortAcRTG((int) Math.round(rand.nextInt(75,80)*modifier));
                randomPlayer.setMediumAcRTG((int) Math.round(rand.nextInt(75,80)*modifier));
                randomPlayer.setDeepAcRTG((int) Math.round(rand.nextInt(75,80)*modifier));
                randomPlayer.setThrowPowerRTG((int) Math.round(rand.nextInt(80,85)*modifier));
                randomPlayer.setPassRecognitionRTG((int) Math.round(rand.nextInt(75,85)*modifier));
                break;
            case "WR":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(87,97)*modifier*1.05));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(87,97)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(70,80)*modifier));
                randomPlayer.setDeepRtRTG((int) Math.round(Player.getBellcurve(75,87)*modifier));
                randomPlayer.setShortRtRTG((int) Math.round(Player.getBellcurve(75,87)*modifier));
                randomPlayer.setMedRtRTG((int) Math.round(Player.getBellcurve(75,87)*modifier));
                randomPlayer.setCatchRTG((int) Math.round(Player.getBellcurve(85,95)*modifier));
                randomPlayer.setCarryRTG((int) Math.round(Player.getBellcurve(85,95)*modifier));
                break;
            case "TE":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(75,90)*modifier));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(87,97)*modifier*1.05));
                randomPlayer.setDeepRtRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setShortRtRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setMedRtRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setCatchRTG((int) Math.round(Player.getBellcurve(85,95)*modifier));
                randomPlayer.setRunBlockRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setCarryRTG((int) Math.round(Player.getBellcurve(75,80)*modifier));
                break;
            case "RB":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(85,95)*modifier*1.02));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(85,95)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(80,90)*modifier));
                randomPlayer.setVisionRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setCarryRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setCatchRTG((int) Math.round(Player.getBellcurve(65,75)*modifier));
                break;
            case "C":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(65,75)*modifier));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(65,75)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(80,90)*modifier));
                randomPlayer.setRunBlockRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setPassBlockRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                break;
            case "OG":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(65,75)*modifier));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(65,75)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(80,90)*modifier));
                randomPlayer.setRunBlockRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setPassBlockRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                break;
            case "OT":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(65,77)*modifier));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(65,77)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(80,90)*modifier));
                randomPlayer.setRunBlockRTG((int) Math.round(Player.getBellcurve(73,83)*modifier));
                randomPlayer.setPassBlockRTG((int) Math.round(Player.getBellcurve(77,87)*modifier));
                break;
            case "DT":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(65,75)*modifier));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(65,75)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(80,90)*modifier));
                randomPlayer.setTackleRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setBlockShedRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setPassRushRTG((int) Math.round(Player.getBellcurve(65,75)*modifier));
                randomPlayer.setZoneRTG((int) Math.round(Player.getBellcurve(45,55)*modifier));
                randomPlayer.setManRTG((int) Math.round(Player.getBellcurve(45,55)*modifier));
                randomPlayer.setCatchRTG((int) Math.round(Player.getBellcurve(20,60)*modifier));
                break;
            case "DE":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(70,80)*modifier));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(70,75)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(80,90)*modifier));
                randomPlayer.setTackleRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setBlockShedRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setPassRushRTG((int) Math.round(Player.getBellcurve(70,80)*modifier));
                randomPlayer.setZoneRTG((int) Math.round(Player.getBellcurve(45,55)*modifier));
                randomPlayer.setManRTG((int) Math.round(Player.getBellcurve(45,55)*modifier));
                randomPlayer.setCatchRTG((int) Math.round(Player.getBellcurve(20,60)*modifier));
                break;
            case "EDGE":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(83,93)*modifier));
                randomPlayer.setTackleRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setBlockShedRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setPassRushRTG((int) Math.round(Player.getBellcurve(78,88)*modifier));
                randomPlayer.setZoneRTG((int) Math.round(Player.getBellcurve(60,70)*modifier));
                randomPlayer.setManRTG((int) Math.round(Player.getBellcurve(55,65)*modifier));
                randomPlayer.setCatchRTG((int) Math.round(Player.getBellcurve(30,65)*modifier));
                break;
            case "LB":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(78,88)*modifier));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(83,93)*modifier));
                randomPlayer.setTackleRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setBlockShedRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setPassRushRTG((int) Math.round(Player.getBellcurve(70,77)*modifier));
                randomPlayer.setZoneRTG((int) Math.round(Player.getBellcurve(70,80)*modifier));
                randomPlayer.setManRTG((int) Math.round(Player.getBellcurve(70,80)*modifier));
                randomPlayer.setCatchRTG((int) Math.round(Player.getBellcurve(40,70)*modifier));
                break;
            case "CB":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(87,96)*modifier*1.05));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(87,97)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(70,80)*modifier));
                randomPlayer.setTackleRTG((int) Math.round(Player.getBellcurve(70,80)*modifier));
                randomPlayer.setBlockShedRTG((int) Math.round(Player.getBellcurve(60,70)*modifier));
                randomPlayer.setPassRushRTG((int) Math.round(Player.getBellcurve(55,65)*modifier));
                randomPlayer.setZoneRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setManRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setCatchRTG((int) Math.round(Player.getBellcurve(60,85)*modifier));
                break;
            case "S":
                randomPlayer.setSpeed((int) Math.round(Player.getBellcurve(83,93)*modifier*1.05));
                randomPlayer.setAgility((int) Math.round(Player.getBellcurve(85,95)*modifier));
                randomPlayer.setStrength((int) Math.round(Player.getBellcurve(77,87)*modifier));
                randomPlayer.setTackleRTG((int) Math.round(Player.getBellcurve(70,80)*modifier));
                randomPlayer.setBlockShedRTG((int) Math.round(Player.getBellcurve(60,80)*modifier));
                randomPlayer.setPassRushRTG((int) Math.round(Player.getBellcurve(50,70)*modifier));
                randomPlayer.setZoneRTG((int) Math.round(Player.getBellcurve(75,85)*modifier));
                randomPlayer.setManRTG((int) Math.round(Player.getBellcurve(70,75)*modifier));
                randomPlayer.setCatchRTG((int) Math.round(Player.getBellcurve(65,85)*modifier));
                break;

        }
        randomPlayer.setOverall();
        return randomPlayer;

    }

    public static int getBellcurve(int orgin,int bound){
        //returns a random int on a bell curve from orgin to bound
        Random rand  = new Random();
        return (int) Math.round((rand.nextInt(orgin,bound)+rand.nextInt(orgin,bound)+ rand.nextInt(orgin, bound)) /3);
    }



    public Boolean isDefensive(){
        //method that will check if the player is a defensive player and return a boolean
        ArrayList<String> defensivePositions = new ArrayList<String>();
        defensivePositions.add("DT");
        defensivePositions.add("DE");
        defensivePositions.add("EDGE");
        defensivePositions.add("LB");
        defensivePositions.add("CB");
        defensivePositions.add("S");

        for (String position: defensivePositions){
            if (this.position.equals(position)){
                return true;
            }
        }
        return false;
    }
    public Boolean isOffensive(){
        //method that will check if the player is a offensive player and return a boolean
        ArrayList<String> offensivePositions = new ArrayList<String>();
        offensivePositions.add("QB");
        offensivePositions.add("WR");
        offensivePositions.add("HB");
        offensivePositions.add("TE");
        offensivePositions.add("OT");
        offensivePositions.add("OG");
        offensivePositions.add("C");


        for (String position: offensivePositions){
            if (this.position.equals(position)){
                return true;
            }
        }
        return false;
    }



    //Setters and Getters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getOverall() {
        return overall;
    }



    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setOverall(int overall) {
        this.overall = overall;
    }

    public int getRunBlockRTG() {
        return runBlockRTG;
    }

    public void setRunBlockRTG(int runBlockRTG) {
        this.runBlockRTG = runBlockRTG;
    }

    public int getPassBlockRTG() {
        return passBlockRTG;
    }

    public void setPassBlockRTG(int passBlockRTG) {
        this.passBlockRTG = passBlockRTG;
    }

    public int getCatchRTG() {
        return catchRTG;
    }

    public void setCatchRTG(int catchRTG) {
        this.catchRTG = catchRTG;
    }

    public int getShortAcRTG() {
        return shortAcRTG;
    }

    public void setShortAcRTG(int shortAcRTG) {
        this.shortAcRTG = shortAcRTG;
    }

    public int getMediumAcRTG() {
        return mediumAcRTG;
    }

    public void setMediumAcRTG(int mediumAcRTG) {
        this.mediumAcRTG = mediumAcRTG;
    }

    public int getDeepAcRTG() {
        return deepAcRTG;
    }

    public void setDeepAcRTG(int deepAcRTG) {
        this.deepAcRTG = deepAcRTG;
    }

    public int getThrowPowerRTG() {
        return throwPowerRTG;
    }

    public void setThrowPowerRTG(int throwPowerRTG) {
        this.throwPowerRTG = throwPowerRTG;
    }

    public int getShortRtRTG() {
        return shortRtRTG;
    }

    public void setShortRtRTG(int shortRtRTG) {
        this.shortRtRTG = shortRtRTG;
    }

    public int getMedRtRTG() {
        return medRtRTG;
    }

    public void setMedRtRTG(int medRtRTG) {
        this.medRtRTG = medRtRTG;
    }

    public int getDeepRtRTG() {
        return deepRtRTG;
    }

    public void setDeepRtRTG(int deepRtRTG) {
        this.deepRtRTG = deepRtRTG;
    }

    public int getZoneRTG() {
        return zoneRTG;
    }

    public void setZoneRTG(int zoneRTG) {
        this.zoneRTG = zoneRTG;
    }

    public int getManRTG() {
        return manRTG;
    }

    public void setManRTG(int manRTG) {
        this.manRTG = manRTG;
    }

    public int getTackleRTG() {
        return tackleRTG;
    }

    public void setTackleRTG(int tackleRTG) {
        this.tackleRTG = tackleRTG;
    }

    public int getBlockShedRTG() {
        return blockShedRTG;
    }

    public void setBlockShedRTG(int blockShedRTG) {
        this.blockShedRTG = blockShedRTG;
    }

    public int getPassRushRTG() {
        return passRushRTG;
    }

    public void setPassRushRTG(int passRushRTG) {
        this.passRushRTG = passRushRTG;
    }

    public int getCarryRTG() {
        return carryRTG;
    }

    public void setCarryRTG(int carryRTG) {
        this.carryRTG = carryRTG;
    }

    public int getVisionRTG() {
        return visionRTG;
    }

    public void setVisionRTG(int visionRTG) {
        this.visionRTG = visionRTG;
    }

    public int getPassRecognitionRTG() {
        return passRecognitionRTG;
    }

    public void setPassRecognitionRTG(int passRecognitionRTG) {
        this.passRecognitionRTG = passRecognitionRTG;
    }

    //To string
    @Override
    public String toString(){
        return this.firstName + " " + this.lastName + ": " + this.position + " - " + this.overall + " ovr";
    }
}
