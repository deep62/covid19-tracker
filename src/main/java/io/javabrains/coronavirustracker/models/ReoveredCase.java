package io.javabrains.coronavirustracker.models;

public class ReoveredCase {
    private int totalRecoveredCase;
    private int diffOfRecoveredFromThePreviousDay;

    public int getTotalRecoveredCase() {
        return totalRecoveredCase;
    }

    public void setTotalRecoveredCase(int totalRecoveredCase) {
        this.totalRecoveredCase = totalRecoveredCase;
    }

    public int getDiffOfRecoveredFromThePreviousDay() {
        return diffOfRecoveredFromThePreviousDay;
    }

    public void setDiffOfRecoveredFromThePreviousDay(int diffOfRecoveredFromThePreviousDay) {
        this.diffOfRecoveredFromThePreviousDay = diffOfRecoveredFromThePreviousDay;
    }
}
