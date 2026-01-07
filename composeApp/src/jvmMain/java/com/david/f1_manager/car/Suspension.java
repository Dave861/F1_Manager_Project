package com.david.f1_manager.car;

/**
 * Suspension component
 */
public class Suspension extends CarPart {

    public Suspension(String id, String name, int performance) {
        super(id, name, performance);
    }

    @Override
    public double getPartTypeMultiplier() {
        return 0.10; // Suspension affects handling
    }
}
