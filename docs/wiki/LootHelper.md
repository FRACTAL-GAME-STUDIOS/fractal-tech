# LootHelper

Utilities for customizing block drops and loot tables. Allows you to override default block drops with custom items and quantities.

## Overview

**What it does:**
- Customize what items drop when blocks are broken
- Replace default block drops entirely or add bonus drops
- Spawn physical item entities with realistic physics
- Support conditional drops based on position, depth, or custom logic
- Random drop quantities and velocities

**When to use:**
- Custom ore drops (e.g., diamonds from stone)
- Bonus loot systems (e.g., extra coal from coal ore)
- Depth-based drops (e.g., rare items only below Y=20)
- Random loot tables
- Custom resource gathering mechanics

## Available Methods

### registerBlockLoot(world, blockTypeId, lootProvider)

Register custom loot that is **added alongside** default block drops.

**Parameters:**
- `World world` - The world to register the loot table in
- `String blockTypeId` - The block type ID (e.g., "Rock_Stone", "Ore_Gold_Stone")
- `BiFunction<Vector3i, String, List<ItemDrop>> lootProvider` - Function that returns list of custom drops

**Example:**
```java
// Add bonus cobble drops (keeps default drops)
LootHelper.registerBlockLoot(world, "Rock_Stone", (pos, blockType) -> {
 return Arrays.asList(
 new LootHelper.ItemDrop("Rock_Stone_Cobble", 2) // +2 bonus coal
 );
});
```

### registerBlockLootReplacement(world, blockTypeId, lootProvider)

Register custom loot that **completely replaces** default block drops.

**Parameters:**
- `World world` - The world to register the loot table in
- `String blockTypeId` - The block type ID (e.g., "Rock_Stone", "Ore_Gold_Stone")
- `BiFunction<Vector3i, String, List<ItemDrop>> lootProvider` - Function that returns list of custom drops

**How it works:**
- Removes the block immediately to prevent default drops
- Spawns only your custom items as physical entities
- Items have proper physics and can be picked up normally

**Example:**
```java
// Make stone drop 2-4 diamonds instead
LootHelper.registerBlockLootReplacement(world, "Rock_Stone", (pos, blockType) -> {
 Random random = new Random();
 int quantity = random.nextInt(3) + 2; // 2-4 diamonds
 return Arrays.asList(
 new LootHelper.ItemDrop("Rock_Gem_Diamond", quantity)
 );
});
```

### clearLootTables(world)

Clear all custom loot tables for a world.

**Parameters:**
- `World world` - The world to clear loot tables from

**Example:**
```java
LootHelper.clearLootTables(world);
```

## ItemDrop Class

Represents an item drop with quantity and optional velocity.

### Constructors

**ItemDrop(String itemId, int quantity)**
- Creates an item drop with default velocity (slight upward motion)

**ItemDrop(String itemId, int quantity, Vector3d velocity)**
- Creates an item drop with custom velocity

### Static Methods

**ItemDrop.withRandomVelocity(String itemId, int quantity)**
- Creates an item drop with random velocity spread for natural-looking drops

**Example:**
```java
// Random velocity for natural spread
LootHelper.ItemDrop.withRandomVelocity("Rock_Gem_Diamond", 1)

// Custom velocity
new LootHelper.ItemDrop("Rock_Gem_Diamond", 3, new Vector3d(0.1, 0.3, -0.1))

// Default velocity
new LootHelper.ItemDrop("Rock_Gem_Diamond", 1)
```

## Advanced Examples

### Random Drop Quantities

```java
LootHelper.registerBlockLootReplacement(world, "Ore_Gold_Stone", (pos, blockType) -> {
 Random random = new Random();
 int dropAmount = random.nextInt(3) + 1; // 1-3 gold
 
 // 10% chance for bonus diamond
 List<LootHelper.ItemDrop> drops = new ArrayList<>();
 drops.add(new LootHelper.ItemDrop("Rock_Gem_Diamond", dropAmount));
 
 if (random.nextDouble() < 0.1) {
 drops.add(new LootHelper.ItemDrop("Rock_Gem_Diamond", 1));
 }
 
 return drops;
});
```

### Depth-Based Drops

