# BlockHelper

Block manipulation and world editing utilities.

## Overview

**What it does:**
- Get and set blocks at specific positions
- Fill or replace blocks in rectangular regions
- Find blocks of specific types within a radius
- Count blocks in regions
- Check if positions contain air blocks
- Name-based block operations (e.g., "Rock_Stone" instead of numeric IDs)

**When to use:**
- Building/terrain modification mods
- Custom world generation
- Area protection or region management
- Block-based game mechanics
- Mining or construction features

BlockHelper now supports **name-based block operations** similar to Minecraft modding, making it much easier to work with blocks without memorizing numeric IDs.

## Available Methods

### Name-Based Block Setting (Recommended)

```java
// Set a block by name - much easier than using numeric IDs!
BlockHelper.setBlockByName(world, x, y, z, "Rock_Stone");
BlockHelper.setBlockByName(world, position, "Soil_Grass");

// Get block ID from name (uses game's native asset system)
int stoneId = BlockHelper.getBlockId("Rock_Stone");

// Fill a region with a named block
Vector3d corner1 = new Vector3d(100, 64, 100);
Vector3d corner2 = new Vector3d(110, 64, 110);
int blocksSet = BlockHelper.fillRegionByName(world, corner1, corner2, "Rock_Stone");

// Replace blocks by name
int replaced = BlockHelper.replaceBlocksInRegionByName(
 world, corner1, corner2, 
 "Soil_Dirt", // old block
 "Soil_Grass" // new block
);

// Find blocks by name
List<Vector3i> diamonds = BlockHelper.findNearbyBlocksByName(world, playerPos, 50, "Ore_Copper_Stone");
```

**Note:** Block names are resolved dynamically using Hytale's native `BlockType` asset system, ensuring compatibility with game updates and modded blocks. See the [Complete Block List](BlockList) for all available block names and IDs.

### getBlockName(blockId) / getBlockName(world, position) / getBlockName(world, x, y, z)

Get the human-readable block name from a block ID or position.

```java
// Get block name from numeric ID
int blockId = 684;
String blockName = BlockHelper.getBlockName(blockId);
WorldHelper.log(world, "Block: " + blockName); // e.g., "Rock_Stone"

// Get block name at a position
Vector3d pos = new Vector3d(100, 64, 100);
String name = BlockHelper.getBlockName(world, pos);
WorldHelper.log(world, "Block at position: " + name);

// Get block name at specific coordinates
String blockName = BlockHelper.getBlockName(world, 100, 64, 100);
```

**Note:** Block names are the internal asset IDs (e.g., "Rock_Stone", "Soil_Grass"). This is similar to how `EntityHelper.getEntityType()` returns entity role names.

### getBlock(world, position) / getBlock(world, x, y, z)

Get the numeric block ID at a specific position.

```java
// Get block at a position
Vector3d pos = new Vector3d(100, 64, 100);
int blockId = BlockHelper.getBlock(world, pos);
WorldHelper.log(world, "Block ID: " + blockId);

// Get block at specific coordinates
int block = BlockHelper.getBlock(world, 100, 64, 100);

// Combine with getBlockName for readable output
int blockId = BlockHelper.getBlock(world, pos);
String blockName = BlockHelper.getBlockName(blockId);
WorldHelper.log(world, "Block: " + blockId + " (" + blockName + ")");

// Check if a position is air
if (BlockHelper.isAir(world, pos)) {
 WorldHelper.log(world, "Position is empty!");
}
```

### setBlockByName(world, position, blockName) / setBlockByName(world, x, y, z, blockName)

Set a block by name at a specific position (recommended method).

```java
// Set a block by name - easy and readable!
boolean success = BlockHelper.setBlockByName(world, 100, 64, 100, "Rock_Stone");
if (success) {
 WorldHelper.log(world, "Block placed successfully!");
}

// Using Vector3d position
Vector3d pos = new Vector3d(100, 64, 100);
BlockHelper.setBlockByName(world, pos, "Soil_Grass");

// Example: Build a cobblestone path
for (int x = 100; x <= 110; x++) {
 BlockHelper.setBlockByName(world, x, 64, 100, "Rock_Stone_Cobble");
}
```

### setBlock(world, position, blockId) / setBlock(world, x, y, z, blockId)

Set a block using numeric ID (use `setBlockByName` instead when possible).

