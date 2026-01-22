# EntityHelper

Fundamental entity operations and spatial queries.

## Overview

**What it does:**
- Find players by name or UUID
- Get entity positions and calculate distances
- Teleport entities to locations
- Find players within radius of a position or entity
- Check entity existence and type
- Get player home/respawn positions
- Iterate through all entities
- Spawn NPCs by role name

**When to use:**
- Finding specific players in the world
- Proximity detection (players near a location)
- Teleportation mechanics
- Distance-based game logic
- Entity validation and queries
- NPC spawning

## Available Methods

### getPlayerByName(world, name)

Find a player by their display name (case-insensitive).

```java
Entity player = EntityHelper.getPlayerByName(world, "Se7enity");
if (player != null) {
 WorldHelper.log(world, "Found player: " + EntityHelper.getName(player));
}
```

### getPlayerByUUID(world, uuid)

Find a player by their UUID.

```java
UUID playerUuid = UUID.fromString("c3257f18-4326-4089-9231-60120125d5d7");
Entity player = EntityHelper.getPlayerByUUID(world, playerUuid);
```

### getPosition(entity) / teleport(entity, position)

Get or set entity positions.

```java
// Get position
Vector3d pos = EntityHelper.getPosition(player);
WorldHelper.log(world, "Player at: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());

// Teleport to position
EntityHelper.teleport(player, new Vector3d(100, 64, 100));

// Teleport to coordinates
EntityHelper.teleport(player, 100, 64, 100);
```

### getDistance(entity1, entity2) / getDistance(entity, position)

Calculate distances between entities or positions.

```java
// Distance between two players
double distance = EntityHelper.getDistance(player1, player2);
WorldHelper.log(world, "Distance: " + distance + " blocks");

// Distance to a location
Vector3d spawn = new Vector3d(0, 64, 0);
double distanceToSpawn = EntityHelper.getDistance(player, spawn);
```

### getPlayersInRadius(world, center, radius) / getPlayersInRadius(entity, radius)

Find all players within a radius.

```java
// Players within 50 blocks of a position
Vector3d center = new Vector3d(100, 64, 100);
List<Entity> nearbyPlayers = EntityHelper.getPlayersInRadius(world, center, 50.0);

// Players within 10 blocks of an entity
List<Entity> closeBy = EntityHelper.getPlayersInRadius(targetEntity, 10.0);

// Example: Broadcast to nearby players
for (Entity nearby : nearbyPlayers) {
 // Send message to each nearby player
}
```

### isWithinDistance(entity, position, distance)

Check if an entity is within a certain distance.

```java
Vector3d checkpoint = new Vector3d(200, 64, 200);
if (EntityHelper.isWithinDistance(player, checkpoint, 5.0)) {
 WorldHelper.broadcastMessage(world, Message.raw("Player reached checkpoint!"));
}

// Check distance between two entities
if (EntityHelper.isWithinDistance(player, boss, 20.0)) {
 // Player is within boss aggro range
}
```

### isPlayer(entity) / exists(entity) / getName(entity) / getEntityType(entity)

Entity validation and information.

```java
// Check if entity is a player
if (EntityHelper.isPlayer(entity)) {
 WorldHelper.log(world, "This is a player!");
}

// Check if entity still exists
if (EntityHelper.exists(entity)) {
 // Entity is valid and not removed
}

// Get entity name (retrieves username from PlayerRef for players)
String name = EntityHelper.getName(entity);
WorldHelper.log(world, "Player name: " + name);

// Get entity type (returns readable names for NPCs)
String type = EntityHelper.getEntityType(entity);
WorldHelper.log(world, "Entity type: " + type);
// Returns: "Minnow", "Deer_Doe", "Skeleton_Fighter", "Fox", etc. for NPCs
// Returns: "Player", "ItemEntity", etc. for other entity types
```

**Note:** `getName()` uses the ECS system to retrieve player usernames from the `PlayerRef` component. For NPCs and other entities, it returns the entity class name as a fallback.

**Note:** `getEntityType()` extracts the `roleName` field from NPC role objects to return readable entity type names like "Minnow", "Deer_Doe", "Trork_Warrior", etc. For NPCs without initialized roles, it returns "NPCEntity_Uninitialized". For non-NPC entities, it returns the class name (e.g., "Player", "ItemEntity").

