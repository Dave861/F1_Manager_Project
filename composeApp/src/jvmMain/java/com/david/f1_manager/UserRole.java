package com.david.f1_manager;

/**
 * User roles with simplified permissions
 * VIEWER: Can only view home screen and race history
 * ADMIN: Full access to all features (manage team, run races, trigger events)
 */
public enum UserRole {
    VIEWER,  // Read-only access to home screen
    ADMIN;   // Full access to everything

    /**
     * Check if this role is ADMIN (has full access)
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if this role can access team management
     */
    public boolean canManageTeam() {
        return this == ADMIN;
    }

    /**
     * Check if this role can run races
     */
    public boolean canRunRaces() {
        return this == ADMIN;
    }

}
