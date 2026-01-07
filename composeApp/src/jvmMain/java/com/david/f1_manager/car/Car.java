package com.david.f1_manager.car;

import com.david.f1_manager.Performable;

/**
 * Represents a complete F1 car with all components
 * Demonstrates interface implementation
 */
public class Car implements Performable {
    private String id;
    private String name;
    private Engine engine;
    private Aerodynamics aerodynamics;
    private Tires tires;
    private Suspension suspension;
    private Gearbox gearbox;

    public Car(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Calculate overall car performance based on all components
     */
    public double calculateOverallPerformance() {
        double totalPerformance = 0.0;

        if (engine != null) {
            totalPerformance += engine.getPerformance() * engine.getPartTypeMultiplier();
        }
        if (aerodynamics != null) {
            totalPerformance += aerodynamics.getPerformance() * aerodynamics.getPartTypeMultiplier();
        }
        if (tires != null) {
            totalPerformance += tires.getPerformance() * tires.getPartTypeMultiplier();
        }
        if (suspension != null) {
            totalPerformance += suspension.getPerformance() * suspension.getPartTypeMultiplier();
        }
        if (gearbox != null) {
            totalPerformance += gearbox.getPerformance() * gearbox.getPartTypeMultiplier();
        }

        return totalPerformance;
    }

    /**
     * Implementation of Performable interface
     * @return the overall performance rating of this car
     */
    @Override
    public double getPerformanceRating() {
        return calculateOverallPerformance();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Aerodynamics getAerodynamics() {
        return aerodynamics;
    }

    public void setAerodynamics(Aerodynamics aerodynamics) {
        this.aerodynamics = aerodynamics;
    }

    public Tires getTires() {
        return tires;
    }

    public void setTires(Tires tires) {
        this.tires = tires;
    }

    public Suspension getSuspension() {
        return suspension;
    }

    public void setSuspension(Suspension suspension) {
        this.suspension = suspension;
    }

    public Gearbox getGearbox() {
        return gearbox;
    }

    public void setGearbox(Gearbox gearbox) {
        this.gearbox = gearbox;
    }

    @Override
    public String toString() {
        return "Car {performance=" + String.format("%.1f", calculateOverallPerformance()) + '}';
    }
}