### getPlayerHome(player) / getPlayerRespawnPosition(player) / teleportPlayerHome(player)

Get and use player home/respawn locations.

```java
// Get player's home/respawn position (bed or world spawn)
Vector3d homePos = EntityHelper.getPlayerHome(player);
if (homePos != null) {
 WorldHelper.log(world, "Player's home is at: " + homePos);
 double distance = EntityHelper.getDistance(player, homePos);
 WorldHelper.log(world, "Distance from home: " + distance + " blocks");
}

// Get full Transform (position + rotation)
Transform respawnTransform = EntityHelper.getPlayerRespawnPosition(player);
if (respawnTransform != null) {
 Vector3d pos = respawnTransform.getPosition();
 Vector3f rotation = respawnTransform.getRotation();
}

// Teleport player to their home/respawn location
if (EntityHelper.teleportPlayerHome(player)) {
 WorldHelper.log(world, "Teleported player home!");
}
```

**Note:** These methods use `Player.getRespawnPosition()` which returns the player's bed spawn point if they have one, or the world spawn point otherwise. This is the same logic used by Hytale's respawn system.

### getAllEntities(world) / getClosestEntity(...)

Entity iteration and proximity searches.

```java
// Get all loaded entities in the world
List<Entity> allEntities = EntityHelper.getAllEntities(world);
WorldHelper.log(world, "Total entities: " + allEntities.size());

// Iterate through all entities
for (Entity entity : allEntities) {
 String type = EntityHelper.getEntityType(entity);
 WorldHelper.log(world, "Found entity of type: " + type);
}

// Find closest entity to a position
Vector3d searchPos = new Vector3d(100, 64, 100);
Entity closest = EntityHelper.getClosestEntity(world, searchPos);
if (closest != null) {
 WorldHelper.log(world, "Closest entity: " + EntityHelper.getEntityType(closest));
}

// Find closest entity to another entity within range
Entity nearbyEntity = EntityHelper.getClosestEntity(player, 50.0);
if (nearbyEntity != null) {
 double distance = EntityHelper.getDistance(player, nearbyEntity);
 WorldHelper.log(world, "Found entity " + distance + " blocks away");
}

// Find closest entity excluding a specific entity (e.g., exclude the player)
Entity closestNonPlayer = EntityHelper.getClosestEntity(world, playerPos, player);
if (closestNonPlayer != null) {
 String type = EntityHelper.getEntityType(closestNonPlayer);
 WorldHelper.log(world, "Closest non-player entity: " + type);
}
```

**Note:** `getAllEntities()` uses reflection to access the `EntityStore`'s internal `entitiesByUuid` map, which contains **all loaded entities** in the world including players, NPCs, items, and all other entity types. If reflection fails (e.g., due to security restrictions), it falls back to returning only players.

**Note:** `getClosestEntity()` has an overload that accepts an `excludeEntity` parameter to exclude a specific entity from the search (useful for finding the closest entity to a player without returning the player itself).

## Filtering Entities by Type

You can filter entities by their type to work with specific entity types:

```java
// Get all entities
List<Entity> allEntities = EntityHelper.getAllEntities(world);

// Filter for only cows
List<Entity> cows = new ArrayList<>();
for (Entity entity : allEntities) {
 if ("Cow".equals(EntityHelper.getEntityType(entity))) {
 cows.add(entity);
 }
}
WorldHelper.log(world, "Found " + cows.size() + " cows");

// Teleport all cows to a specific location
Vector3d barnLocation = new Vector3d(100, 64, 100);
for (Entity cow : cows) {
 EntityHelper.teleport(cow, barnLocation);
}

// Find all hostile mobs
List<Entity> hostileMobs = new ArrayList<>();
for (Entity entity : allEntities) {
 String type = EntityHelper.getEntityType(entity);
 if (type.contains("Skeleton") || type.contains("Trork") || type.contains("Zombie")) {
 hostileMobs.add(entity);
 }
}
WorldHelper.log(world, "Found " + hostileMobs.size() + " hostile mobs");

// Count entities by type
Map<String, Integer> entityCounts = new HashMap<>();
for (Entity entity : allEntities) {
 String type = EntityHelper.getEntityType(entity);
 entityCounts.put(type, entityCounts.getOrDefault(type, 0) + 1);
}
// Log the counts
for (Map.Entry<String, Integer> entry : entityCounts.entrySet()) {
 WorldHelper.log(world, entry.getKey() + ": " + entry.getValue());
}
```

