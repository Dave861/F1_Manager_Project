package com.david.f1_manager;

import com.david.f1_manager.car.Car;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an F1 team with drivers and car
 */
public class Team {
    private String id;
    private String name;
    private List<Driver> drivers;
    private Car car;

    public Team() {
        this.drivers = new ArrayList<>();
    }

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
        this.drivers = new ArrayList<>();
    }

    /**
     * Add a driver to the team (max 2 drivers)
     */
    public boolean addDriver(Driver driver) {
        if (drivers.size() >= 2) {
            return false;
        }
        drivers.add(driver);
        return true;
    }

    /**
     * Remove a driver from the team
     */
    public boolean removeDriver(Driver driver) {
        return drivers.remove(driver);
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

    public List<Driver> getDrivers() {
        return new ArrayList<>(drivers);
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = new ArrayList<>(drivers);
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return "Team {name='" + name + "', drivers=" + drivers.size() + '}';
    }
}
