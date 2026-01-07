package com.david.f1_manager;

/**
 * Represents a user in the F1 Manager system
 */
public class User {
    private String id;
    private String username;
    private String password;
    private UserRole role;
    private String managedTeamId;

    public User(String id, String username, String password, UserRole role) {
        this(id, username, password, role, null);
    }

    public User(String id, String username, String password, UserRole role, String managedTeamId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.managedTeamId = managedTeamId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getManagedTeamId() {
        return managedTeamId;
    }

    public void setManagedTeamId(String managedTeamId) {
        this.managedTeamId = managedTeamId;
    }
}