## NPC Spawning

### spawnNPC(world, roleName, position) / spawnNPC(world, roleName, x, y, z)

Spawn an NPC entity by role name (recommended method).

```java
// Spawn a cow at a position
Vector3d spawnPos = new Vector3d(100, 64, 100);
Entity cow = EntityHelper.spawnNPC(world, "Cow", spawnPos);
if (cow != null) {
 WorldHelper.log(world, "Successfully spawned a Cow!");
}

// Spawn using coordinates
Entity deer = EntityHelper.spawnNPC(world, "Deer_Doe", 105, 64, 100);

// Spawn with rotation (yaw in radians)
Entity chicken = EntityHelper.spawnNPC(world, "Chicken", 110, 64, 100, (float) Math.PI);
```

**Available NPC Role Names:**
- **Animals:** `Cow`, `Pig`, `Sheep`, `Rabbit`, `Fox`, `Deer_Doe`, `Chicken`, `Horse`
- **Fish:** `Minnow`, `Salmon`, `Pike`, `Catfish`, `Bluegill`
- **Hostile:** `Skeleton_Fighter`, `Trork_Warrior`, `Goblin`, `Zombie`
- And many more! Any NPC role name in Hytale works, find a comprehensive list here: https://hytalemodding.dev/en/docs/server/entities

**How It Works:**
EntityHelper uses Hytale's internal `NPCPlugin.spawnEntity()` method which properly:
1. Looks up the role index by name
2. Creates all required ECS components (Transform, HeadRotation, DisplayName, UUID, Model, etc.)
3. Adds the entity to the EntityStore with proper initialization
4. Returns the spawned NPCEntity

**Example: Spawn multiple entities around player**
```java
Vector3d playerPos = EntityHelper.getPosition(player);

// Spawn a cow in front
Entity cow = EntityHelper.spawnNPC(world, "Cow", 
 playerPos.getX() + 5, playerPos.getY(), playerPos.getZ());

// Spawn a deer to the right
Entity deer = EntityHelper.spawnNPC(world, "Deer_Doe",
 playerPos.getX(), playerPos.getY(), playerPos.getZ() + 5);

// Spawn a chicken behind with rotation
Entity chicken = EntityHelper.spawnNPC(world, "Chicken",
 playerPos.getX() - 5, playerPos.getY(), playerPos.getZ(), (float) Math.PI);
```

### removeNearbyItemEntities(world, position, itemId, quantity, radius)

Remove dropped item entities within a radius of a position. Used internally by ContainerHelper to clean up dropped items after cancelled transactions.

```java
// Remove all item entities within 5 blocks of a position
Vector3i chestPos = new Vector3i(100, 64, 100);
EntityHelper.removeNearbyItemEntities(world, chestPos, null, 0, 5.0);

// Remove specific item type (Food_Pork_Raw) within 10 blocks
EntityHelper.removeNearbyItemEntities(world, chestPos, "Food_Pork_Raw", 1, 10.0);
```

**Parameters:**
- `world` - The world to search in
- `position` - Center position to search from (Vector3i)
- `itemId` - Item ID to match (currently not used for filtering, pass null)
- `quantity` - Quantity to match (currently not used for filtering, pass 0)
- `radius` - Search radius in blocks

**How It Works:**
This method uses Hytale's ECS (Entity Component System) to efficiently find and remove item entities:
1. Queries the EntityStore for all entities with `ItemComponent` (dropped items)
2. Checks each item entity's distance from the center position
3. Removes entities within the specified radius using `CommandBuffer.removeEntity()`

**Note:** This is primarily used internally by ContainerHelper to prevent item duplication when transactions are cancelled. The `itemId` and `quantity` parameters are reserved for future filtering functionality.

## See Also

- [WorldHelper](WorldHelper) - For world operations
- [BlockHelper](BlockHelper) - For block operations
- [ContainerHelper](ContainerHelper) - For container transaction management
- [Home](Home) - Back to main page
