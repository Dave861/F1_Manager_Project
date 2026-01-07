package com.david.f1_manager.car;

/**
 * Tires component
 */
public class Tires extends CarPart {
    private TireCompound compound;

    public enum TireCompound {
        SOFT,    // Best grip, fastest wear
        MEDIUM,  // Balanced performance
        HARD     // Longest lasting, slowest wear
    }

    public Tires(String id, String name, int performance, TireCompound compound) {
        super(id, name, performance);
        this.compound = compound;
    }

    @Override
    public double getPartTypeMultiplier() {
        return 0.20; // Tires affect grip and speed
    }

    public TireCompound getCompound() {
        return compound;
    }

    public void setCompound(TireCompound compound) {
        this.compound = compound;
    }
}
