# WorldHelper

Convenient utilities for world operations and thread-safe execution.

## Overview

**What it does:**
- Executes tasks on the world's main thread
- Provides player management (get players, count)
- Broadcasts messages to all players
- Queries world state (paused, tick count)
- Simplifies logging with proper HytaleLogger API usage
- Tick tracking and delayed task execution
- Time and day system management

**When to use:**
- Need thread-safe world operations
- Broadcasting server messages
- Logging debug/warning/error messages
- Checking world state or player counts
- Scheduling delayed or periodic tasks
- Working with game time and day/night cycles

## Available Methods

### executeOnWorldThread(world, task)

Executes a task on the world's main thread (thread-safe).

```java
WorldHelper.executeOnWorldThread(world, () -> {
 // Any world operations here are thread-safe
 // Example: Spawn entities, modify blocks, etc.
});
```

### broadcastMessage(world, message)

Sends a message to all players in the world.

```java
WorldHelper.broadcastMessage(world, Message.raw("Server restart in 5 minutes!"));
```

### getPlayers(world) / getPlayerCount(world)

Get all players or just the count.

```java
List<Entity> players = WorldHelper.getPlayers(world);
int count = WorldHelper.getPlayerCount(world);
```

### isPaused(world) / getCurrentTick(world)

Query world state.

```java
if (!WorldHelper.isPaused(world)) {
 long tick = WorldHelper.getCurrentTick(world);
 // Do something based on tick count
}
```

### log(world, message) / logWarning(world, message) / logError(world, message)

Simplified logging using HytaleLogger.

```java
WorldHelper.log(world, "Debug: Processing items");
WorldHelper.logWarning(world, "Warning: High entity count");
WorldHelper.logError(world, "Error: Failed to load data");
```

### onTick(world, callback)

Register a callback that fires every single tick.

```java
WorldHelper.onTick(world, currentTick -> {
 // Runs every tick (20 times per second at 20 TPS)
 // Example: Update HUD, check conditions, etc.
});
```

### onTickInterval(world, interval, callback)

Register a callback that fires every N ticks. More efficient than `onTick` for periodic tasks.

```java
// Run every second (20 ticks at 20 TPS)
WorldHelper.onTickInterval(world, 20, currentTick -> {
 WorldHelper.log(world, "One second passed, tick: " + currentTick);
});

// Run every 5 seconds (100 ticks)
WorldHelper.onTickInterval(world, 100, currentTick -> {
 // Example: Auto-save, cleanup, periodic checks
 int itemCount = 0; // Count items, etc.
});
```

### stopTickTracking(world)

Stop all tick tracking for a world. Call this in your plugin's shutdown method.

```java
@Override
public void onDisable() {
 WorldHelper.stopTickTracking(world);
}
```

### waitTicks(world, ticks, callback)

Wait for a specified number of ticks before executing a callback. Perfect for delayed actions.

```java
// Player joins -> wait 3 seconds (60 ticks) -> send welcome message
EventHelper.onPlayerJoinWorld(plugin, world -> {
 WorldHelper.waitTicks(world, 60, () -> {
 WorldHelper.broadcastMessage(world, Message.raw("Welcome to the server!"));
 });
});

// Wait 5 seconds (100 ticks) before spawning an entity
WorldHelper.waitTicks(world, 100, () -> {
 // Spawn entity, trigger event, etc.
 WorldHelper.log(world, "Delayed action executed!");
});

// Chain multiple delays
WorldHelper.waitTicks(world, 20, () -> {
 WorldHelper.log(world, "After 1 second");
 
 WorldHelper.waitTicks(world, 20, () -> {
 WorldHelper.log(world, "After 2 seconds total");
 });
});
```

**Note:** The tick tracker uses a background timer that polls `world.getTick()` every 50ms and executes callbacks on the world's main thread for thread safety. The `waitTicks` method creates a self-canceling timer for one-time execution.

## Time and Day System

Get and manipulate the in-game time and day cycle.

### Getting Time Information

```java
// Get current game date/time
LocalDateTime gameDateTime = WorldHelper.getGameDateTime(world);
WorldHelper.log(world, "Current time: " + gameDateTime);

// Get specific time components
int year = WorldHelper.getYear(world); // Current year
int dayOfYear = WorldHelper.getDayOfYear(world); // Day of year (1-365)
int hour = WorldHelper.getCurrentHour(world); // Hour (0-23)

// Get day progress (0.0 = midnight, 0.5 = noon, 1.0 = next midnight)
float dayProgress = WorldHelper.getDayProgress(world);
WorldHelper.log(world, "Day is " + (dayProgress * 100) + "% complete");
```

### Day/Night Detection

```java
// Check if it's day or night
if (WorldHelper.isDaytime(world)) {
 WorldHelper.log(world, "It's daytime!");
}

if (WorldHelper.isNighttime(world)) {
 WorldHelper.log(world, "It's nighttime!");
}

// Get sunlight factor (0.0 = night, 1.0 = full daylight)
double sunlight = WorldHelper.getSunlightFactor(world);
WorldHelper.log(world, "Sunlight: " + (sunlight * 100) + "%");
```

### Moon Phase

```java
// Get current moon phase (0-7 by default)
int moonPhase = WorldHelper.getMoonPhase(world);
WorldHelper.log(world, "Moon phase: " + moonPhase);
```

### Setting Time

```java
// Set time of day (0.0-1.0)
WorldHelper.setDayTime(world, 0.0); // Midnight
WorldHelper.setDayTime(world, 0.25); // Sunrise
WorldHelper.setDayTime(world, 0.5); // Noon
WorldHelper.setDayTime(world, 0.75); // Sunset

// Set specific game time
Instant newTime = Instant.parse("2024-06-15T12:00:00Z");
WorldHelper.setGameTime(world, newTime);
```

### Practical Time Examples

```java
// Spawn hostile mobs only at night
if (WorldHelper.isNighttime(world)) {
 EntityHelper.spawnNPC(world, "Skeleton_Fighter", x, y, z);
}

// Change behavior based on time of day
int hour = WorldHelper.getCurrentHour(world);
if (hour >= 6 && hour < 18) {
 // Daytime behavior (6 AM - 6 PM)
 WorldHelper.log(world, "NPCs are active");
} else {
 // Nighttime behavior
 WorldHelper.log(world, "NPCs are sleeping");
}

// Moon phase events
int moonPhase = WorldHelper.getMoonPhase(world);
if (moonPhase == 0) {
 WorldHelper.log(world, "Full moon! Werewolves appear!");
}
```

## See Also

- [EventHelper](EventHelper) - For event handling
- [EntityHelper](EntityHelper) - For entity operations
- [Home](Home) - Back to main page
