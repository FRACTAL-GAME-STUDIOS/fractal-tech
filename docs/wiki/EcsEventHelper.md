# EcsEventHelper

EcsEventHelper provides simplified access to ECS-based events in Hytale. These events require Entity Component System (ECS) registration, which EcsEventHelper handles automatically for you.

## Overview

**What it does:**
- Creates and registers ECS systems automatically
- Provides simple callbacks for block breaking, placing, and damage tracking
- Filters out false positives (e.g., "Empty" blocks during placement)
- Extracts block type, item information, and mining progress from events

**When to use:**
- Detecting when players break or place blocks
- Tracking mining progress and tool effectiveness
- Building protection systems or region management
- Tracking block modifications
- Creating custom building or mining mechanics

**Note:** Crafting detection is available through `EventHelper.onCraftRecipe()` instead of ECS events.

## Important Note

⚠️ **ECS events must be registered after you have a World instance.** Register them in the `AddPlayerToWorldEvent` callback, not in your plugin's `setup()` method.

## Available Methods

### onBlockBreak(world, callback)

Detects when a player breaks a block.

**Callback Parameters (Basic):**
- `Vector3i position` - The exact position of the broken block
- `String blockTypeId` - The block type ID (e.g., "Soil_Dirt", "Rock_Stone")

**Callback Parameters (With Player Entity):**
- `Vector3i position` - The exact position of the broken block
- `String blockTypeId` - The block type ID (e.g., "Soil_Dirt", "Rock_Stone")
- `Entity playerEntity` - The player entity who broke the block

**Features:**
- Automatically filters out "Empty" blocks (prevents false positives during block placement)
- Provides block type ID for identifying what was broken
- Fires for all block breaking actions
- Optional player entity parameter for accessing player-specific data

**Example (Basic):**
```java
EcsEventHelper.onBlockBreak(world, (position, blockTypeId) -> {
 getLogger().at(Level.INFO).log("Block broken: " + blockTypeId + " at " + position);
 
 // Example: Track mining statistics
 if (blockTypeId.contains("Ore_")) {
 // Player mined an ore block
 incrementMiningStats(blockTypeId);
 }
});
```

**Example (With Player Entity):**
```java
EcsEventHelper.onBlockBreak(world, (position, blockTypeId, playerEntity) -> {
 if (playerEntity != null) {
 String playerName = playerEntity.getLegacyDisplayName();
 getLogger().at(Level.INFO).log(playerName + " broke " + blockTypeId + " at " + position);
 
 // Example: Drain stamina when breaking blocks
 StatsHelper.addStat(playerEntity, "Stamina", -2.0f);
 
 // Example: Check player permissions
 if (!hasPermission(playerEntity, "build.break")) {
 // Restore the block
 BlockHelper.setBlockByName(world, position, blockTypeId);
 }
 }
});
```

### onBlockPlace(world, callback)

Detects when a player places a block.

**Callback Parameters (Basic):**
- `Vector3i position` - The exact position where the block was placed
- `String itemId` - The item ID being placed from the player's hand

**Callback Parameters (With Player Entity):**
- `Vector3i position` - The exact position where the block was placed
- `String itemId` - The item ID being placed from the player's hand
- `Entity playerEntity` - The player entity who placed the block

**Features:**
- Provides the item ID being placed
- Provides exact block position
- Fires for all block placements
- Optional player entity parameter for accessing player-specific data

**Example (Basic):**
```java
EcsEventHelper.onBlockPlace(world, (position, itemId) -> {
 getLogger().at(Level.INFO).log("Block placed: " + itemId + " at " + position);
 
 // Example: Track building statistics
 incrementBlocksPlaced(itemId);
});
```

**Example (With Player Entity):**
```java
EcsEventHelper.onBlockPlace(world, (position, itemId, playerEntity) -> {
 if (playerEntity != null) {
 String playerName = playerEntity.getLegacyDisplayName();
 getLogger().at(Level.INFO).log(playerName + " placed " + itemId + " at " + position);
 
 // Example: Reward player for building
 if (itemId.contains("Wood")) {
 StatsHelper.addStat(playerEntity, "Mana", 1.0f);
 }
 
 // Example: Check build permissions
 if (!canBuildHere(playerEntity, position)) {
 BlockHelper.setBlock(world, position, 0); // Remove the block
 }
 }
});
```

### onBlockDamage(world, callback)

