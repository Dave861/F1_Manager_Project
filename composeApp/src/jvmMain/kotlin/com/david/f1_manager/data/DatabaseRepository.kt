package com.david.f1_manager.data

import com.david.f1_manager.Driver
import com.david.f1_manager.Team
import com.david.f1_manager.Track
import com.david.f1_manager.User
import com.david.f1_manager.UserRole
import com.david.f1_manager.ai.AITeam
import com.david.f1_manager.car.Aerodynamics
import com.david.f1_manager.car.Car
import com.david.f1_manager.car.Engine
import com.david.f1_manager.car.Gearbox
import com.david.f1_manager.car.Suspension
import com.david.f1_manager.car.Tires

class DatabaseRepository {

    fun loadUsers(): Map<String, User> {
        val users = mutableMapOf<String, User>()
        val conn = DatabaseConfig.getConnection()

        val sql = "SELECT id, username, password, role, managed_team_id FROM users"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)

        while (rs.next()) {
            val id = rs.getString("id")
            val username = rs.getString("username")
            val password = rs.getString("password")
            val roleStr = rs.getString("role")
            val managedTeamId = rs.getString("managed_team_id")
            val role = if (roleStr == "ADMIN") UserRole.ADMIN else UserRole.VIEWER

            users[id] = User(id, username, password, role, managedTeamId)
        }

        rs.close()
        stmt.close()
        conn.close()

