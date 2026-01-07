package com.david.f1_manager.car;

/**
 * Gearbox component
 */
public class Gearbox extends CarPart {

    public Gearbox(String id, String name, int performance) {
        super(id, name, performance);
    }

    @Override
    public double getPartTypeMultiplier() {
        return 0.10; // Gearbox affects acceleration
    }
}