Detects when a player damages a block (mining progress tracking).

**Callback Parameters (Basic):**
- `Vector3i position` - The exact position of the block being damaged
- `String blockTypeId` - The block type ID (e.g., "Rock_Stone")
- `float currentDamage` - The current accumulated damage on the block (0.0 to 1.0+)
- `float damage` - The amount of damage being applied this tick
- `String itemInHand` - The item ID in the player's hand (null if empty/hand)

**Callback Parameters (With Player Entity):**
- `Vector3i position` - The exact position of the block being damaged
- `String blockTypeId` - The block type ID (e.g., "Rock_Stone")
- `float currentDamage` - The current accumulated damage on the block (0.0 to 1.0+)
- `float damage` - The amount of damage being applied this tick
- `String itemInHand` - The item ID in the player's hand (null if empty/hand)
- `Entity playerEntity` - The player entity damaging the block

**Features:**
- Fires continuously while a player is mining/damaging a block
- Provides real-time mining progress information
- Shows what tool is being used
- Tracks damage accumulation
- Optional player entity parameter for accessing player-specific data

**Example (Basic):**
```java
EcsEventHelper.onBlockDamage(world, (position, blockTypeId, currentDamage, damage, itemInHand) -> {
 String tool = itemInHand != null ? itemInHand : "Hand";
 
 getLogger().at(Level.INFO).log("Mining " + blockTypeId + " at " + position);
 getLogger().at(Level.INFO).log(" Progress: " + String.format("%.1f%%", currentDamage * 100));
 getLogger().at(Level.INFO).log(" Tool: " + tool);
 
 // Example: Warn when block is almost broken
 if (currentDamage >= 0.9f) {
 WorldHelper.log(world, "Block almost broken!");
 }
 
 // Example: Track mining speed with different tools
 if ("Pickaxe_Diamond".equals(itemInHand)) {
 // Player is using diamond pickaxe - fast mining!
 }
});
```

**Example (With Player Entity):**
```java
EcsEventHelper.onBlockDamage(world, (position, blockTypeId, currentDamage, damage, itemInHand, playerEntity) -> {
 if (playerEntity != null) {
 String playerName = playerEntity.getLegacyDisplayName();
 String tool = itemInHand != null ? itemInHand : "Hand";
 
 getLogger().at(Level.INFO).log(playerName + " mining " + blockTypeId + 
 " - " + String.format("%.1f%%", currentDamage * 100) + " with " + tool);
 
 // Example: Drain stamina while mining
 StatsHelper.addStat(playerEntity, "Stamina", -0.5f);
 
 // Example: Apply mining fatigue if low on stamina
 float stamina = StatsHelper.getStamina(playerEntity);
 if (stamina < 10.0f) {
 // Slow down mining by reducing damage
 // (Note: This is just for demonstration, actual implementation would vary)
 }
 
 // Example: Grant XP for mining
 if (currentDamage >= 1.0f) {
 grantMiningXP(playerEntity, blockTypeId);
 }
 }
});
```

**Use Cases:**
- Mining progress bars/indicators
- Tool effectiveness tracking
- Mining speed analysis
- Custom mining mechanics
- Block hardness testing
- Mining fatigue effects
- Protected block warnings

### onZoneDiscovery(world, callback)

Detects when a player discovers a new zone on the map.

**Callback Parameters:**
- `WorldMapTracker.ZoneDiscoveryInfo discoveryInfo` - Complete zone discovery information

**ZoneDiscoveryInfo Fields:**
- `zoneName()` - The name of the discovered zone
- `regionName()` - The region the zone belongs to
- `display()` - Whether to display the discovery notification
- `discoverySoundEventId()` - Sound event ID to play (can be null)
- `icon()` - Zone icon identifier (can be null)
- `major()` - Whether this is a major zone discovery
- `duration()` - Display duration in seconds
- `fadeInDuration()` - Fade in animation duration
- `fadeOutDuration()` - Fade out animation duration

**Features:**
- Fires when a player enters a new zone for the first time
- Provides complete zone metadata
- Distinguishes between major and minor zones
- Includes display and sound settings

