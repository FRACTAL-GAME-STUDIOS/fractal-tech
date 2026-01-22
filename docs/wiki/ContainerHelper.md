# ContainerHelper

Utilities for tracking item container changes in Hytale. Provides easy-to-use methods for detecting when items are added, removed, or changed in containers like chests, furnaces, and other block-based item storage.

## Overview

**What it does:**
- Track changes to specific containers (chests, furnaces, etc.)
- Detect when items are added, removed, or moved in containers
- Register callbacks for container events at specific block positions
- Manage multiple container listeners across worlds

**When to use:**
- Chest protection systems
- Container logging and auditing
- Custom storage mechanics
- Item tracking and anti-theft systems
- Automated inventory management

**Important Notes:**
- You must register each container individually by position
- Containers must exist at the position (have an ItemContainerState)
- This is not a global event - you track specific containers
- Listeners persist until unregistered or world is cleared

## Transaction Cancellation

Container transactions can now be cancelled, similar to EquipmentHelper. When you cancel a transaction, ContainerHelper will automatically revert **BOTH** the container and the player's inventory to their state before the transaction occurred.

**How to cancel:**
```java
ContainerHelper.onContainerChange(world, position, (transaction) -> {
 // Check if someone is trying to take a specific item
 if ("Weapon_Sword_Diamond".equals(transaction.getItemId()) && transaction.isRemoved()) {
 // Cancel the transaction - item stays in chest
 transaction.setCancelled(true);
 WorldHelper.log(world, "Cannot remove diamond sword from this chest!");
 }
});
```

**Player-specific restrictions:**
```java
// Block specific player from using containers
ContainerHelper.onContainerChange(world, position, (transaction) -> {
 // Get all players and check if restricted player is interacting
 for (Entity entity : EntityHelper.getEntities(world)) {
 if (EntityHelper.isPlayer(entity)) {
 String playerName = EntityHelper.getName(entity);
 if ("RestrictedPlayer".equals(playerName)) {
 transaction.setCancelled(true);
 PlayerHelper.sendMessage(entity, "You are not allowed to use this container!");
 break;
 }
 }
 }
});
```

**Use cases:**
- Prevent removing specific items from containers
- Block adding certain items to containers
- Permission-based container access (player-specific restrictions)
- Protected storage systems
- Quest item restrictions
- VIP-only container access
- Admin-only storage

**How it works:**
- Automatically reverts the transaction using reflection
- Restores **container slots** to their original state
- Restores **player inventory slots** to their original state (via `otherContainer`)
- Handles both `ItemStackSlotTransaction` (normal click) and `ItemStackTransaction` (shift-click)
- Handles both `MOVE_FROM_SELF` (removing) and `MOVE_TO_SELF` (adding)
- Handles `ListTransaction` (take all button) by recursively reverting all contained transactions
- **Prevents item duplication** when dragging items outside the UI by automatically removing dropped item entities
- Preserves item data (durability, metadata, etc.)
- Uses `ThreadLocal` re-entry guard to prevent infinite loops
- Works with all transaction types

## Available Methods

### Auto-Registration System

#### enableAutoTracking(world, callback)

Enable automatic container tracking for a world. This will automatically register listeners on ALL containers as they are placed by players, and unregister them when destroyed.

**How it works:**
- Uses `EcsEventHelper.onBlockPlace()` to detect when containers are placed
- Automatically registers the callback on the new container after a 2-tick delay
- Uses `EcsEventHelper.onBlockBreak()` to detect when containers are destroyed
- Automatically unregisters the container when broken
- Tracks common container types: chests, furnaces, crafting benches
- **Automatically parses transactions** - No manual string parsing needed!

**Parameters:**
- `World world` - The world to enable auto-tracking for
- `Consumer<ContainerTransaction> callback` - Callback that receives parsed transaction data

**Example:**
```java
// Automatically track ALL containers in the world
ContainerHelper.enableAutoTracking(world, (transaction) -> {
 WorldHelper.log(world, "Action: " + transaction.getAction());
 WorldHelper.log(world, "Item: " + transaction.getItemId());
 WorldHelper.log(world, "Quantity: " + transaction.getQuantity());
});

// Now whenever a player places a chest, it's automatically tracked!
// When they break it, it's automatically unregistered!
```

