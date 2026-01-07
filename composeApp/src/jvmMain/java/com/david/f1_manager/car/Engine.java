package com.david.f1_manager.car;

/**
 * Engine component
 */
public class Engine extends CarPart {

    public Engine(String id, String name, int performance) {
        super(id, name, performance);
    }

    @Override
    public double getPartTypeMultiplier() {
        return 0.35; // Engine has highest impact on performance
    }
}