```java
LootHelper.registerBlockLootReplacement(world, "Rock_Stone", (pos, blockType) -> {
 List<LootHelper.ItemDrop> drops = new ArrayList<>();
 
 // Different drops based on depth
 if (pos.getY() < 10) {
 // Deep underground - rare items
 drops.add(new LootHelper.ItemDrop("Rock_Gem_Diamond", 2));
 } else if (pos.getY() < 30) {
 // Mid-depth - uncommon items
 drops.add(new LootHelper.ItemDrop("Rock_Gem_Ruby", 1));
 } else {
 // Surface - common items
 drops.add(new LootHelper.ItemDrop("Rock_Gem_Topaz", 1));
 }
 
 return drops;
});
```

### Multiple Items with Random Velocity

```java
LootHelper.registerBlockLootReplacement(world, "Rock_Gem_Diamond", (pos, blockType) -> {
 return Arrays.asList(
 LootHelper.ItemDrop.withRandomVelocity("Rock_Gem_Ruby", 1),
 LootHelper.ItemDrop.withRandomVelocity("Rock_Gem_Topaz", 1)
 );
});
```

### Conditional Bonus Drops

```java
// Add bonus drops alongside default
LootHelper.registerBlockLoot(world, "Rock_Stone", (pos, blockType) -> {
 Random random = new Random();
 List<LootHelper.ItemDrop> bonusDrops = new ArrayList<>();
 
 // 25% chance for extra coal
 if (random.nextDouble() < 0.25) {
 bonusDrops.add(new LootHelper.ItemDrop("Ingredient_Charcoal", 1));
 }
 
 // 5% chance for diamond
 if (random.nextDouble() < 0.05) {
 bonusDrops.add(new LootHelper.ItemDrop("Rock_Gem_Ruby", 1));
 }
 
 return bonusDrops;
});
```

## Complete Usage Example

```java
@Override
public void start() {
 EventHelper.onPlayerJoinWorld(this, world -> {
 // Custom ore drops
 LootHelper.registerBlockLootReplacement(world, "Rock_Stone", (pos, blockType) -> {
 Random random = new Random();
 int diamonds = random.nextInt(3) + 2; // 2-4 diamonds
 return Arrays.asList(
 LootHelper.ItemDrop.withRandomVelocity("Rock_Gem_Diamond", diamonds)
 );
 });
 
 // Stone drops iron swords (for testing)
 LootHelper.registerBlockLootReplacement(world, "Rock_Stone", (pos, blockType) -> {
 return Arrays.asList(
 new LootHelper.ItemDrop("Weapon_Sword_Iron", 1)
 );
 });
 
 // Bonus coal with default drops
 LootHelper.registerBlockLoot(world, "Rock_Stone", (pos, blockType) -> {
 return Arrays.asList(
 new LootHelper.ItemDrop("Resource_Coal", 2)
 );
 });
 
 // Depth-based stone drops
 LootHelper.registerBlockLootReplacement(world, "Rock_Stone", (pos, blockType) -> {
 if (pos.getY() < 20) {
 return Arrays.asList(
 new LootHelper.ItemDrop("Rock_Gem_Diamond", 1)
 );
 }
 return Arrays.asList(
 new LootHelper.ItemDrop("Ingredient_Charcoal", 1)
 );
 });
 });
}
```

## Technical Details

**Item Entity Spawning:**
- Uses `ItemComponent.generateItemDrop()` - the same method Hytale uses internally
- Spawns physical item entities with proper physics and collision
- Items can be picked up naturally by players
- Items despawn after the normal item lifetime
- Supports custom velocities for realistic drop patterns
- Uses `CommandBuffer.addEntity()` for proper ECS system integration

**ECS System:**
- Registers a `BreakBlockEvent` system per world
- Intercepts block breaks before default drops occur
- For replacements: Sets block to air (ID 0) to prevent default drops
- Spawns custom items using the entity component system
- Uses `CommandBuffer` to queue entity additions (required for ECS systems)
- Entities are added after the system finishes processing

**Performance:**
- Loot tables stored in concurrent maps for thread-safety
- One ECS system per world (registered on first loot table)
- Minimal overhead - only processes blocks with custom loot
- CommandBuffer ensures thread-safe entity spawning

## See Also

- [EcsEventHelper](EcsEventHelper) - For block break event handling
- [ItemHelper](ItemHelper) - For item creation and management
- [BlockHelper](BlockHelper) - For block manipulation
- [Home](Home) - Back to main page