        return users
    }

    fun loadDrivers(): Map<String, Driver> {
        val drivers = mutableMapOf<String, Driver>()
        val conn = DatabaseConfig.getConnection()

        val sql = "SELECT id, name, skill FROM drivers"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)

        while (rs.next()) {
            val id = rs.getString("id")
            val name = rs.getString("name")
            val skill = rs.getInt("skill")

            drivers[id] = Driver(id, name, skill)
        }

        rs.close()
        stmt.close()
        conn.close()

        return drivers
    }

    /**
     * Create a new driver in the database
     * @return the created driver, or null if creation failed
     */
    fun createDriver(name: String, skill: Int): Driver? {
        val conn = DatabaseConfig.getConnection()
        val id = "driver_${System.currentTimeMillis()}"

        val sql = "INSERT INTO drivers (id, name, skill) VALUES (?, ?, ?)"
        val stmt = conn.prepareStatement(sql)
        stmt.setString(1, id)
        stmt.setString(2, name)
        stmt.setInt(3, skill.coerceIn(1, 100))

        val rowsAffected = stmt.executeUpdate()

        stmt.close()
        conn.close()

        return if (rowsAffected > 0) Driver(id, name, skill) else null
    }

    /**
     * Update an existing driver's properties
     * @return true if update succeeded
     */
    fun updateDriver(driverId: String, name: String, skill: Int): Boolean {
        val conn = DatabaseConfig.getConnection()

        val sql = "UPDATE drivers SET name = ?, skill = ? WHERE id = ?"
        val stmt = conn.prepareStatement(sql)
        stmt.setString(1, name)
        stmt.setInt(2, skill.coerceIn(1, 100))
        stmt.setString(3, driverId)

        val rowsAffected = stmt.executeUpdate()

        stmt.close()
        conn.close()

        return rowsAffected > 0
    }

    /**
     * Delete a driver from the database
     * Only succeeds if driver is not assigned to any team
     * @return true if deletion succeeded, false if driver is on a team or not found
     */
    fun deleteDriver(driverId: String): Boolean {
        val conn = DatabaseConfig.getConnection()

        // First check if driver is assigned to a team
        val checkSql = "SELECT team_id FROM drivers WHERE id = ?"
        val checkStmt = conn.prepareStatement(checkSql)
        checkStmt.setString(1, driverId)
        val rs = checkStmt.executeQuery()

        if (rs.next()) {
            val teamId = rs.getString("team_id")
            if (teamId != null) {
                // Driver is on a team - cannot delete
                rs.close()
                checkStmt.close()
                conn.close()
                return false
            }
        }
        rs.close()
        checkStmt.close()

        // Safe to delete
        val sql = "DELETE FROM drivers WHERE id = ?"
        val stmt = conn.prepareStatement(sql)
        stmt.setString(1, driverId)

        val rowsAffected = stmt.executeUpdate()

        stmt.close()
        conn.close()

        return rowsAffected > 0
    }

    /**
     * Add a driver to a team in the database
     * Updates the team_id column in the drivers table
     * @return true if assignment succeeded
     */
    fun addDriverToTeamInDB(driverId: String, teamId: String): Boolean {
        val conn = DatabaseConfig.getConnection()

        val sql = "UPDATE drivers SET team_id = ? WHERE id = ?"
        val stmt = conn.prepareStatement(sql)
        stmt.setString(1, teamId)
        stmt.setString(2, driverId)

        val rowsAffected = stmt.executeUpdate()

        stmt.close()
        conn.close()

        return rowsAffected > 0
    }

    /**
     * Remove a driver from their team in the database
     * Sets team_id to NULL
     * @return true if removal succeeded
     */
    fun removeDriverFromTeamInDB(driverId: String): Boolean {
        val conn = DatabaseConfig.getConnection()

        val sql = "UPDATE drivers SET team_id = NULL WHERE id = ?"
        val stmt = conn.prepareStatement(sql)
        stmt.setString(1, driverId)

        val rowsAffected = stmt.executeUpdate()

        stmt.close()
        conn.close()

        return rowsAffected > 0
    }

    fun loadEngines(): Map<String, Engine> {
        val engines = mutableMapOf<String, Engine>()
        val conn = DatabaseConfig.getConnection()

        val sql = "SELECT id, name, performance FROM engines"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)

        while (rs.next()) {
            val id = rs.getString("id")
            val name = rs.getString("name")
            val performance = rs.getInt("performance")

            engines[id] = Engine(id, name, performance)
        }

        rs.close()
        stmt.close()
        conn.close()

        return engines
    }

    fun loadAerodynamics(): Map<String, Aerodynamics> {
        val aeros = mutableMapOf<String, Aerodynamics>()
        val conn = DatabaseConfig.getConnection()

        val sql = "SELECT id, name, performance FROM aerodynamics"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)

        while (rs.next()) {
            val id = rs.getString("id")
            val name = rs.getString("name")
            val performance = rs.getInt("performance")

            aeros[id] = Aerodynamics(id, name, performance)
        }

        rs.close()
        stmt.close()
        conn.close()

        return aeros
    }

    fun loadTires(): Map<String, Tires> {
        val tires = mutableMapOf<String, Tires>()
        val conn = DatabaseConfig.getConnection()

        val sql = "SELECT id, name, performance, compound FROM tires"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)

        while (rs.next()) {
            val id = rs.getString("id")
            val name = rs.getString("name")
            val performance = rs.getInt("performance")
            val compoundStr = rs.getString("compound")

            val compound = when (compoundStr) {
                "SOFT" -> Tires.TireCompound.SOFT
                "MEDIUM" -> Tires.TireCompound.MEDIUM
                "HARD" -> Tires.TireCompound.HARD
                else -> Tires.TireCompound.MEDIUM
            }

            tires[id] = Tires(id, name, performance, compound)
        }

        rs.close()
        stmt.close()
        conn.close()

        return tires
    }

    fun loadGearboxes(): Map<String, Gearbox> {
        val gearboxes = mutableMapOf<String, Gearbox>()
        val conn = DatabaseConfig.getConnection()

        val sql = "SELECT id, name, performance FROM gearboxes"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)

        while (rs.next()) {
            val id = rs.getString("id")
            val name = rs.getString("name")
            val performance = rs.getInt("performance")

            gearboxes[id] = Gearbox(id, name, performance)
        }

        rs.close()
        stmt.close()
        conn.close()

        return gearboxes
    }

    fun loadSuspensions(): Map<String, Suspension> {
        val suspensions = mutableMapOf<String, Suspension>()
        val conn = DatabaseConfig.getConnection()

        val sql = "SELECT id, name, performance FROM suspensions"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)

        while (rs.next()) {
            val id = rs.getString("id")
            val name = rs.getString("name")
            val performance = rs.getInt("performance")

            suspensions[id] = Suspension(id, name, performance)
        }

        rs.close()
        stmt.close()
        conn.close()

        return suspensions
    }

    fun loadCars(
        engines: Map<String, Engine>,
        aeros: Map<String, Aerodynamics>,
        tires: Map<String, Tires>,
        suspensions: Map<String, Suspension>,
        gearboxes: Map<String, Gearbox>
    ): Map<String, Car> {
        val cars = mutableMapOf<String, Car>()
        val conn = DatabaseConfig.getConnection()

        val sql = """
            SELECT id, name, engine_id, aerodynamics_id, tires_id, 
                   suspension_id, gearbox_id 
            FROM cars
        """
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)

        while (rs.next()) {
            val id = rs.getString("id")
            val name = rs.getString("name")
            val engineId = rs.getString("engine_id")
            val aeroId = rs.getString("aerodynamics_id")
            val tiresId = rs.getString("tires_id")
            val suspensionId = rs.getString("suspension_id")
            val gearboxId = rs.getString("gearbox_id")

            val car = Car(id, name)

            // Assign parts if they exist
            if (engineId != null) car.engine = engines[engineId]
            if (aeroId != null) car.aerodynamics = aeros[aeroId]
            if (tiresId != null) car.tires = tires[tiresId]
            if (suspensionId != null) car.suspension = suspensions[suspensionId]
            if (gearboxId != null) car.gearbox = gearboxes[gearboxId]

            cars[id] = car
        }

        rs.close()
        stmt.close()
        conn.close()

        return cars
    }

    fun loadTeams(
        cars: Map<String, Car>,
        drivers: Map<String, Driver>
    ): Pair<Map<String, Team>, List<AITeam>> {
        val teams = mutableMapOf<String, Team>()
        val aiTeams = mutableListOf<AITeam>()
        val conn = DatabaseConfig.getConnection()

        // Load teams
        val sql = "SELECT id, name, car_id FROM teams"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)

        while (rs.next()) {
            val id = rs.getString("id")
            val name = rs.getString("name")
            val carId = rs.getString("car_id")

            // Determine if it's an AI team (simple check: starts with "ai")
            val team = if (id.startsWith("ai")) {
                // For AI teams we pick a random strategy at load time
                AITeam(id, name, AITeam.AIStrategy.entries.random())
            } else {
                Team(id, name)
            }

            // Assign car if exists
            if (carId != null) {
                team.car = cars[carId]
            }

            teams[id] = team

            if (team is AITeam) {
                aiTeams.add(team)
            }
        }

        rs.close()
        stmt.close()

        // Load drivers and assign to teams
        val driverSql = "SELECT id, team_id FROM drivers WHERE team_id IS NOT NULL"
        val driverStmt = conn.createStatement()
        val driverRs = driverStmt.executeQuery(driverSql)

        while (driverRs.next()) {
            val driverId = driverRs.getString("id")
            val teamId = driverRs.getString("team_id")

            val driver = drivers[driverId]
            val team = teams[teamId]

            if (driver != null && team != null) {
                team.addDriver(driver)
            }
        }

        driverRs.close()
        driverStmt.close()
        conn.close()

        return Pair(teams, aiTeams)
    }

    fun loadTracks(): Map<String, Track> {
        val tracks = mutableMapOf<String, Track>()
        val conn = DatabaseConfig.getConnection()

        val sql = "SELECT id, name, laps, characteristics FROM tracks"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)

        while (rs.next()) {
            val id = rs.getString("id")
            val name = rs.getString("name")
            val laps = rs.getInt("laps")
            val charStr = rs.getString("characteristics")

            val track = Track(id, name, laps)

            track.characteristics = when (charStr) {
                "SPEED" -> Track.TrackCharacteristics.SPEED
                "TECHNICAL" -> Track.TrackCharacteristics.TECHNICAL
                "BALANCED" -> Track.TrackCharacteristics.BALANCED
                else -> Track.TrackCharacteristics.BALANCED
            }

            tracks[id] = track
        }

        rs.close()
        stmt.close()
        conn.close()

        return tracks
    }

    /**
     * Update a car's engine assignment
     */
    fun updateCarEngine(carId: String, engineId: String?): Boolean {
        return updateCarPart(carId, "engine_id", engineId)
    }

    /**
     * Update a car's aerodynamics assignment
     */
    fun updateCarAerodynamics(carId: String, aerodynamicsId: String?): Boolean {
        return updateCarPart(carId, "aerodynamics_id", aerodynamicsId)
    }

    /**
     * Update a car's tires assignment
     */
    fun updateCarTires(carId: String, tiresId: String?): Boolean {
        return updateCarPart(carId, "tires_id", tiresId)
    }

    /**
     * Update a car's gearbox assignment
     */
    fun updateCarGearbox(carId: String, gearboxId: String?): Boolean {
        return updateCarPart(carId, "gearbox_id", gearboxId)
    }

    /**
     * Update a car's suspension assignment
     */
    fun updateCarSuspension(carId: String, suspensionId: String?): Boolean {
        return updateCarPart(carId, "suspension_id", suspensionId)
    }

    /**
     * Generic helper to update a car part column
     */
    private fun updateCarPart(carId: String, columnName: String, partId: String?): Boolean {
        val conn = DatabaseConfig.getConnection()

        val sql = "UPDATE cars SET $columnName = ? WHERE id = ?"
        val stmt = conn.prepareStatement(sql)
        if (partId != null) {
            stmt.setString(1, partId)
        } else {
            stmt.setNull(1, java.sql.Types.VARCHAR)
        }
        stmt.setString(2, carId)

        val rowsAffected = stmt.executeUpdate()

        stmt.close()
        conn.close()

        return rowsAffected > 0
    }
}