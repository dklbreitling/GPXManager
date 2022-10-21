package softwaredesign.activities;

public abstract class Activity {
    private String name;
    private double met;

    public void setName(String name) {
        this.name = name;
    }

    public void setMet(double met) {
        this.met = met;
    }

    public String toString() {
        return name;
    }

    public double getMet() {
        return met;
    }
}
