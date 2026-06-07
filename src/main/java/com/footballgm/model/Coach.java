package com.footballgm.model;


//Class that models a Coach
public class Coach {
    //Filler Schemes
    
    //Field Variables
    private int offensiveRTG;
    private int defensiveRTG;
    private String firstName;
    private String lastName;
    private OScheme offensiveScheme;
    private DScheme defensiveScheme;

    public Coach() {
        this.firstName = "Default";
        this.lastName = "Coach";
        this.offensiveRTG = 50;
        this.defensiveRTG = 50;

        this.offensiveScheme = new OScheme(
                "Vertical",
                30, 70,
                45, 55,
                40, 30, 30
        );

        this.defensiveScheme = new DScheme(
                "3-4 Zone",
                DScheme.DefensiveFormation.D3_4,
                60, 40, 30
        );
    }

    //Getters and Setters

    public int getOffensiveRTG() {
        return offensiveRTG;
    }

    public void setOffensiveRTG(int offensiveRTG) {
        this.offensiveRTG = offensiveRTG;
    }

    public int getDefensiveRTG() {
        return defensiveRTG;
    }

    public void setDefensiveRTG(int defensiveRTG) {
        this.defensiveRTG = defensiveRTG;
    }

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

    public OScheme getOffensiveScheme() {
        return offensiveScheme;
    }

    public void setOffensiveScheme(OScheme offensiveScheme) {
        this.offensiveScheme = offensiveScheme;
    }

    public DScheme getDefensiveScheme() {
        return defensiveScheme;
    }

    public void setDefensiveScheme(DScheme defensiveScheme) {
        this.defensiveScheme = defensiveScheme;
    }
}
