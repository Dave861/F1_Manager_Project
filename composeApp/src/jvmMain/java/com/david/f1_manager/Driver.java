package com.david.f1_manager;

/**
 * Represents an F1 Driver with their skills and attributes
 */
public class Driver {
    private String id;
    private String name;
    private int skill; // 1-100

    public Driver(String id, String name, int skill) {
        this.id = id;
        this.name = name;
        this.skill = Math.max(1, Math.min(100, skill)); // Ensure skill is within valid range
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

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        int validatedSkill = this.skill; // Keep current value as fallback
        try {
            // Validate the skill value
            if (skill < 1 || skill > 100) {
                throw new InvalidRatingException("Skill must be between 1 and 100, got: " + skill);
            }
            validatedSkill = skill;
        } catch (InvalidRatingException e) {
            // Log the error and use clamped value as fallback
            System.err.println("Warning: " + e.getMessage() + ". Using clamped value.");
            validatedSkill = Math.max(1, Math.min(100, skill));
        } finally {
            // Ensure skill is always set to a valid value
            this.skill = validatedSkill;
        }
    }

    @Override
    public String toString() {
        return "Driver {name='" + name + "', skill=" + skill + '}';
    }
}