#### enableAutoTrackingSimple(world, callback)

Enable automatic container tracking with a simplified callback that only receives the raw transaction string.

**Parameters:**
- `World world` - The world to enable auto-tracking for
- `Consumer<String> callback` - Callback that receives raw transaction string

**Example:**
```java
ContainerHelper.enableAutoTrackingSimple(world, (transactionString) -> {
 WorldHelper.log(world, "Raw transaction: " + transactionString);
});
```

#### disableAutoTracking(world)

Disable automatic container tracking for a world.

**Parameters:**
- `World world` - The world to disable auto-tracking for

**Example:**
```java
ContainerHelper.disableAutoTracking(world);
```

#### isAutoTrackingEnabled(world)

Check if auto-tracking is enabled for a world.

**Parameters:**
- `World world` - The world to check

**Returns:** `boolean` - true if auto-tracking is enabled

**Example:**
```java
if (ContainerHelper.isAutoTrackingEnabled(world)) {
 WorldHelper.log(world, "Auto-tracking is active!");
}
```

#### addContainerType(blockTypeId)

Add a custom container block type to the auto-tracking list. Useful for modded containers.

**Parameters:**
- `String blockTypeId` - The block type ID to track (e.g., "Furniture_Custom_Chest")

**Example:**
```java
// Track custom modded containers
ContainerHelper.addContainerType("MyMod_Custom_Chest");
ContainerHelper.addContainerType("MyMod_Magic_Storage");
```

### Manual Registration Methods

#### onContainerChange(world, position, callback)

Register a callback for when items change in a container at a specific position. **Automatically parses transactions!**

**Parameters:**
- `World world` - The world containing the container
- `Vector3i position` - The block position of the container
- `Consumer<ContainerTransaction> callback` - Callback that receives parsed transaction data

**Returns:** `boolean` - true if successfully registered, false if no container exists at position

**Example:**
```java
Vector3i chestPos = new Vector3i(100, 64, 100);
ContainerHelper.onContainerChange(world, chestPos, (transaction) -> {
 WorldHelper.log(world, "Action: " + transaction.getAction());
 WorldHelper.log(world, "Item: " + transaction.getItemId());
 WorldHelper.log(world, "Quantity: " + transaction.getQuantity());
 
 // Use helper methods
 if (transaction.isAdded()) {
 WorldHelper.log(world, "Item was added!");
 }
});
```

### onContainerChangeSimple(world, position, callback)

Register a simplified callback that only receives the raw transaction string (no parsing).

**Parameters:**
- `World world` - The world containing the container
- `Vector3i position` - The block position of the container
- `Consumer<String> callback` - Callback that receives raw transaction string

**Returns:** `boolean` - true if successfully registered, false if no container exists at position

**Example:**
```java
Vector3i chestPos = new Vector3i(100, 64, 100);
ContainerHelper.onContainerChangeSimple(world, chestPos, (transactionString) -> {
 WorldHelper.log(world, "Raw transaction: " + transactionString);
});
```

### onContainerItemAdd(world, position, callback)

Register a callback for when items are **added** to a container. Only fires for ADD actions.

**Parameters:**
- `World world` - The world containing the container
- `Vector3i position` - The block position of the container
- `Consumer<ContainerTransaction> callback` - Callback that receives parsed transaction data

**Returns:** `boolean` - true if successfully registered, false if no container exists at position

**Example:**
```java
Vector3i chestPos = new Vector3i(100, 64, 100);
ContainerHelper.onContainerItemAdd(world, chestPos, (transaction) -> {
 WorldHelper.log(world, "Item added: " + transaction.getQuantity() + "x " + transaction.getItemId());
});
```

### onContainerItemRemove(world, position, callback)

Register a callback for when items are **removed** from a container. Only fires for REMOVE actions.

**Parameters:**
- `World world` - The world containing the container
- `Vector3i position` - The block position of the container
- `Consumer<ContainerTransaction> callback` - Callback that receives parsed transaction data

**Returns:** `boolean` - true if successfully registered, false if no container exists at position