**Example:**
```java
EcsEventHelper.onZoneDiscovery(world, (discoveryInfo) -> {
 String zoneName = discoveryInfo.zoneName();
 String region = discoveryInfo.regionName();
 
 getLogger().at(Level.INFO).log("Player discovered: " + zoneName + " in " + region);
 
 // Example: Track exploration progress
 if (discoveryInfo.major()) {
 // Major zone discovered - award achievement
 WorldHelper.log(world, "Major discovery: " + zoneName + "!");
 }
 
 // Example: Custom rewards for specific zones
 if ("Emerald_Grove".equals(zoneName)) {
 // Give bonus items for discovering Emerald Grove
 InventoryHelper.giveItem(player, "Gem_Emerald", 5);
 }
});
```

**Use Cases:**
- Exploration tracking and statistics
- Achievement systems for map discovery
- Custom zone discovery rewards
- Quest progression based on exploration
- Zone-specific welcome messages
- Map completion tracking
- Region unlock systems

## Complete Usage Example

```java
@Override
protected void setup() {
 getLogger().at(Level.INFO).log("Setting up plugin...");
 
 // Register simple global events first
 EventHelper.onPlayerChat(this, (username, message) -> {
 getLogger().at(Level.INFO).log("[Chat] " + username + ": " + message);
 });
 
 EventHelper.onItemDrop(this, (itemId, quantity) -> {
 getLogger().at(Level.INFO).log("[Drop] " + quantity + "x " + itemId);
 });
 
 // Register ECS events when world is available
 this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, (event) -> {
 World world = event.getWorld();
 getLogger().at(Level.INFO).log("World available, registering ECS events...");
 
 // Now we can register ECS events
 EcsEventHelper.onBlockBreak(world, (position, blockTypeId) -> {
 getLogger().at(Level.INFO).log("[Break] " + blockTypeId + " at " + position);
 });
 
 EcsEventHelper.onBlockPlace(world, (position, itemId) -> {
 getLogger().at(Level.INFO).log("[Place] " + itemId + " at " + position);
 });
 
 EcsEventHelper.onBlockDamage(world, (position, blockTypeId, currentDamage, damage, itemInHand) -> {
 String tool = itemInHand != null ? itemInHand : "Hand";
 getLogger().at(Level.INFO).log("[Damage] " + blockTypeId + " - " + 
 String.format("%.1f%%", currentDamage * 100) + " with " + tool);
 });
 
 EcsEventHelper.onZoneDiscovery(world, (discoveryInfo) -> {
 getLogger().at(Level.INFO).log("[Discovery] " + discoveryInfo.zoneName() + 
 " in " + discoveryInfo.regionName());
 });
 });
}
```

## Practical Examples

### Example 1: Protected Region System

```java
EcsEventHelper.onBlockBreak(world, (position, blockTypeId) -> {
 if (isInProtectedRegion(position)) {
 // Restore the block that was broken
 BlockHelper.setBlockByName(world, position, blockTypeId);
 WorldHelper.broadcastMessage(world, Message.raw("Cannot break blocks in protected area!"));
 }
});

private boolean isInProtectedRegion(Vector3i position) {
 // Check if position is in spawn protection (0,0 to 100,100)
 return position.getX() >= 0 && position.getX() <= 100 &&
 position.getZ() >= 0 && position.getZ() <= 100;
}
```

### Example 2: Build Height Limit

```java
EcsEventHelper.onBlockPlace(world, (position, itemId) -> {
 if (position.getY() > 100) {
 // Remove the placed block
 BlockHelper.setBlock(world, position, 0); // 0 = air
 WorldHelper.broadcastMessage(world, Message.raw("Cannot build above Y=100!"));
 }
});
```

### Example 3: Mining Statistics Tracker

```java
// Track what blocks players mine
private Map<String, Integer> miningStats = new HashMap<>();

EcsEventHelper.onBlockBreak(world, (position, blockTypeId) -> {
 // Increment counter for this block type
 miningStats.put(blockTypeId, miningStats.getOrDefault(blockTypeId, 0) + 1);
 
 // Special handling for ores
 if (blockTypeId.contains("Ore_Diamond")) {
 WorldHelper.broadcastMessage(world, Message.raw("A player found diamond ore!"));
 }
 
 // Log stats every 100 blocks
 int totalMined = miningStats.values().stream().mapToInt(Integer::intValue).sum();
 if (totalMined % 100 == 0) {
 getLogger().at(Level.INFO).log("Total blocks mined: " + totalMined);
 getLogger().at(Level.INFO).log("Most mined: " + getMostMinedBlock());
 }
});

private String getMostMinedBlock() {
 return miningStats.entrySet().stream()
 .max(Map.Entry.comparingByValue())
 .map(Map.Entry::getKey)
 .orElse("None");
}
```

