package io.javabrains.coronavirustracker.models;

public class DeathCases {

    private int latestTotalDeath;
    private int diffOfDeathFromThePreviousDay;

    public int getLatestTotalDeath() {
        return latestTotalDeath;
    }

    public void setLatestTotalDeath(int latestTotalDeath) {
        this.latestTotalDeath = latestTotalDeath;
    }

    public int getDiffOfDeathFromThePreviousDay() {
        return diffOfDeathFromThePreviousDay;
    }

    public void setDiffOfDeathFromThePreviousDay(int diffOfDeathFromThePreviousDay) {
        this.diffOfDeathFromThePreviousDay = diffOfDeathFromThePreviousDay;
    }
}