**Example:**
```java
Vector3i chestPos = new Vector3i(100, 64, 100);
ContainerHelper.onContainerItemRemove(world, chestPos, (transaction) -> {
 WorldHelper.log(world, "Item removed: " + transaction.getQuantity() + "x " + transaction.getItemId());
});
```

### unregisterContainer(world, position)

Stop tracking a container at a specific position.

**Parameters:**
- `World world` - The world containing the container
- `Vector3i position` - The block position to stop tracking

**Example:**
```java
Vector3i chestPos = new Vector3i(100, 64, 100);
ContainerHelper.unregisterContainer(world, chestPos);
```

### clearWorld(world)

Clear all tracked containers for a world.

**Parameters:**
- `World world` - The world to clear container tracking for

**Example:**
```java
ContainerHelper.clearWorld(world);
```

### getTrackedContainerCount(world)

Get the number of tracked containers in a world.

**Parameters:**
- `World world` - The world to check

**Returns:** `int` - Number of tracked containers

**Example:**
```java
int count = ContainerHelper.getTrackedContainerCount(world);
WorldHelper.log(world, "Tracking " + count + " containers");
```

## Usage Examples

### Interaction-Based Registration (Recommended)

The recommended approach is to register containers when players interact with them. This works for both newly placed containers and existing containers from previous worlds.

```java
@Override
public void start() {
 EventHelper.onPlayerJoinWorld(this, world -> {
 // Register block interaction event to detect container usage
 EcsEventHelper.onBlockInteract(world, (position, blockTypeId) -> {
 // Check if this is a container block
 if (ContainerHelper.isContainerType(blockTypeId)) {
 // Register the container when player interacts with it
 ContainerHelper.onContainerChange(world, position, (transaction) -> {
 // ContainerTransaction automatically parses the event!
 WorldHelper.log(world, "Action: " + transaction.getAction());
 
 if (transaction.getItemId() != null) {
 WorldHelper.log(world, "Item: " + transaction.getItemId());
 WorldHelper.log(world, "Quantity: " + transaction.getQuantity());
 }
 
 // Check action type with helper methods
 if (transaction.isAdded()) {
 WorldHelper.log(world, "Item added to container!");
 } else if (transaction.isRemoved()) {
 WorldHelper.log(world, "Item removed from container!");
 }
 });
 WorldHelper.log(world, "Registered container at " + position);
 }
 });
 
 WorldHelper.log(world, "Container tracking enabled!");
 WorldHelper.log(world, "Containers will be registered when players interact with them");
 });
}
```

**Why this approach?**
- Works with existing containers from previous worlds or generated by the game
- Works with newly placed containers
- Only registers containers that are actually used
- Handles block state variations (open/closed states)
- Minimal performance impact
- **Automatic parsing** - No need to manually parse transaction strings!
- **Correct shift-click detection** - Properly detects ADDED vs REMOVED for shift-click transfers
- **Manual cleanup** - You need to unregister containers when they're broken (see example below)

**Cleaning up when containers are broken:**
```java
// Listen for block breaks and unregister containers
EcsEventHelper.onBlockBreak(world, (position, blockTypeId) -> {
 if (ContainerHelper.isContainerType(blockTypeId)) {
 ContainerHelper.unregisterContainer(world, position);
 WorldHelper.log(world, "Unregistered container at " + position);
 }
});
```

### Auto-Tracking All Containers (Alternative)

This approach automatically registers containers when placed and **automatically unregisters them when broken**.

```java
@Override
public void start() {
 EventHelper.onPlayerJoinWorld(this, world -> {
 // Enable auto-tracking for ALL containers in the world
 ContainerHelper.enableAutoTracking(world, (transaction) -> {
 // ContainerTransaction provides parsed data automatically
 if (transaction.isRemoved()) {
 WorldHelper.broadcast(world, "Item removed: " + transaction.getQuantity() + "x " + transaction.getItemId());
 } else if (transaction.isAdded()) {
 WorldHelper.log(world, "Item added: " + transaction.getQuantity() + "x " + transaction.getItemId());
 }
 
 // Access raw transaction if needed
 WorldHelper.log(world, "Full details: " + transaction.getRawTransaction());
 });
 
 WorldHelper.log(world, "Auto-tracking enabled! All placed containers will be monitored.");
 WorldHelper.log(world, "Currently tracking: " + ContainerHelper.getTrackedContainerCount(world) + " containers");
 });
}
```