### Example 4: Building Contest Tracker

```java
// Track blocks placed by players
private Map<String, Integer> buildingStats = new HashMap<>();

EcsEventHelper.onBlockPlace(world, (position, itemId) -> {
 // Get player who placed the block (you'd need to track this)
 String playerName = getCurrentPlayer(); // Implement this
 
 // Increment counter
 String key = playerName + ":" + itemId;
 buildingStats.put(key, buildingStats.getOrDefault(key, 0) + 1);
 
 // Check for milestones
 int totalPlaced = buildingStats.values().stream().mapToInt(Integer::intValue).sum();
 if (totalPlaced == 1000) {
 WorldHelper.broadcastMessage(world, Message.raw("1000 blocks placed in the building contest!"));
 }
});
```

### Example 5: Custom Block Drop System

```java
EcsEventHelper.onBlockBreak(world, (position, blockTypeId) -> {
 // Custom drops for specific blocks
 if (blockTypeId.equals("Rock_Stone")) {
 // 10% chance to drop extra cobblestone
 if (Math.random() < 0.1) {
 // Spawn extra item at the block position
 spawnItem(world, position, "Rock_Stone_Cobble", 1);
 }
 }
 
 if (blockTypeId.contains("Ore_")) {
 // Double ore drops on weekends
 if (isWeekend()) {
 String oreType = blockTypeId.replace("Ore_", "Ingot_");
 spawnItem(world, position, oreType, 2);
 }
 }
});
```

## Technical Details

### How It Works

When you call `EcsEventHelper.onBlockBreak()` or `onBlockPlace()`, the helper:

1. Creates a custom `EntityEventSystem` for the event type
2. Registers the system with `EntityStore.REGISTRY`
3. Sets up the query to target player entities using `PlayerRef.getComponentType()`
4. Configures dependencies using `RootDependency.first()`
5. Wraps your callback with error handling

This all happens automatically - you just provide the callback!

### Why "Empty" Blocks Are Filtered

When a player places a block, Hytale internally:
1. Fires a `BreakBlockEvent` for the "Empty" block at that position
2. Then fires a `PlaceBlockEvent` for the actual block

The `onBlockBreak()` method filters out "Empty" blocks to prevent false positives. If you need to detect when air blocks are explicitly broken (which is rare), you'll need to use the raw `BreakBlockEvent` directly.

### Performance Considerations

ECS events are efficient because they:
- Only fire for entities matching the query (players in this case)
- Run on the world's main thread (thread-safe)
- Are managed by Hytale's optimized ECS system

However, avoid expensive operations in your callbacks. If you need to do heavy processing, use `WorldHelper.executeOnWorldThread()` to defer it.

## Comparison with EventHelper

| Feature | EventHelper | EcsEventHelper |
|---------|-------------|----------------|
| Registration | Plugin setup | After World available |
| Event Types | Global events | ECS events |
| Examples | Chat, items, player join/leave | Block break/place |
| Complexity | Simple | Handles ECS complexity |
| When to use | Most events | Block-related events |

## See Also

- [EventHelper](EventHelper) - For simple global events
- [BlockHelper](BlockHelper) - For block manipulation
- [WorldHelper](WorldHelper) - For world operations

## Common Issues

### Issue: "ECS events not firing"

**Solution:** Make sure you're registering them in the `AddPlayerToWorldEvent` callback, not in `setup()`:

```java
// ❌ WRONG - Don't do this
@Override
protected void setup() {
 EcsEventHelper.onBlockBreak(world, ...); // world is null here!
}

// ✅ CORRECT - Do this
@Override
protected void setup() {
 this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, (event) -> {
 World world = event.getWorld();
 EcsEventHelper.onBlockBreak(world, ...); // Now world is available!
 });
}
```

### Issue: "Getting block break events when placing blocks"

**Solution:** This is already handled! The `onBlockBreak()` method filters out "Empty" blocks automatically. If you're still seeing issues, make sure you're using the latest version of HytaleBoilerplate.

### Issue: "Can't cancel events"

**Note:** ECS event callbacks in HytaleBoilerplate don't support cancellation directly. However, you can:
- Restore broken blocks using `BlockHelper.setBlockByName()`
- Remove placed blocks using `BlockHelper.setBlock(world, position, 0)`
- This achieves the same result as cancellation
