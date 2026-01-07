# F1 Manager - Documentation

A Formula 1 team management simulation built with **Java** for domain logic and **Kotlin, Jetpack Compose** for UI. 
The purpose of this project was to showcase OOP principles in action and learn some stuff while at it.

---

## Use Cases

- Simulate races on different tracks, weather conditions, unexpected events
- Experiment with different drivers, car parts and opponents

---

## 1. Architecture Overview

![Overview](/docs_images/architecture_overview_diagram.png)

1. **Java for Domain Models**: All business entities (Driver, Team, Car, Track) are Java classes to demonstrate OOP concepts
2. **Kotlin for UI**: Compose Desktop provides modern, declarative UI
3. **PostgreSQL Database**: Persistent storage for teams, drivers, and car configurations
4. **DataStore Pattern**: Single source of truth for application state with reactive updates
5. **Repository Pattern**: DatabaseRepository handles all database operations

---

## 2. Java Domain Model (UML)

### Complete Class Diagram

![UML](/docs_images/model_uml_diagram.png)

### OOP Concepts Highlighted

| Relationship Type | Example | Meaning |
|-------------------|---------|---------|
| **Inheritance** | `AITeam extends Team` | AITeam is a specialized type of Team |
| **Interface Implementation** | `CarPart implements Performable` | All car parts must provide performance ratings |
| **Composition** | `Team owns Car` | If Team is deleted, Car is also deleted |
| **Aggregation** | `Team has Drivers` | Drivers can exist independently of Team |
| **Association** | `User manages Team` | User can be linked to a Team via managedTeamId |

---

## 3. Database Connection

### Entity-Relationship Diagram

![ERD](/docs_images/er_diagram.png)

### Database Operations

The `DatabaseRepository.kt` class handles all CRUD operations:

#### Read Operations
- `loadUsers()` - SELECT users with managed_team_id
- `loadDrivers()` - SELECT drivers
- `loadEngines()` - SELECT engines
- `loadAerodynamics()` - SELECT aerodynamics
- `loadTires()` - SELECT tires with compound
- `loadGearboxes()` - SELECT gearboxes
- `loadSuspensions()` - SELECT suspensions
- `loadCars()` - SELECT cars with JOIN to parts
- `loadTeams()` - SELECT teams with JOIN to drivers
- `loadTracks()` - SELECT tracks

#### Create Operations
- `createDriver(name, skill)` - INSERT INTO drivers

#### Update Operations
- `updateDriver(id, name, skill)` - UPDATE drivers SET name, skill
- `addDriverToTeamInDB(driverId, teamId)` - UPDATE drivers SET team_id
- `removeDriverFromTeamInDB(driverId)` - UPDATE drivers SET team_id = NULL
- `updateCarEngine(carId, engineId)` - UPDATE cars SET engine_id
- `updateCarAerodynamics(carId, aeroId)` - UPDATE cars SET aerodynamics_id
- `updateCarTires(carId, tiresId)` - UPDATE cars SET tires_id
- `updateCarGearbox(carId, gearboxId)` - UPDATE cars SET gearbox_id
- `updateCarSuspension(carId, suspensionId)` - UPDATE cars SET suspension_id

#### Delete Operations
- `deleteDriver(driverId)` - DELETE FROM drivers WHERE team_id IS NULL


---

## 4. DataStore Pattern

### Architecture

The `DataStore` is a **singleton object** that acts as the single source of truth for all application data.

### How DataStore Works

![DataStore Pattern](/docs_images/datastore_pattern.png)

### Reactive UI Updates

The `refreshTrigger` pattern enables automatic UI updates:

```kotlin
@Composable
fun TeamManagementScreen() {
    // Observe the refresh trigger
    val refreshTrigger = DataStore.refreshTrigger.value

    // Get team from DataStore
    val team = DataStore.currentManagedTeam

    // When DataStore.notifyChange() is called,
    // refreshTrigger changes, causing recomposition
}
```

---

## 5. Race Simulation State Machine

![Race Simulator Part 1](/docs_images/rs_part1.png)
![Race Simulator Part 2](/docs_images/rs_part2.png)

### Lap Time Calculation

![Lap Time Diagram](/docs_images/laptime_calc.png)

---

## 6. UI Components

### Screen Responsibilities

| Screen | Purpose | Data Sources | Actions |
|--------|---------|--------------|---------|
| **HomeScreen** | Dashboard | `DataStore.teams`, `DataStore.raceResults`, `DataStore.currentUser` | View team info, view race history |
| **TeamManagementScreen** | Team config | `DataStore.currentManagedTeam`, `DataStore.drivers`, `DataStore.carParts` | Add/remove drivers, assign car parts, create drivers, delete drivers |
| **RaceSetupScreen** | Pre-race config | `DataStore.tracks`, `DataStore.aiTeams`, `DataStore.currentManagedTeam` | Select track, set weather, configure strategy, select AI opponents |
| **RaceViewScreen** | Live race | `LiveRaceSimulator` state | Trigger crashes, pit stops, safety car, weather changes |

---

## 7. Screenshots

![Guest Screen](/screenshots/guest_screen.png)
![Admin Home](/screenshots/admin_dash.png)
![Team Screen Car](/screenshots/team_screen_car.png)
![Team Screen Drivers](/screenshots/team_screen_drivers.png)
![Race Setup](/screenshots/race_setup.png)
![Live Race](/screenshots/live_race.png)