**Benefits of auto-tracking:**
- **Automatic cleanup** - Containers are automatically unregistered when broken
- No need to manually listen for block breaks
- Tracks all containers placed by players
- Minimal setup required

### Auto-Tracking with Custom Container Types

```java
// Add custom modded container types
ContainerHelper.addContainerType("MyMod_Magic_Chest");
ContainerHelper.addContainerType("MyMod_Ender_Storage");

// Enable auto-tracking with parsed transactions
ContainerHelper.enableAutoTracking(world, (transaction) -> {
 WorldHelper.log(world, "Container activity: " + transaction.getAction() + " - " + transaction.getItemId());
});
```

### Basic Container Tracking (Manual)

```java
// Place a chest
BlockHelper.setBlockByName(world, 100, 64, 100, "Furniture_Desert_Chest_Small");

// Wait for block state to be created
WorldHelper.waitTicks(world, 2, () -> {
 Vector3i chestPos = new Vector3i(100, 64, 100);
 
 // Track all changes to this chest
 boolean registered = ContainerHelper.onContainerChange(world, chestPos, (container, event) -> {
 WorldHelper.log(world, "Chest at " + chestPos + " changed!");
 WorldHelper.log(world, "Transaction: " + event.transaction());
 });
 
 if (registered) {
 WorldHelper.log(world, "Successfully tracking chest!");
 } else {
 WorldHelper.log(world, "Failed to register - no container at position");
 }
});
```

### Chest Protection System

```java
// Track when items are removed from protected chests
Map<Vector3i, String> protectedChests = new ConcurrentHashMap<>();
protectedChests.put(new Vector3i(100, 64, 100), "PlayerName");

Vector3i chestPos = new Vector3i(100, 64, 100);
ContainerHelper.onContainerItemRemove(world, chestPos, (transaction) -> {
 String owner = protectedChests.get(chestPos);
 if (owner != null) {
 WorldHelper.broadcast(world, "Protected chest of " + owner + " was accessed!");
 WorldHelper.log(world, "Transaction: " + transaction);
 }
});
```

### Item Logging System

```java
// Log all container activity
Vector3i chestPos = new Vector3i(100, 64, 100);
ContainerHelper.onContainerChange(world, chestPos, (container, event) -> {
 String transaction = event.transaction().toString();
 
 if (transaction.contains("action=ADD")) {
 WorldHelper.log(world, "[CHEST LOG] Item added to chest at " + chestPos);
 } else if (transaction.contains("action=REMOVE")) {
 WorldHelper.log(world, "[CHEST LOG] Item removed from chest at " + chestPos);
 } else if (transaction.contains("action=MOVE")) {
 WorldHelper.log(world, "[CHEST LOG] Item moved in chest at " + chestPos);
 }
 
 WorldHelper.log(world, " Details: " + transaction);
});
```

### Multiple Container Tracking

```java
// Track multiple chests
List<Vector3i> chestPositions = Arrays.asList(
 new Vector3i(100, 64, 100),
 new Vector3i(110, 64, 100),
 new Vector3i(120, 64, 100)
);

for (Vector3i pos : chestPositions) {
 ContainerHelper.onContainerChangeSimple(world, pos, (transaction) -> {
 WorldHelper.log(world, "Chest at " + pos + " changed: " + transaction);
 });
}

WorldHelper.log(world, "Tracking " + ContainerHelper.getTrackedContainerCount(world) + " chests");
```

### Automated Inventory Management