```java
// Get block ID from name first
int stoneId = BlockHelper.getBlockId("Rock_Stone");

// Set using numeric ID
boolean success = BlockHelper.setBlock(world, 100, 64, 100, stoneId);

// Example: Copy a block from one location to another
int sourceBlock = BlockHelper.getBlock(world, 100, 64, 100);
BlockHelper.setBlock(world, 200, 64, 200, sourceBlock);
```

**TIP:** Use name-based methods (`setBlockByName`, `fillRegionByName`, etc.) for better code readability. Numeric IDs are still useful for copying blocks or advanced operations.

### fillRegionByName(world, pos1, pos2, blockName)

Fill a rectangular region with a named block type (recommended).

```java
// Create a stone platform
Vector3d corner1 = new Vector3d(100, 64, 100);
Vector3d corner2 = new Vector3d(110, 64, 110);
int blocksSet = BlockHelper.fillRegionByName(world, corner1, corner2, "Rock_Stone");
WorldHelper.log(world, "Created platform with " + blocksSet + " blocks");

// Build a grass field
BlockHelper.fillRegionByName(world, corner1, corner2, "Soil_Grass");
```

### fillRegion(world, pos1, pos2, blockId)

Fill a rectangular region with a block ID (use `fillRegionByName` when possible).

```java
// Using numeric ID
int stoneId = BlockHelper.getBlockId("Rock_Stone");
int blocksSet = BlockHelper.fillRegion(world, corner1, corner2, stoneId);
```

### replaceBlocksInRegionByName(world, pos1, pos2, oldBlockName, newBlockName)

Replace all blocks of one type with another in a region by name (recommended).

```java
// Replace all dirt with grass in an area
Vector3d corner1 = new Vector3d(90, 60, 90);
Vector3d corner2 = new Vector3d(110, 70, 110);
int replaced = BlockHelper.replaceBlocksInRegionByName(
 world, corner1, corner2, 
 "Soil_Dirt", // old block
 "Soil_Grass" // new block
);
WorldHelper.log(world, "Replaced " + replaced + " dirt blocks with grass");

// Convert stone to cobblestone
BlockHelper.replaceBlocksInRegionByName(world, corner1, corner2, "Rock_Stone", "Rock_Stone_Cobble");
```

### replaceBlocksInRegion(world, pos1, pos2, oldBlockId, newBlockId)

Replace blocks using numeric IDs (use `replaceBlocksInRegionByName` when possible).

```java
// Using numeric IDs
int dirtId = BlockHelper.getBlockId("Soil_Dirt");
int grassId = BlockHelper.getBlockId("Soil_Grass");
int replaced = BlockHelper.replaceBlocksInRegion(world, corner1, corner2, dirtId, grassId);
```

### findNearbyBlocksByName(world, center, radius, blockName)

Find all positions of a specific block type by name within a radius (recommended).

```java
// Find all copper ore within 50 blocks
Vector3d playerPos = EntityHelper.getPosition(player);
List<Vector3i> copperOres = BlockHelper.findNearbyBlocksByName(world, playerPos, 50, "Ore_Copper_Stone");
WorldHelper.log(world, "Found " + copperOres.size() + " copper ore blocks nearby");

// Find all diamond ore
List<Vector3i> diamonds = BlockHelper.findNearbyBlocksByName(world, playerPos, 50, "Ore_Diamond");
for (Vector3i orePos : diamonds) {
 WorldHelper.log(world, "Diamond at: " + orePos.getX() + ", " + orePos.getY() + ", " + orePos.getZ());
}
```

### findNearbyBlocks(world, center, radius, blockId)

Find blocks using numeric ID (use `findNearbyBlocksByName` when possible).

```java
// Using numeric ID
int diamondId = BlockHelper.getBlockId("Ore_Diamond");
List<Vector3i> diamonds = BlockHelper.findNearbyBlocks(world, playerPos, 50, diamondId);
```

### getBlocksInRegion(world, pos1, pos2)

Get all block positions and IDs within a rectangular region.

```java
// Scan a region and catalog all blocks
Vector3d corner1 = new Vector3d(100, 64, 100);
Vector3d corner2 = new Vector3d(105, 69, 105);
List<BlockHelper.BlockPosition> blocks = BlockHelper.getBlocksInRegion(world, corner1, corner2);

// Count different block types
Map<Integer, Integer> blockCounts = new HashMap<>();
for (BlockHelper.BlockPosition block : blocks) {
 blockCounts.put(block.blockId, blockCounts.getOrDefault(block.blockId, 0) + 1);
}

// Log the results
for (Map.Entry<Integer, Integer> entry : blockCounts.entrySet()) {
 WorldHelper.log(world, "Block ID " + entry.getKey() + ": " + entry.getValue() + " blocks");
}
```

