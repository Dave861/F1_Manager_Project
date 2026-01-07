package com.david.f1_manager;

/**
 * Represents an F1 circuit/track
 */
public class Track {
    private String id;
    private String name;
    private int laps;
    private TrackCharacteristics characteristics;

    public enum TrackCharacteristics {
        SPEED,      // Favors engine power (e.g., Monza)
        TECHNICAL,  // Favors aero + driver skill (e.g., Monaco)
        BALANCED    // Overall performance (e.g., Silverstone)
    }

    public Track(String id, String name, int laps) {
        this.id = id;
        this.name = name;
        this.laps = laps;
        this.characteristics = TrackCharacteristics.BALANCED;
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

    public int getLaps() {
        return laps;
    }

    public void setLaps(int laps) {
        this.laps = laps;
    }

    public TrackCharacteristics getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(TrackCharacteristics characteristics) {
        this.characteristics = characteristics;
    }

    @Override
    public String toString() {
        return "Track {name='" + name + "', laps=" + laps + '}';
    }
}
