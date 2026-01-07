package com.david.f1_manager.car;

import com.david.f1_manager.Performable;

/**
 * Base class for car components
 * Demonstrates abstract class and interface implementation
 */
public abstract class CarPart implements Performable {
    private String id;
    private String name;
    private int performance; // 1-100

    public CarPart(String id, String name, int performance) {
        this.id = id;
        this.name = name;
        this.performance = Math.max(1, Math.min(100, performance));
    }

    // Abstract method for specific part type performance contribution
    public abstract double getPartTypeMultiplier();

    /**
     * Implementation of Performable interface
     * @return the performance rating of this part
     */
    @Override
    public double getPerformanceRating() {
        return performance;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPerformance() {
        return performance;
    }

    public void setPerformance(int performance) {
        this.performance = Math.max(1, Math.min(100, performance));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {name='" + name + "', performance=" + performance + '}';
    }
}