package com.david.f1_manager.race;

import com.david.f1_manager.car.*;

/**
 * Represents the race strategy for a team.
 * NOT currently implemented in the LiveRaceSimulator.
 */
public class RaceStrategy {
    private String id;
    private int pitStopCount;
    private Tires.TireCompound startingTireCompound;
    private double initialFuelLoad; // 0-100 kg

    public RaceStrategy(String id, int pitStopCount, Tires.TireCompound startingTireCompound, double initialFuelLoad) {
        this.id = id;
        this.pitStopCount = pitStopCount;
        this.startingTireCompound = startingTireCompound;
        this.initialFuelLoad = Math.max(0, Math.min(100, initialFuelLoad)); // Clamp to 0-100
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPitStopCount() {
        return pitStopCount;
    }

    public void setPitStopCount(int pitStopCount) {
        this.pitStopCount = pitStopCount;
    }

    public Tires.TireCompound getStartingTireCompound() {
        return startingTireCompound;
    }

    public void setStartingTireCompound(Tires.TireCompound startingTireCompound) {
        this.startingTireCompound = startingTireCompound;
    }

    public double getInitialFuelLoad() {
        return initialFuelLoad;
    }

    public void setInitialFuelLoad(double initialFuelLoad) {
        this.initialFuelLoad = Math.max(0, Math.min(100, initialFuelLoad));
    }

    @Override
    public String toString() {
        return "RaceStrategy {pitStops=" + pitStopCount + ", startingTires=" + startingTireCompound + ", fuelLoad=" + initialFuelLoad + "kg}";
    }
}