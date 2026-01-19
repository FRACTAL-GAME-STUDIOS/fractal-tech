# BlockStateHelper

A utility class for working with block states in Hytale. Block states are additional data attached to blocks like chests, signs, and other interactive blocks. This helper abstracts the deprecated `BlockState` API to make it easier to work with.

## Table of Contents
- [Overview](#overview)
- [State Checking](#state-checking)
- [State Access](#state-access)
- [State Modification](#state-modification)
- [State Information](#state-information)

---

## Overview

**What are Block States?**

Block states store additional data for blocks that need more than just their block type. Examples include:
- **Chests** - Store inventory contents
- **Signs** - Store text
- **Furnaces** - Store smelting progress
- **Doors** - Store open/closed state

**Note:** The underlying `BlockState` class is deprecated in Hytale's API, but it's still used for these special blocks. This helper provides a clean interface to work with them.

---

## State Checking

### `hasState(World world, int x, int y, int z)`
Check if a block at the given position has state data.

**Parameters:**
- `world` - The world
- `x`, `y`, `z` - Block coordinates

**Returns:** `boolean` - true if the block has state data

**Example:**
```java
if (BlockStateHelper.hasState(world, x, y, z)) {
 LOGGER.log("This block has additional state data");
}
```

### `isStateType(BlockState state, Class<?> stateClass)`
Check if a block state is of a specific type.

**Parameters:**
- `state` - The block state to check
- `stateClass` - The class to check against (e.g., ItemContainerState.class)

**Returns:** `boolean` - true if the state is of the specified type

**Example:**
```java
BlockState state = BlockStateHelper.getState(world, x, y, z);
if (BlockStateHelper.isStateType(state, ItemContainerState.class)) {
 LOGGER.log("This is a container block (chest, etc.)");
}
```

---

## State Access

### `getState(World world, int x, int y, int z)`
Get the block state at the given position.

**Parameters:**
- `world` - The world
- `x`, `y`, `z` - Block coordinates

**Returns:** `BlockState` or `null` if no state exists

**Example:**
```java
BlockState state = BlockStateHelper.getState(world, x, y, z);
if (state != null) {
 LOGGER.log("Found state data for block");
}
```

### `ensureState(World world, int x, int y, int z)`
Get or create a block state at the given position. If the block supports state data but doesn't have it yet, this will create it.

**Parameters:**
- `world` - The world
- `x`, `y`, `z` - Block coordinates

**Returns:** `BlockState` or `null` if the block doesn't support state data

**Example:**
```java
// This will create state data if the block is a chest but doesn't have state yet
BlockState state = BlockStateHelper.ensureState(world, x, y, z);
if (state instanceof ItemContainerState chestState) {
 // Now we can work with the chest
 ItemContainer container = chestState.getItemContainer();
}
```

**Use Case:** Use this when you've just placed a block and need to access its state immediately.

---

## State Modification

### `setState(World world, int x, int y, int z, BlockState state)`
Set or update the block state at the given position.

**Parameters:**
- `world` - The world
- `x`, `y`, `z` - Block coordinates
- `state` - The block state to set

**Returns:** `boolean` - true if successful

**Example:**
```java
BlockState state = BlockStateHelper.getState(world, x, y, z);
// Modify the state...
boolean success = BlockStateHelper.setState(world, x, y, z, state);
```

### `markNeedsSave(BlockState state)`
Mark a block state as needing to be saved. Call this after modifying state data to ensure it persists.

**Parameters:**
- `state` - The block state to mark for saving

**Example:**
```java
BlockState state = BlockStateHelper.ensureState(world, x, y, z);
if (state instanceof ItemContainerState chestState) {
 ItemContainer container = chestState.getItemContainer();
 ItemHelper.fillContainer(container, "Item_Diamond", 5);
 
 // IMPORTANT: Mark for saving so items persist
 BlockStateHelper.markNeedsSave(chestState);
}
```

**Important:** Always call this after modifying block state data, or your changes may not persist!

---

## State Information

### `getBlockPosition(BlockState state)`
Get the world position of a block state.

**Parameters:**
- `state` - The block state

**Returns:** `Vector3i` - The block's position, or null if unavailable

**Example:**
```java
BlockState state = BlockStateHelper.getState(world, x, y, z);
Vector3i pos = BlockStateHelper.getBlockPosition(state);
if (pos != null) {
 LOGGER.log("Block is at: " + pos.x + ", " + pos.y + ", " + pos.z);
}
```

### `getBlockType(BlockState state)`
Get the block type of a block state.

**Parameters:**
- `state` - The block state

**Returns:** `BlockType` or `null` if unavailable

**Example:**
```java
BlockState state = BlockStateHelper.getState(world, x, y, z);
BlockType type = BlockStateHelper.getBlockType(state);
if (type != null) {
 LOGGER.log("Block type: " + type.getId());
}
```

---

## Complete Example: Chest Management

```java
// Place a chest in the world
int x = 100, y = 64, z = 100;
BlockHelper.setBlockByName(world, x, y, z, "Furniture_Desert_Chest_Small");

// Wait a tick for the block to initialize
WorldHelper.waitTicks(world, 2, () -> {
 // Get or create the chest's state
 BlockState state = BlockStateHelper.ensureState(world, x, y, z);
 
 if (state instanceof ItemContainerState chestState) {
 // Get the container
 ItemContainer container = chestState.getItemContainer();
 
 // Add items using ItemHelper
 ItemHelper.fillContainerRandom(container,
 "Furniture_Crude_Torch", 2,
 "Ingredient_Bone_Fragment", 10,
 "Item_Diamond", 5
 );
 
 // CRITICAL: Save the state
 BlockStateHelper.markNeedsSave(chestState);
 
 LOGGER.log("Chest created and filled with loot!");
 } else {
 LOGGER.log("Block doesn't support item containers");
 }
});
```

---

## Working with Different State Types

### Item Containers (Chests, Furnaces, etc.)

```java
BlockState state = BlockStateHelper.ensureState(world, x, y, z);
if (state instanceof ItemContainerState containerState) {
 ItemContainer container = containerState.getItemContainer();
 
 // Add items
 ItemHelper.addToContainer(container, "Item_Diamond", 5);
 
 // Save
 BlockStateHelper.markNeedsSave(containerState);
}
```

### Signs

```java
BlockState state = BlockStateHelper.ensureState(world, x, y, z);
if (state instanceof SignState signState) {
 // Set sign text (if API is available)
 // signState.setText("Hello World");
 
 // Save
 BlockStateHelper.markNeedsSave(signState);
}
```

---

## Common Patterns

### Pattern 1: Place Block and Add State

```java
// 1. Place the block
BlockHelper.setBlockByName(world, x, y, z, "Furniture_Desert_Chest_Small");

// 2. Wait for initialization
WorldHelper.waitTicks(world, 2, () -> {
 // 3. Get/create state
 BlockState state = BlockStateHelper.ensureState(world, x, y, z);
 
 // 4. Modify state
 if (state instanceof ItemContainerState chestState) {
 ItemContainer container = chestState.getItemContainer();
 ItemHelper.fillContainer(container, "Item_Diamond", 5);
 
 // 5. Save
 BlockStateHelper.markNeedsSave(chestState);
 }
});
```

### Pattern 2: Check and Modify Existing State

```java
// Check if block has state
if (BlockStateHelper.hasState(world, x, y, z)) {
 BlockState state = BlockStateHelper.getState(world, x, y, z);
 
 if (state instanceof ItemContainerState chestState) {
 ItemContainer container = chestState.getItemContainer();
 
 // Check contents
 int diamonds = ItemHelper.countItemInContainer(container, "Item_Diamond");
 LOGGER.log("Chest has " + diamonds + " diamonds");
 
 // Add more
 ItemHelper.addToContainer(container, "Item_Diamond", 10);
 
 // Save
 BlockStateHelper.markNeedsSave(chestState);
 }
}
```

### Pattern 3: Safe State Access

```java
BlockState state = BlockStateHelper.getState(world, x, y, z);
if (state == null) {
 LOGGER.log("No state data at this position");
 return;
}

if (!BlockStateHelper.isStateType(state, ItemContainerState.class)) {
 LOGGER.log("Block is not a container");
 return;
}

ItemContainerState chestState = (ItemContainerState) state;
// Work with chest...
```

---

## Tips

1. **Always wait after placing blocks** - Use `WorldHelper.waitTicks()` to ensure block state is initialized
2. **Always save after modifications** - Call `markNeedsSave()` or changes won't persist
3. **Use `ensureState` for new blocks** - It creates state if needed
4. **Use `getState` for existing blocks** - Faster if you know state exists
5. **Check state types safely** - Use `instanceof` or `isStateType()` before casting
6. **Combine with ItemHelper** - For easy container manipulation

---

## Deprecation Notice

The underlying `BlockState` class is marked as deprecated in Hytale's API. This helper provides a clean interface that:
- Abstracts the deprecated API
- Provides null-safety
- Simplifies common operations
- Makes code more maintainable

When Hytale updates their block state system, this helper can be updated without changing your code.

---

## Related Helpers
- [ItemHelper](ItemHelper) - For working with items and containers
- [BlockHelper](BlockHelper) - For placing and manipulating blocks
- [WorldHelper](WorldHelper) - For world operations and tick scheduling