```java
// Auto-sort items when chest is modified
Vector3i chestPos = new Vector3i(100, 64, 100);
ContainerHelper.onContainerChange(world, chestPos, (container, event) -> {
 // Wait a tick for transaction to complete
 WorldHelper.waitTicks(world, 1, () -> {
 // Get block state
 BlockState state = BlockStateHelper.getState(world, chestPos.getX(), chestPos.getY(), chestPos.getZ());
 if (state instanceof ItemContainerState containerState) {
 ItemContainer chest = containerState.getItemContainer();
 
 // Count items
 int diamondCount = ItemHelper.countItemInContainer(chest, "Rock_Gem_Diamond");
 int topazCount = ItemHelper.countItemInContainer(chest, "Rock_Gem_Topaz");
 
 WorldHelper.log(world, "Chest contents: " + diamondCount + " diamonds, " + topazCount + " topaz");
 
 // Could implement auto-sorting logic here
 }
 });
});
```

### Container Event Filtering

```java
// Only track specific types of changes
Vector3i chestPos = new Vector3i(100, 64, 100);
ContainerHelper.onContainerChange(world, chestPos, (container, event) -> {
 String transaction = event.transaction().toString();
 
 // Only log if diamonds are involved
 if (transaction.contains("Rock_Gem_Diamond")) {
 WorldHelper.broadcast(world, "Diamond transaction detected!");
 WorldHelper.log(world, transaction);
 }
 
 // Only log if quantity is large
 if (transaction.contains("quantity=") && transaction.matches(".*quantity=(\\d{2,}).*")) {
 WorldHelper.log(world, "Large quantity transaction: " + transaction);
 }
});
```

## Complete Usage Example

```java
@Override
public void start() {
 EventHelper.onPlayerJoinWorld(this, world -> {
 // Create a protected chest system
 Vector3i chestPos = new Vector3i(100, 64, 100);
 
 // Place chest
 BlockHelper.setBlockByName(world, chestPos.getX(), chestPos.getY(), chestPos.getZ(), 
 "Furniture_Desert_Chest_Small");
 
 // Wait for block state to initialize
 WorldHelper.waitTicks(world, 2, () -> {
 // Fill with initial items
 BlockState state = BlockStateHelper.ensureState(world, chestPos.getX(), chestPos.getY(), chestPos.getZ());
 if (state instanceof ItemContainerState containerState) {
 ItemContainer container = containerState.getItemContainer();
 ItemHelper.fillContainer(container, "Rock_Gem_Diamond", 10);
 BlockStateHelper.markNeedsSave(containerState);
 }
 
 // Track changes
 ContainerHelper.onContainerChange(world, chestPos, (container, event) -> {
 String transaction = event.transaction().toString();
 
 if (transaction.contains("action=REMOVE")) {
 WorldHelper.broadcast(world, "Items removed from protected chest!");
 } else if (transaction.contains("action=ADD")) {
 WorldHelper.broadcast(world, "Items added to chest");
 }
 
 WorldHelper.log(world, "Transaction details: " + transaction);
 });
 
 WorldHelper.log(world, "Protected chest system initialized!");
 WorldHelper.log(world, "Tracking " + ContainerHelper.getTrackedContainerCount(world) + " containers");
 });
 });
}
```

## Technical Details

**Container Registration:**
- Uses `ItemContainer.registerChangeEvent()` to listen for changes
- Each container must be registered individually by block position
- Listeners are stored in concurrent maps for thread-safety
- Supports multiple listeners per world

**Event Detection:**
- Fires on any container transaction (ADD, REMOVE, MOVE, REPLACE, CLEAR)
- Provides full transaction details including item IDs, quantities, and slots
- Transaction strings can be parsed for detailed information
- Events fire immediately when container is modified

**Block State Integration:**
- Uses `BlockStateHelper` to get container from block position
- Requires block to have an `ItemContainerState` (chests, furnaces, etc.)
- Returns false if no container exists at position
- Works with any block that implements ItemContainerState

**Performance:**
- Minimal overhead - only processes registered containers
- Thread-safe concurrent storage for multi-player support
- Listeners persist until explicitly unregistered
- No global event polling - uses native container events

**Limitations:**
- Must register each container individually (not automatic/global)
- Container must exist before registration
- Listener registration should happen after block state is created
- Use `WorldHelper.waitTicks()` after placing containers to ensure state exists

## See Also

- [BlockStateHelper](BlockStateHelper) - For working with block states
- [ItemHelper](ItemHelper) - For container and item operations
- [EventHelper](EventHelper) - For global inventory events
- [Home](Home) - Back to main page
