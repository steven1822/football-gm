package com.footballgm.model;

//Class that modeles a coaches defensive scheme
public class DScheme {

    public enum DefensiveFormation {D3_4,D4_3}
    private String schemeName;
    private DefensiveFormation dForm;
    private int zoneFreq;
    private int manFreq;
    private int blitzFreq;

    public DScheme(String schemeName, DefensiveFormation dForm, int zoneFreq, int manFreq, int blitzFreq) {
        if (zoneFreq + manFreq != 100){
            throw new IllegalArgumentException("Zone Frequency + Man Frequency must equal 100");
        }
        if (blitzFreq > 100 || blitzFreq < 0){
            throw new IllegalArgumentException("Blitz Frequency must be less than or equal to 100 and greater than or equal to 0");
        }
        this.schemeName = schemeName;
        this.dForm = dForm;
        this.zoneFreq = zoneFreq;
        this.manFreq = manFreq;
        this.blitzFreq = blitzFreq;
    }

    //getters and setters

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public DefensiveFormation getdForm() {
        return dForm;
    }

    public void setdForm(DefensiveFormation dForm) {
        this.dForm = dForm;
    }

    public int getZoneFreq() {
        return zoneFreq;
    }

    public void setZoneFreq(int zoneFreq) {
        this.zoneFreq = zoneFreq;
        if (zoneFreq + manFreq != 100){
            throw new IllegalArgumentException("Zone Frequency + Man Frequency must equal 100");
        }
    }

    public int getManFreq() {
        return manFreq;
    }

    public void setManFreq(int manFreq) {
        this.manFreq = manFreq;
        if (zoneFreq + manFreq != 100){
            throw new IllegalArgumentException("Zone Frequency + Man Frequency must equal 100");
        }
    }

    public int getBlitzFreq() {
        return blitzFreq;
    }

    public void setBlitzFreq(int blitzFreq) {
        this.blitzFreq = blitzFreq;
        if (blitzFreq > 100 || blitzFreq < 0){
            throw new IllegalArgumentException("Blitz Frequency must be less than or equal to 100 and greater than or equal to 0");
        }
    }
}