### countBlocksInRegion(world, pos1, pos2, blockId)

Count how many blocks of a specific type exist in a region.

```java
// Count stone blocks in a mining area
Vector3d corner1 = new Vector3d(100, 50, 100);
Vector3d corner2 = new Vector3d(120, 64, 120);
int stoneCount = BlockHelper.countBlocksInRegion(world, corner1, corner2, 1);
WorldHelper.log(world, "Mining area contains " + stoneCount + " stone blocks");

// Check if area is mostly cleared (count air blocks)
int airCount = BlockHelper.countBlocksInRegion(world, corner1, corner2, 0);
int totalBlocks = (21 * 15 * 21); // volume of region
double clearPercentage = (airCount * 100.0) / totalBlocks;
WorldHelper.log(world, "Area is " + clearPercentage + "% cleared");
```

## Practical Examples

### Example 1: Create a simple house foundation

```java
Vector3d corner1 = new Vector3d(100, 64, 100);
Vector3d corner2 = new Vector3d(110, 64, 110);

// Create stone floor using name-based method
BlockHelper.fillRegionByName(world, corner1, corner2, "Rock_Stone");

// Create cobblestone walls (4 separate fills)
BlockHelper.fillRegionByName(world, new Vector3d(100, 65, 100), new Vector3d(100, 70, 110), "Rock_Stone_Cobble"); // West wall
BlockHelper.fillRegionByName(world, new Vector3d(110, 65, 100), new Vector3d(110, 70, 110), "Rock_Stone_Cobble"); // East wall
BlockHelper.fillRegionByName(world, new Vector3d(100, 65, 100), new Vector3d(110, 70, 100), "Rock_Stone_Cobble"); // North wall
BlockHelper.fillRegionByName(world, new Vector3d(100, 65, 110), new Vector3d(110, 70, 110), "Rock_Stone_Cobble"); // South wall
```

### Example 2: Terrain transformation

```java
// Convert a dirt area to grass
Vector3d corner1 = new Vector3d(90, 60, 90);
Vector3d corner2 = new Vector3d(110, 70, 110);
int replaced = BlockHelper.replaceBlocksInRegionByName(world, corner1, corner2, "Soil_Dirt", "Soil_Grass");
WorldHelper.log(world, "Transformed " + replaced + " dirt blocks to grass");

// Create a stone path through grass
for (int x = 95; x <= 105; x++) {
 BlockHelper.setBlockByName(world, x, 64, 100, "Rock_Stone_Cobble");
}
```

## Important Notes

### Chunk Requirements

BlockHelper uses Hytale's **32x32 block chunks** and can only access chunks present in the ChunkStore. Chunks that aren't in the store will return 0 (air) for reads and fail for writes.

**Checking if a chunk is loaded:**
```java
ChunkStore chunkStore = world.getChunkStore();
// Hytale chunks are 32x32 blocks (use ChunkUtil for correct indexing)
long chunkPos = ChunkUtil.indexChunkFromBlock(x, z);
LongSet chunkIndexes = chunkStore.getChunkIndexes();
boolean isLoaded = chunkIndexes.contains(chunkPos);
WorldHelper.log(world, "Chunk loaded: " + isLoaded);
```

Most chunks where players are active will be in the ChunkStore. If you encounter issues, verify the chunk is loaded using the code above.

### How Block Setting Works (Technical Details)

When you call any `BlockHelper.setBlock()` or `setBlockByName()` method, the following happens automatically:

1. **Block Data Update**: The block is set in the `BlockChunk` via `blockChunk.setBlock(localX, y, localZ, blockId, rotation, filler)`
2. **Section Invalidation**: The `BlockChunk` automatically invalidates the chunk section's cached packet, marking it as needing a rebuild
3. **Client Notification**: A `ServerSetBlock` packet is sent to all players who have that chunk loaded via `WorldNotificationHandler.sendPacketIfChunkLoaded()`

**Why This Matters:**
Without sending the `ServerSetBlock` packet, blocks would be set on the server but clients wouldn't see the change until they reload the chunk. The packet ensures:
- **Immediate visibility**: Players see the block change instantly
- **Collision updates**: The client updates collision for the new block
- **Proper rendering**: The block mesh is rebuilt on the client

**All BlockHelper methods handle this automatically** - you don't need to manually send any packets.

## See Also

- [WorldHelper](WorldHelper) - For world operations
- [EntityHelper](EntityHelper) - For entity operations
- [Home](Home) - Back to main page
