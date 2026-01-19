# ZoneHelper

A utility class for zone/region management and discovery tracking in Hytale. Provides methods for tracking player zone discoveries, querying current zones, and managing zone-related data.

## Table of Contents
- [Overview](#overview)
- [Initialization](#initialization)
- [Current Zone Queries](#current-zone-queries)
- [Discovery Tracking](#discovery-tracking)
- [Zone Statistics](#zone-statistics)
- [Player Queries](#player-queries)

---

## Overview

**What are Zones?**

Zones in Hytale are named regions of the world that players can discover. When a player enters a new zone for the first time, a discovery event is triggered. ZoneHelper provides utilities to:
- Track which zones each player has discovered
- Query a player's current zone
- Find all players in a specific zone
- Get statistics about zone discoveries

**Important:** Zone tracking must be initialized when the world loads using `initializeZoneTracking()`.

---

## Initialization

### `initializeZoneTracking(World world)`

Initialize zone discovery tracking for a world. This should be called once when the world loads.

**Parameters:**
- `world` - The world to track zones in

**Example:**
```java
@Override
public void onWorldLoad(AddPlayerToWorldEvent event) {
 World world = event.getWorld();
 
 // Initialize zone tracking
 ZoneHelper.initializeZoneTracking(world);
 
 LOGGER.log("Zone tracking initialized");
}
```

**Important:** Call this in your `AddPlayerToWorldEvent` handler to ensure zone tracking works properly.

---

## Current Zone Queries

### `getCurrentZone(Entity player)`

Get the current zone a player is in.

**Parameters:**
- `player` - The player entity

**Returns:** `String` - The zone name, or null if not in a tracked zone

**Example:**
```java
String currentZone = ZoneHelper.getCurrentZone(player);
if (currentZone != null) {
 PlayerHelper.sendMessage(player, "You are in: " + currentZone);
} else {
 PlayerHelper.sendMessage(player, "You are not in a named zone");
}
```

### `setCurrentZone(Entity player, String zoneName)`

Set the current zone for a player. This is typically called automatically by zone tracking systems.

**Parameters:**
- `player` - The player entity
- `zoneName` - The zone name (null to clear)

**Example:**
```java
// Manually set player's current zone
ZoneHelper.setCurrentZone(player, "Forest_Clearing");

// Clear current zone
ZoneHelper.setCurrentZone(player, null);
```

### `isInZone(Entity player, String zoneName)`

Check if a player is in a specific zone.

**Parameters:**
- `player` - The player entity
- `zoneName` - The zone name to check

**Returns:** `boolean` - true if the player is in the specified zone

**Example:**
```java
if (ZoneHelper.isInZone(player, "Dangerous_Cave")) {
 PlayerHelper.sendMessage(player, "⚠ Warning: This area is dangerous!");
}

// Quest requirement
if (ZoneHelper.isInZone(player, "Ancient_Temple")) {
 // Complete quest objective
 PlayerHelper.sendMessage(player, "Quest objective complete!");
}
```

---

## Discovery Tracking

### `getDiscoveredZones(Entity player)`

Get all zones a player has discovered.

**Parameters:**
- `player` - The player entity

**Returns:** `List<String>` - List of discovered zone names

**Example:**
```java
List<String> discovered = ZoneHelper.getDiscoveredZones(player);

PlayerHelper.sendMessage(player, "You have discovered " + discovered.size() + " zones:");
for (String zone : discovered) {
 PlayerHelper.sendMessage(player, " - " + zone);
}
```

### `discoverZone(Entity player, String zoneName)`

Mark a zone as discovered for a player.

**Parameters:**
- `player` - The player entity
- `zoneName` - The zone name to mark as discovered

**Returns:** `boolean` - true if this is a new discovery, false if already discovered

**Example:**
```java
// Manually trigger zone discovery
boolean isNew = ZoneHelper.discoverZone(player, "Hidden_Cave");

if (isNew) {
 PlayerHelper.sendMessage(player, "🎉 You discovered: Hidden_Cave!");
 InventoryHelper.giveItem(player, "Item_Discovery_Token", 1);
} else {
 PlayerHelper.sendMessage(player, "You've been here before.");
}
```

**Use Cases:**
- Custom discovery triggers
- Quest rewards
- Secret area discoveries
- Achievement systems

### `hasDiscoveredZone(Entity player, String zoneName)`

Check if a player has discovered a specific zone.

**Parameters:**
- `player` - The player entity
- `zoneName` - The zone name to check

**Returns:** `boolean` - true if the player has discovered this zone

**Example:**
```java
// Quest requirement
if (ZoneHelper.hasDiscoveredZone(player, "Mountain_Peak")) {
 PlayerHelper.sendMessage(player, "You may now access the summit quest!");
}

// Unlock fast travel
if (ZoneHelper.hasDiscoveredZone(player, "Village_Square")) {
 // Enable teleport to village
}
```

### `clearDiscoveredZones(Entity player)`

Clear all discovered zones for a player. Useful for resetting player progress.

**Parameters:**
- `player` - The player entity

**Example:**
```java
// Reset player's exploration progress
ZoneHelper.clearDiscoveredZones(player);
PlayerHelper.sendMessage(player, "Your exploration progress has been reset.");
```

---

## Zone Statistics

### `getDiscoveredZoneCount(Entity player)`

Get the number of zones a player has discovered.

**Parameters:**
- `player` - The player entity

**Returns:** `int` - The count of discovered zones

**Example:**
```java
int count = ZoneHelper.getDiscoveredZoneCount(player);
PlayerHelper.sendMessage(player, "Zones discovered: " + count + "/50");

// Achievement check
if (count >= 25) {
 PlayerHelper.sendMessage(player, "🏆 Achievement: Explorer!");
}
```

### `getAllDiscoveredZones()`

Get all unique zones that have been discovered by any player.

**Returns:** `Set<String>` - Set of all discovered zone names

**Example:**
```java
Set<String> allZones = ZoneHelper.getAllDiscoveredZones();
WorldHelper.log(world, "Total zones discovered by all players: " + allZones.size());

for (String zone : allZones) {
 WorldHelper.log(world, " - " + zone);
}
```

### `getZoneDiscoveryCount(String zoneName)`

Get the total number of players who have discovered a specific zone.

**Parameters:**
- `zoneName` - The zone name

**Returns:** `int` - The count of players who discovered this zone

**Example:**
```java
int count = ZoneHelper.getZoneDiscoveryCount("Secret_Dungeon");
WorldHelper.log(world, count + " players have found the secret dungeon");

// Rare zone tracking
if (count < 5) {
 PlayerHelper.sendMessage(player, "You're one of the first to discover this place!");
}
```

---

## Player Queries

### `getPlayersInZone(World world, String zoneName)`

Get all players currently in a specific zone.

**Parameters:**
- `world` - The world to search in
- `zoneName` - The zone name

**Returns:** `List<Entity>` - List of players in the zone

**Example:**
```java
List<Entity> playersInZone = ZoneHelper.getPlayersInZone(world, "Arena");

if (playersInZone.size() >= 2) {
 // Start PvP match
 for (Entity player : playersInZone) {
 PlayerHelper.sendMessage(player, "⚔ Arena match starting!");
 }
}

// Zone event
if (playersInZone.size() > 0) {
 WorldHelper.log(world, "Event starting in Arena with " + playersInZone.size() + " players");
}
```

**Use Cases:**
- Zone-based events
- PvP arena management
- Group quests
- Zone population monitoring

---

## Complete Examples

### Example 1: Zone Discovery System

```java
@Override
public void onWorldLoad(AddPlayerToWorldEvent event) {
 World world = event.getWorld();
 
 // Initialize zone tracking
 ZoneHelper.initializeZoneTracking(world);
 
 // Set up zone discovery event
 EcsEventHelper.onZoneDiscovery(world, (discoveryInfo) -> {
 String zoneName = discoveryInfo.zoneName();
 
 // Find the player who discovered it
 // Note: You'd need to track this from the event context
 // For now, this logs the discovery
 WorldHelper.log(world, "Zone discovered: " + zoneName);
 });
}

// Manual zone tracking with player movement
WorldHelper.onTickInterval(world, 20, tick -> {
 List<Entity> players = EntityHelper.getAllEntities(world);
 
 for (Entity player : players) {
 if (!(player instanceof Player)) continue;
 
 // Check player position and update current zone
 // This is a simplified example
 Vector3d pos = EntityHelper.getPosition(player);
 
 if (pos.x > 100 && pos.x < 200 && pos.z > 100 && pos.z < 200) {
 String oldZone = ZoneHelper.getCurrentZone(player);
 String newZone = "Forest_Clearing";
 
 if (!newZone.equals(oldZone)) {
 ZoneHelper.setCurrentZone(player, newZone);
 
 // Check if it's a new discovery
 if (ZoneHelper.discoverZone(player, newZone)) {
 PlayerHelper.sendMessage(player, "🎉 Discovered: " + newZone);
 }
 }
 }
 }
});
```

### Example 2: Zone-Based Quest System

```java
// Quest: Discover 5 specific zones
String[] requiredZones = {
 "Ancient_Temple",
 "Dark_Forest",
 "Mountain_Peak",
 "Hidden_Cave",
 "Coastal_Village"
};

EventHelper.onPlayerChat(plugin, (username, message) -> {
 if (message.equals("!quest")) {
 Entity player = EntityHelper.getPlayerByName(world, username);
 if (player == null) return;
 
 int discovered = 0;
 List<String> missing = new ArrayList<>();
 
 for (String zone : requiredZones) {
 if (ZoneHelper.hasDiscoveredZone(player, zone)) {
 discovered++;
 } else {
 missing.add(zone);
 }
 }
 
 PlayerHelper.sendMessage(player, "Quest Progress: " + discovered + "/" + requiredZones.length);
 
 if (discovered == requiredZones.length) {
 PlayerHelper.sendMessage(player, "✅ Quest Complete! Reward: 10 Diamonds");
 InventoryHelper.giveItem(player, "Gem_Diamond", 10);
 } else {
 PlayerHelper.sendMessage(player, "Missing zones:");
 for (String zone : missing) {
 PlayerHelper.sendMessage(player, " - " + zone);
 }
 }
 }
});
```

### Example 3: Zone-Based PvP Arena

```java
// Check arena population every 5 seconds
WorldHelper.onTickInterval(world, 100, tick -> {
 List<Entity> playersInArena = ZoneHelper.getPlayersInZone(world, "PvP_Arena");
 
 if (playersInArena.size() >= 2) {
 // Start match
 for (Entity player : playersInArena) {
 PlayerHelper.sendMessage(player, "⚔ PvP Match Starting!");
 PlayerHelper.sendMessage(player, "Players: " + playersInArena.size());
 }
 } else if (playersInArena.size() == 1) {
 Entity player = playersInArena.get(0);
 PlayerHelper.sendMessage(player, "Waiting for opponent...");
 }
});
```

### Example 4: Explorer Achievement System

```java
EventHelper.onPlayerChat(plugin, (username, message) -> {
 if (message.equals("!explorer")) {
 Entity player = EntityHelper.getPlayerByName(world, username);
 if (player == null) return;
 
 int count = ZoneHelper.getDiscoveredZoneCount(player);
 List<String> zones = ZoneHelper.getDiscoveredZones(player);
 
 PlayerHelper.sendMessage(player, "=== Explorer Stats ===");
 PlayerHelper.sendMessage(player, "Zones Discovered: " + count);
 
 // Award titles based on discovery count
 if (count >= 50) {
 PlayerHelper.sendMessage(player, "🏆 Title: Master Explorer");
 } else if (count >= 25) {
 PlayerHelper.sendMessage(player, "🏆 Title: Adventurer");
 } else if (count >= 10) {
 PlayerHelper.sendMessage(player, "🏆 Title: Wanderer");
 }
 
 // Show recent discoveries
 PlayerHelper.sendMessage(player, "Recent discoveries:");
 int shown = Math.min(5, zones.size());
 for (int i = 0; i < shown; i++) {
 PlayerHelper.sendMessage(player, " - " + zones.get(i));
 }
 }
});
```

---

## Tips

1. **Initialize tracking early** - Call `initializeZoneTracking()` in your world load event
2. **Combine with EcsEventHelper** - Use `onZoneDiscovery()` for automatic tracking
3. **Manual zone updates** - Use `setCurrentZone()` for custom zone systems
4. **Quest integration** - Use `hasDiscoveredZone()` for quest requirements
5. **Statistics tracking** - Use discovery counts for achievements and leaderboards
6. **Zone events** - Use `getPlayersInZone()` for zone-based multiplayer events

---

## Important Notes

- **Thread Safety:** All methods are thread-safe using concurrent collections
- **Persistence:** Zone discoveries are stored in memory and reset on server restart
- **Player Identification:** Uses player UUID for tracking (deprecated but functional)
- **Zone Names:** Case-sensitive, use exact zone names from discovery events

---

## Related Helpers
- [EcsEventHelper](EcsEventHelper) - For zone discovery events
- [EntityHelper](EntityHelper) - For player queries and management
- [PlayerHelper](PlayerHelper) - For player messaging and utilities
- [WorldHelper](WorldHelper) - For world operations and tick scheduling
