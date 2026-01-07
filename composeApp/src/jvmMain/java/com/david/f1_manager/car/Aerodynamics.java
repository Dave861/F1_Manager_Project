package com.david.f1_manager.car;

/**
 * Aerodynamics component
 */
public class Aerodynamics extends CarPart {

    public Aerodynamics(String id, String name, int performance) {
        super(id, name, performance);
    }

    @Override
    public double getPartTypeMultiplier() {
        return 0.25; // Aero is crucial for cornering
    }
}
