package com.david.f1_manager.ai;

import com.david.f1_manager.Team;

/**
 * Represents an AI-controlled F1 team with predefined strategy
 */
public class AITeam extends Team {
    private AIStrategy strategy;

    public enum AIStrategy {
        AGGRESSIVE,
        CONSERVATIVE,   
        ADAPTIVE
    }

    public AITeam(String id, String name, AIStrategy strategy) {
        super(id, name);
        this.strategy = strategy;
    }

    // Getters and Setters
    public AIStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(AIStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String toString() {
        return "AITeam {name='" + getName() + "', strategy=" + strategy + ", drivers=" + getDrivers().size() + '}';
    }
}
