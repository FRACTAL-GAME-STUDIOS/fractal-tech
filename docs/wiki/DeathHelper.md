# DeathHelper

**DeathHelper** provides a simple way to track entity deaths in your Hytale plugin. It fires when any entity dies (players or NPCs), providing the entity that died, its death position, and detailed information about what killed it (similar to Minecraft's DamageSource).

## Features

- **Entity Death Tracking** - Detect when any entity dies (players, NPCs, mobs)
- **Death Position** - Get exact coordinates where the entity died
- **Damage Source Information** - Identify what killed the entity (entity, projectile, environment, etc.)
- **Killer Details** - Get the killer's name and type (player or NPC)
- **Easy-to-Use API** - Simple helper methods for common queries
- **Automatic Name Resolution** - Automatically extracts player usernames and NPC role names

## When to use

- Death logging and statistics
- Custom death messages
- Death-based rewards or penalties
- PvP/PvE tracking
- Death location markers
- Loot drop customization based on killer
- Achievement systems

## API Reference

### onEntityDeath(world, callback)

Register a callback that fires when any entity dies.

**Parameters:**
- `world` - The world to track deaths in
- `callback` - Consumer that receives an `EntityDeath` object

**Example:**
```java
DeathHelper.onEntityDeath(world, (death) -> {
 String entityName = death.getEntityName();
 String entityType = death.isPlayer() ? "Player" : "NPC";
 
 getLogger().at(Level.INFO).log("Entity died: " + entityName + " (" + entityType + ")");
});
```

## EntityDeath Methods

The `EntityDeath` object provides convenient methods to access death information:

### Entity Information

**getEntityName()** - Get the name of the entity that died
- Returns player username for players
- Returns NPC role name for NPCs (e.g., "Cow", "Skeleton_Fighter")
- Returns "Unknown" if unavailable

**isPlayer()** - Check if the entity that died is a player
- Returns `true` if player, `false` if NPC

**getEntityRef()** - Get the entity reference
- Returns `Ref<EntityStore>` for advanced ECS operations

### Death Location

**getPosition()** - Get the position where the entity died
- Returns `Vector3d` with x, y, z coordinates
- Returns `null` if position unavailable

### Damage Information

**getDamageInfo()** - Get the damage object
- Returns `Damage` object with full damage details
- Returns `null` if damage info unavailable

**getDamageSource()** - Get the damage source
- Returns `Damage.Source` (EntitySource, ProjectileSource, EnvironmentSource, etc.)

### Killer Information

**wasKilledByEntity()** - Check if killed by another entity
- Returns `true` if killed by entity (player or NPC)

**wasKilledByProjectile()** - Check if killed by a projectile
- Returns `true` if killed by arrow, thrown item, etc.

**wasKilledByEnvironment()** - Check if killed by environment
- Returns `true` if killed by fall damage, drowning, fire, etc.

**getKillerRef()** - Get the killer's entity reference
- Returns `Ref<EntityStore>` of the killer
- Returns `null` if not killed by entity/projectile

**getKillerName()** - Get the name of the killer
- Returns player username for players
- Returns NPC role name for NPCs
- Returns `null` if not killed by entity

**isKillerPlayer()** - Check if the killer is a player
- Returns `true` if killer is a player, `false` if NPC

**getEnvironmentType()** - Get the environment type if killed by environment
- Returns environment type string (e.g., "fall", "drown")
- Returns `null` if not killed by environment

### Advanced Access

**getStore()** - Get the ECS store for direct component access
- Returns `Store<EntityStore>` for advanced operations

## Examples

### Basic Death Logging

Log all entity deaths with basic information:

```java
DeathHelper.onEntityDeath(world, (death) -> {
 String name = death.getEntityName();
 String type = death.isPlayer() ? "Player" : "NPC";
 Vector3d pos = death.getPosition();
 
 getLogger().at(Level.INFO).log(name + " (" + type + ") died at " + 
 String.format("X=%.2f Y=%.2f Z=%.2f", pos.x, pos.y, pos.z));
});
```

### Detailed Death Information

Log comprehensive death details including killer and damage:

```java
DeathHelper.onEntityDeath(world, (death) -> {
 // Entity that died
 String entityName = death.getEntityName();
 String entityType = death.isPlayer() ? "Player" : "NPC";
 
 getLogger().at(Level.INFO).log("=== ENTITY DEATH ===");
 getLogger().at(Level.INFO).log("Died: " + entityName + " (" + entityType + ")");
 
 // Death position
 Vector3d pos = death.getPosition();
 if (pos != null) {
 getLogger().at(Level.INFO).log("Position: " + 
 String.format("X=%.2f Y=%.2f Z=%.2f", pos.x, pos.y, pos.z));
 }
 
 // Killer information
 if (death.wasKilledByEntity()) {
 String killerName = death.getKillerName();
 String killerType = death.isKillerPlayer() ? "Player" : "NPC";
 getLogger().at(Level.INFO).log("Killed by: " + killerName + " (" + killerType + ")");
 } else if (death.wasKilledByProjectile()) {
 String shooterName = death.getKillerName();
 String shooterType = death.isKillerPlayer() ? "Player" : "NPC";
 getLogger().at(Level.INFO).log("Shot by: " + shooterName + " (" + shooterType + ")");
 } else if (death.wasKilledByEnvironment()) {
 String envType = death.getEnvironmentType();
 getLogger().at(Level.INFO).log("Killed by environment: " + envType);
 }
 
 // Damage details
 Damage damageInfo = death.getDamageInfo();
 if (damageInfo != null) {
 getLogger().at(Level.INFO).log("Damage: " + damageInfo.getAmount());
 DamageCause cause = damageInfo.getCause();
 if (cause != null) {
 getLogger().at(Level.INFO).log("Cause: " + cause.getId());
 }
 }
});
```

### PvP Kill Tracking

Track player vs player kills:

```java
DeathHelper.onEntityDeath(world, (death) -> {
 // Only track player deaths
 if (!death.isPlayer()) {
 return;
 }
 
 // Only track PvP kills
 if (!death.wasKilledByEntity() || !death.isKillerPlayer()) {
 return;
 }
 
 String victim = death.getEntityName();
 String killer = death.getKillerName();
 
 getLogger().at(Level.INFO).log("[PvP] " + killer + " killed " + victim);
 
 // Award points, update leaderboard, etc.
});
```

### NPC Death Rewards

Give rewards when players kill specific NPCs:

```java
DeathHelper.onEntityDeath(world, (death) -> {
 // Only track NPC deaths
 if (death.isPlayer()) {
 return;
 }
 
 // Only track kills by players
 if (!death.wasKilledByEntity() || !death.isKillerPlayer()) {
 return;
 }
 
 String npcType = death.getEntityName();
 String playerName = death.getKillerName();
 
 // Give rewards based on NPC type
 switch (npcType) {
 case "Skeleton_Fighter":
 getLogger().at(Level.INFO).log(playerName + " killed a Skeleton! +10 XP");
 // Award XP, items, etc.
 break;
 case "Bear_Grizzly":
 getLogger().at(Level.INFO).log(playerName + " killed a Bear! +25 XP");
 // Award XP, items, etc.
 break;
 }
});
```

### Death Location Markers

Create markers at death locations:

```java
DeathHelper.onEntityDeath(world, (death) -> {
 // Only track player deaths
 if (!death.isPlayer()) {
 return;
 }
 
 Vector3d deathPos = death.getPosition();
 if (deathPos == null) {
 return;
 }
 
 String playerName = death.getEntityName();
 
 // Create a death marker (e.g., spawn a block, particle effect, etc.)
 getLogger().at(Level.INFO).log("Creating death marker for " + playerName + 
 " at " + String.format("%.2f, %.2f, %.2f", deathPos.x, deathPos.y, deathPos.z));
 
 // You could:
 // - Place a gravestone block
 // - Spawn particles
 // - Create a temporary waypoint
 // - Store location for /back command
});
```

### Environmental Death Tracking

Track deaths from specific environmental causes:

```java
DeathHelper.onEntityDeath(world, (death) -> {
 if (!death.wasKilledByEnvironment()) {
 return;
 }
 
 String entityName = death.getEntityName();
 String envType = death.getEnvironmentType();
 
 if (envType != null) {
 getLogger().at(Level.INFO).log(entityName + " died from: " + envType);
 
 // Track statistics
 // "fall" - fell from height
 // "drown" - drowned
 // "fire" - burned
 // etc.
 }
});
```

### Custom Death Messages

Send custom death messages to all players:

```java
DeathHelper.onEntityDeath(world, (death) -> {
 String victim = death.getEntityName();
 String message;
 
 if (death.wasKilledByEntity()) {
 String killer = death.getKillerName();
 if (death.isKillerPlayer()) {
 message = victim + " was slain by " + killer;
 } else {
 message = victim + " was killed by a " + killer;
 }
 } else if (death.wasKilledByProjectile()) {
 String shooter = death.getKillerName();
 message = victim + " was shot by " + shooter;
 } else if (death.wasKilledByEnvironment()) {
 String envType = death.getEnvironmentType();
 message = victim + " died from " + envType;
 } else {
 message = victim + " died";
 }
 
 // Broadcast to all players
 for (Entity player : world.getPlayers()) {
 // Send message to player
 getLogger().at(Level.INFO).log("[Death] " + message);
 }
});
```

## How It Works

DeathHelper uses Hytale's Entity Component System (ECS) to listen for `DeathComponent` additions. When an entity dies:

1. The ECS system detects the `DeathComponent` being added to the entity
2. DeathHelper extracts:
 - Entity reference and position from `TransformComponent`
 - Damage information from `DeathComponent`
 - Player username from `PlayerRef` component
 - NPC role name from the entity's role object
3. An `EntityDeath` object is created with all this information
4. Your callback is invoked with the `EntityDeath` object

## Notes

- **Automatic Name Resolution**: Player names and NPC role names are automatically extracted
- **NPC Role Names**: NPCs return their role name (e.g., "Cow", "Skeleton_Fighter", "Deer_Doe")
- **Thread-Safe**: The callback runs on the ECS system thread
- **Performance**: Minimal overhead - only processes actual death events
- **Damage Source Types**: 
 - `EntitySource` - Killed by another entity (player or NPC)
 - `ProjectileSource` - Killed by a projectile (arrow, thrown item)
 - `EnvironmentSource` - Killed by environment (fall, drown, fire)
 - `CommandSource` - Killed by command (e.g., /kill)

## See Also

- [EntityHelper](EntityHelper) - Get entity information, spawn NPCs, teleport entities
- [EcsEventHelper](EcsEventHelper) - Listen to other ECS events (block break, zone discovery, etc.)
- [EventHelper](EventHelper) - Listen to player join/leave events
