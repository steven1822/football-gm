package com.footballgm.model;




import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;



//Class modeling a football team
public class Team {
//Teams name
    private String teamName;
    //City that team is located in
    private String cityName;
    //League.Team color 1
    private Color color1;
    //League.Team color 2
    private Color color2;
    //League.Team color 3
    private Color color3;

    //ArrayList that holds all the players on a team
    private ArrayList<Player> roster;

    //The coach of the team
    private Coach coach;

    //Starting Offense for the team
    private HashMap<LineupSlot, Player> startingO;
    //Starting Defense for a 3-4 defense
    private HashMap<LineupSlot, Player> D3_4startingD;
    //Starting Defense for a 4-3 defense
    private HashMap<LineupSlot,Player> D4_3startingD;
    //Filler player to set keys for starting lineups
    private Player blankPlayer = null;

    //lineup slots enum
    public enum LineupSlot {
    QB, RB, WR1, WR2, WR3, TE, LT, LG, C, RG, RT, DT, LE, RE,
    DT1, DT2, EDGE, EDGE1, EDGE2, MLB, MLB1, MLB2, OLB, ROLB, LOLB, CB1, CB2, FS, SS
    }

    public Team(){
        roster = new ArrayList<>();
        coach = new Coach();
        startingO = new HashMap<LineupSlot, Player>();
        D3_4startingD = new HashMap<LineupSlot,Player>();
        D4_3startingD = new HashMap<LineupSlot,Player>();
        //Creates the Keys for the starting Offense Hash Map
        this.startingO.put(LineupSlot.QB,blankPlayer);
        this.startingO.put(LineupSlot.WR1,blankPlayer);
        this.startingO.put(LineupSlot.WR2,blankPlayer);
        this.startingO.put(LineupSlot.WR3,blankPlayer);
        this.startingO.put(LineupSlot.TE,blankPlayer);
        this.startingO.put(LineupSlot.LT,blankPlayer);
        this.startingO.put(LineupSlot.RT,blankPlayer);
        this.startingO.put(LineupSlot.LG,blankPlayer);
        this.startingO.put(LineupSlot.RG,blankPlayer);
        this.startingO.put(LineupSlot.C,blankPlayer);
        this.startingO.put(LineupSlot.RB,blankPlayer);
        //Creates the keys for the Starting 3-4 defense hash map
        this.D3_4startingD.put(LineupSlot.MLB1,blankPlayer);
        this.D3_4startingD.put(LineupSlot.MLB2,blankPlayer);
        this.D3_4startingD.put(LineupSlot.OLB,blankPlayer);
        this.D3_4startingD.put(LineupSlot.EDGE,blankPlayer);
        this.D3_4startingD.put(LineupSlot.DT,blankPlayer);
        this.D3_4startingD.put(LineupSlot.LE,blankPlayer);
        this.D3_4startingD.put(LineupSlot.RE,blankPlayer);
        this.D3_4startingD.put(LineupSlot.CB1,blankPlayer);
        this.D3_4startingD.put(LineupSlot.CB2,blankPlayer);
        this.D3_4startingD.put(LineupSlot.FS,blankPlayer);
        this.D3_4startingD.put(LineupSlot.SS,blankPlayer);
        //Creates the keys for the Starting 4-3 defense hash map
        this.D4_3startingD.put(LineupSlot.MLB,blankPlayer);
        this.D4_3startingD.put(LineupSlot.ROLB,blankPlayer);
        this.D4_3startingD.put(LineupSlot.LOLB,blankPlayer);
        this.D4_3startingD.put(LineupSlot.EDGE1,blankPlayer);
        this.D4_3startingD.put(LineupSlot.EDGE2,blankPlayer);
        this.D4_3startingD.put(LineupSlot.DT1,blankPlayer);
        this.D4_3startingD.put(LineupSlot.DT2,blankPlayer);
        this.D4_3startingD.put(LineupSlot.CB1,blankPlayer);
        this.D4_3startingD.put(LineupSlot.CB2,blankPlayer);
        this.D4_3startingD.put(LineupSlot.FS,blankPlayer);
        this.D4_3startingD.put(LineupSlot.SS,blankPlayer);






    }
    public void addPlayer(Player player){
        roster.add(player);
    }

    public void removePlayer(Player player){
        roster.remove(player);
    }


    //Getters and Setters
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Color getColor1() {
        return color1;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    public Color getColor2() {
        return color2;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }

    public Color getColor3() {
        return color3;
    }

    public void setColor3(Color color3) {
        this.color3 = color3;
    }

    public ArrayList<Player> getRoster() {
        return roster;
    }

    public void setRoster(ArrayList<Player> roster) {
        this.roster = roster;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public HashMap<LineupSlot, Player> getStartingO() {
        return startingO;
    }

    public void setStartingO(HashMap<LineupSlot, Player> startingO) {
        this.startingO = startingO;
    }

    public HashMap<LineupSlot, Player> getD3_4startingD() {
        return D3_4startingD;
    }

    public void setD3_4startingD(HashMap<LineupSlot, Player> d3_4startingD) {
        D3_4startingD = d3_4startingD;
    }

    public HashMap<LineupSlot, Player> getD4_3startingD() {
        return D4_3startingD;
    }

    public void setD4_3startingD(HashMap<LineupSlot, Player> d4_3startingD) {
        D4_3startingD = d4_3startingD;
    }
}
