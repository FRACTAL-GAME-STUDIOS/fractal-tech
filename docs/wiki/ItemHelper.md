# ItemHelper

A comprehensive utility class for working with items and item containers in Hytale. Provides simplified methods for creating items, managing containers, and common item operations.

## Table of Contents
- [Item Creation](#item-creation)
- [Container Operations](#container-operations)
- [Random Slot Placement](#random-slot-placement)
- [Container Queries](#container-queries)
- [Item Utilities](#item-utilities)

---

## Item Creation

### `createStack(String itemId, int quantity)`
Create an item stack with the specified ID and quantity.

**Parameters:**
- `itemId` - The item ID (e.g., "Furniture_Crude_Torch")
- `quantity` - The quantity

**Returns:** `ItemStack` or `null` if creation failed

**Example:**
```java
ItemStack torch = ItemHelper.createStack("Furniture_Crude_Torch", 5);
ItemStack bones = ItemHelper.createStack("Ingredient_Bone_Fragment", 10);
```

### `createStack(String itemId)`
Create an item stack with quantity 1.

**Parameters:**
- `itemId` - The item ID

**Returns:** `ItemStack` or `null` if creation failed

**Example:**
```java
ItemStack sword = ItemHelper.createStack("Weapon_Sword_Iron");
```

---

## Container Operations

### `addToContainer(ItemContainer container, String itemId, int quantity)`
Add an item to a container in the first available slot.

**Parameters:**
- `container` - The container to add to
- `itemId` - The item ID
- `quantity` - The quantity

**Returns:** `boolean` - true if the item was fully added, false if there was a remainder

**Example:**
```java
ItemContainer chest = chestState.getItemContainer();
boolean success = ItemHelper.addToContainer(chest, "Furniture_Crude_Torch", 1);
```

### `addToContainerSlot(ItemContainer container, int slot, String itemId, int quantity)`
Add an item to a specific slot in a container.

**Parameters:**
- `container` - The container
- `slot` - The slot index (0-based)
- `itemId` - The item ID
- `quantity` - The quantity

**Returns:** `boolean` - true if successful

**Example:**
```java
// Add torch to slot 0
ItemHelper.addToContainerSlot(chest, 0, "Furniture_Crude_Torch", 1);
// Add bones to slot 1
ItemHelper.addToContainerSlot(chest, 1, "Ingredient_Bone_Fragment", 10);
```

### `addToContainerSlot(ItemContainer container, int slot, ItemStack stack)`
Add an existing item stack to a specific slot.

**Parameters:**
- `container` - The container
- `slot` - The slot index
- `stack` - The item stack to add

**Returns:** `boolean` - true if successful

**Example:**
```java
ItemStack customItem = ItemHelper.createStack("Rock_Gem_Diamond", 5);
ItemHelper.addToContainerSlot(chest, 2, customItem);
```

### `fillContainer(ItemContainer container, Object... items)`
Fill a container with multiple items in sequential order (slots 0, 1, 2, etc.).

**Parameters:**
- `container` - The container to fill
- `items` - Pairs of itemId and quantity (itemId1, qty1, itemId2, qty2, ...)

**Returns:** `int` - Number of items successfully added

**Example:**
```java
int added = ItemHelper.fillContainer(chest,
 "Furniture_Crude_Torch", 1,
 "Ingredient_Bone_Fragment", 10,
 "Rock_Gem_Diamond", 5
);
// Items will be in slots 0, 1, 2
```

---

## Random Slot Placement

### `addToContainerRandom(ItemContainer container, String itemId, int quantity)`
Add an item to a random empty slot in a container.

**Parameters:**
- `container` - The container to add to
- `itemId` - The item ID
- `quantity` - The quantity

**Returns:** `boolean` - true if the item was added to a random slot, false if no empty slots available

**Example:**
```java
// Will place torch in a random empty slot
boolean success = ItemHelper.addToContainerRandom(chest, "Furniture_Crude_Torch", 1);
```

### `fillContainerRandom(ItemContainer container, Object... items)`
Fill a container with multiple items in random empty slots. Unlike `fillContainer`, this places items randomly instead of sequentially.

**Parameters:**
- `container` - The container to fill
- `items` - Pairs of itemId and quantity (itemId1, qty1, itemId2, qty2, ...)

**Returns:** `int` - Number of items successfully added

**Example:**
```java
// Items will be placed in random slots each time
int added = ItemHelper.fillContainerRandom(chest,
 "Furniture_Crude_Torch", 1,
 "Ingredient_Bone_Fragment", 10
);
```

**Use Case:** Great for creating natural-looking loot chests where items aren't in predictable positions.

---

## Container Queries

### `getItemsFromContainer(ItemContainer container)`
Get all items from a container as a list.

**Parameters:**
- `container` - The container

**Returns:** `List<ItemStack>` - List of all non-empty item stacks

**Example:**
```java
List<ItemStack> items = ItemHelper.getItemsFromContainer(chest);
for (ItemStack item : items) {
 String id = ItemHelper.getItemId(item);
 int qty = ItemHelper.getQuantity(item);
 LOGGER.log("Found: " + id + " x" + qty);
}
```

### `getItemFromSlot(ItemContainer container, int slot)`
Get an item from a specific slot in a container.

**Parameters:**
- `container` - The container
- `slot` - The slot index

**Returns:** `ItemStack` or `null` if slot is empty

**Example:**
```java
ItemStack item = ItemHelper.getItemFromSlot(chest, 0);
if (item != null) {
 LOGGER.log("Slot 0 contains: " + ItemHelper.getItemId(item));
}
```

### `countItemInContainer(ItemContainer container, String itemId)`
Count how many of a specific item are in a container.

**Parameters:**
- `container` - The container
- `itemId` - The item ID to count

**Returns:** `int` - Total quantity of the item

**Example:**
```java
int torchCount = ItemHelper.countItemInContainer(chest, "Furniture_Crude_Torch");
LOGGER.log("Chest contains " + torchCount + " torches");
```

### `hasSpace(ItemContainer container, ItemStack stack)`
Check if a container has space for an item stack.

**Parameters:**
- `container` - The container
- `stack` - The item stack to check

**Returns:** `boolean` - true if the container can fit the item

**Example:**
```java
ItemStack newItem = ItemHelper.createStack("Rock_Gem_Diamond", 10);
if (ItemHelper.hasSpace(chest, newItem)) {
 ItemHelper.addToContainer(chest, "Rock_Gem_Diamond", 10);
}
```

### `isEmpty(ItemContainer container)`
Check if a container is empty.

**Parameters:**
- `container` - The container

**Returns:** `boolean` - true if the container has no items

**Example:**
```java
if (ItemHelper.isEmpty(chest)) {
 LOGGER.log("Chest is empty!");
}
```

---

## Container Modification

### `removeFromContainer(ItemContainer container, String itemId, int quantity)`
Remove a specific quantity of an item from a container.

**Parameters:**
- `container` - The container
- `itemId` - The item ID to remove
- `quantity` - The quantity to remove

**Returns:** `boolean` - true if the full quantity was removed

**Example:**
```java
boolean removed = ItemHelper.removeFromContainer(chest, "Furniture_Crude_Torch", 1);
```

### `clearContainer(ItemContainer container)`
Clear all items from a container.

**Parameters:**
- `container` - The container to clear

**Returns:** `boolean` - true if successful

**Example:**
```java
ItemHelper.clearContainer(chest);
```

---

## Item Utilities

### `areStackable(ItemStack stack1, ItemStack stack2)`
Check if two item stacks are stackable (same item, can combine).

**Parameters:**
- `stack1` - First item stack
- `stack2` - Second item stack

**Returns:** `boolean` - true if they can stack together

**Example:**
```java
ItemStack torch1 = ItemHelper.createStack("Furniture_Crude_Torch", 1);
ItemStack torch2 = ItemHelper.createStack("Furniture_Crude_Torch", 5);
if (ItemHelper.areStackable(torch1, torch2)) {
 LOGGER.log("These items can be combined!");
}
```

### `getItemId(ItemStack stack)`
Get the item ID from an item stack.

**Parameters:**
- `stack` - The item stack

**Returns:** `String` - The item ID, or null if stack is null/empty

**Example:**
```java
String id = ItemHelper.getItemId(stack);
```

### `getQuantity(ItemStack stack)`
Get the quantity from an item stack.

**Parameters:**
- `stack` - The item stack

**Returns:** `int` - The quantity, or 0 if stack is null/empty

**Example:**
```java
int qty = ItemHelper.getQuantity(stack);
```

### `isEmptyStack(ItemStack stack)`
Check if an item stack is empty or null.

**Parameters:**
- `stack` - The item stack to check

**Returns:** `boolean` - true if the stack is empty or null

**Example:**
```java
if (ItemHelper.isEmptyStack(stack)) {
 LOGGER.log("Slot is empty");
}
```

---

## Complete Example: Loot Chest System

```java
// Place a chest in the world
BlockHelper.setBlockByName(world, x, y, z, "Furniture_Desert_Chest_Small");

// Get the chest's block state
BlockState state = BlockStateHelper.ensureState(world, x, y, z);
if (state instanceof ItemContainerState chestState) {
 ItemContainer container = chestState.getItemContainer();
 
 // Fill chest with random loot in random slots
 int itemsAdded = ItemHelper.fillContainerRandom(container,
 "Furniture_Crude_Torch", 2,
 "Ingredient_Bone_Fragment", 10,
 "Weapon_Sword_Iron", 1
 );
 
 // Save the chest
 BlockStateHelper.markNeedsSave(chestState);
 
 // Log what was added
 LOGGER.log("Added " + itemsAdded + " item types to chest");
 
 // Count specific items
 int diamonds = ItemHelper.countItemInContainer(container, "Rock_Gem_Diamond");
 LOGGER.log("Chest contains " + diamonds + " diamonds");
}
```

---

## Tips

1. **Use `fillContainerRandom` for loot chests** - Creates more natural-looking item placement
2. **Use `fillContainer` for organized storage** - Items in predictable sequential slots
3. **Always check return values** - Methods return success/failure status
4. **Save block states after modification** - Use `BlockStateHelper.markNeedsSave()` for persistence
5. **Use `countItemInContainer` for quest requirements** - Check if player has collected enough items

---

## Item Entity Methods

Methods for working with dropped item entities in the world.

### `getItemEntities(World world)`
Get all dropped item entities in the world.

**Parameters:**
- `world` - The world to search in

**Returns:** `List<ItemEntity>` - List of all dropped items

**Example:**
```java
List<ItemHelper.ItemEntity> items = ItemHelper.getItemEntities(world);
for (ItemHelper.ItemEntity item : items) {
 WorldHelper.log(world, "Found: " + item.getQuantity() + "x " + item.getItemId() + 
 " at " + item.getPosition());
}
```

### `getItemEntitiesByItemId(World world, String itemId)`
Get all dropped item entities of a specific item type.

**Parameters:**
- `world` - The world to search in
- `itemId` - The item ID to filter by (e.g., "Food_Pork_Raw")

**Returns:** `List<ItemEntity>` - List of matching dropped items

**Example:**
```java
// Find all dropped pork
List<ItemHelper.ItemEntity> pork = ItemHelper.getItemEntitiesByItemId(world, "Food_Pork_Raw");
WorldHelper.log(world, "Found " + pork.size() + " dropped pork items");
```

### `getItemEntitiesInRadius(World world, Vector3d center, double radius)`
Get all dropped item entities within a radius of a position.

**Parameters:**
- `world` - The world to search in
- `center` - Center position to search from
- `radius` - Search radius in blocks

**Returns:** `List<ItemEntity>` - List of items within the radius

**Example:**
```java
// Find items within 10 blocks of player
Vector3d playerPos = EntityHelper.getPosition(player);
List<ItemHelper.ItemEntity> nearbyItems = ItemHelper.getItemEntitiesInRadius(world, playerPos, 10.0);
WorldHelper.log(world, "Found " + nearbyItems.size() + " items nearby");
```

### `removeItemEntity(World world, ItemEntity itemEntity)`
Remove a specific item entity from the world.

**Parameters:**
- `world` - The world containing the item entity
- `itemEntity` - The ItemEntity to remove

**Returns:** `boolean` - true if successfully removed

**Example:**
```java
List<ItemHelper.ItemEntity> items = ItemHelper.getItemEntities(world);
for (ItemHelper.ItemEntity item : items) {
 if ("Food_Pork_Raw".equals(item.getItemId())) {
 ItemHelper.removeItemEntity(world, item);
 WorldHelper.log(world, "Removed pork at " + item.getPosition());
 }
}
```

### `removeAllItemEntities(World world)`
Remove all dropped item entities from the world.

**Parameters:**
- `world` - The world to clear items from

**Returns:** `int` - Number of items removed

**Example:**
```java
int removed = ItemHelper.removeAllItemEntities(world);
WorldHelper.broadcast(world, "Cleared " + removed + " dropped items from the world!");
```

### `removeItemEntitiesByItemId(World world, String itemId)`
Remove all dropped item entities of a specific type.

**Parameters:**
- `world` - The world to clear items from
- `itemId` - The item ID to remove (e.g., "Food_Pork_Raw")

**Returns:** `int` - Number of items removed

**Example:**
```java
// Remove all dropped diamonds
int removed = ItemHelper.removeItemEntitiesByItemId(world, "Rock_Gem_Diamond");
WorldHelper.log(world, "Removed " + removed + " dropped diamonds");
```

### `countItemEntities(World world)`
Count the total number of dropped item entities in the world.

**Parameters:**
- `world` - The world to count items in

**Returns:** `int` - Number of dropped items

**Example:**
```java
int count = ItemHelper.countItemEntities(world);
WorldHelper.log(world, "There are " + count + " dropped items in the world");
```

### `countItemEntitiesByItemId(World world, String itemId)`
Count dropped item entities of a specific type.

**Parameters:**
- `world` - The world to count items in
- `itemId` - The item ID to count

**Returns:** `int` - Number of matching dropped items

**Example:**
```java
int torchCount = ItemHelper.countItemEntitiesByItemId(world, "Furniture_Crude_Torch");
WorldHelper.log(world, "There are " + torchCount + " dropped torches");
```

### `teleportItemEntity(World world, ItemEntity itemEntity, Vector3d position)`
Teleport a specific item entity to a new position.

**Parameters:**
- `world` - The world containing the item entity
- `itemEntity` - The ItemEntity to teleport
- `position` - The new position

**Returns:** `boolean` - true if successfully teleported

**Example:**
```java
List<ItemHelper.ItemEntity> items = ItemHelper.getItemEntities(world);
Vector3d destination = new Vector3d(100, 64, 100);

for (ItemHelper.ItemEntity item : items) {
 if ("Rock_Gem_Diamond".equals(item.getItemId())) {
 ItemHelper.teleportItemEntity(world, item, destination);
 WorldHelper.log(world, "Teleported diamond to " + destination);
 }
}
```

### `teleportItemEntity(World world, ItemEntity itemEntity, double x, double y, double z)`
Teleport a specific item entity to coordinates.

**Parameters:**
- `world` - The world containing the item entity
- `itemEntity` - The ItemEntity to teleport
- `x`, `y`, `z` - Destination coordinates

**Returns:** `boolean` - true if successfully teleported

**Example:**
```java
ItemHelper.ItemEntity item = items.get(0);
ItemHelper.teleportItemEntity(world, item, 100, 64, 100);
```

### `teleportItemEntitiesByItemId(World world, String itemId, Vector3d position)`
Teleport all dropped items of a specific type to a position.

**Parameters:**
- `world` - The world to search in
- `itemId` - The item ID to teleport (e.g., "Food_Pork_Raw")
- `position` - The destination position

**Returns:** `int` - Number of items teleported

**Example:**
```java
// Teleport all dropped torches to spawn
Vector3d spawn = new Vector3d(0, 64, 0);
int teleported = ItemHelper.teleportItemEntitiesByItemId(world, "Furniture_Crude_Torch", spawn);
WorldHelper.broadcast(world, "Teleported " + teleported + " torches to spawn!");
```

### `teleportItemEntitiesInRadius(World world, Vector3d center, double radius, Vector3d destination)`
Teleport all dropped items within a radius to a position.

**Parameters:**
- `world` - The world to search in
- `center` - Center position to search from
- `radius` - Search radius in blocks
- `destination` - The destination position

**Returns:** `int` - Number of items teleported

**Example:**
```java
// Teleport items near player to a chest location
Vector3d playerPos = EntityHelper.getPosition(player);
Vector3d chestPos = new Vector3d(100, 64, 100);
int teleported = ItemHelper.teleportItemEntitiesInRadius(world, playerPos, 10.0, chestPos);
WorldHelper.log(world, "Teleported " + teleported + " nearby items to chest");
```

### `teleportAllItemEntities(World world, Vector3d position)`
Teleport all dropped items in the world to a position.

**Parameters:**
- `world` - The world to search in
- `position` - The destination position

**Returns:** `int` - Number of items teleported

**Example:**
```java
// Teleport all items to spawn
Vector3d spawn = new Vector3d(0, 64, 0);
int teleported = ItemHelper.teleportAllItemEntities(world, spawn);
WorldHelper.broadcast(world, "Teleported all " + teleported + " items to spawn!");
```

### ItemEntity Class
Represents a dropped item entity with the following properties:
- `getRef()` - Get the entity reference (for removal/teleportation)
- `getItemId()` - Get the item ID (e.g., "Food_Pork_Raw")
- `getQuantity()` - Get the item quantity
- `getPosition()` - Get the world position (Vector3d)

**Technical Implementation:**
- Uses Hytale's ECS (Entity Component System) for efficient queries
- Queries `EntityStore` for entities with `ItemComponent`
- Only iterates through actual item entities (very efficient)
- Uses `CommandBuffer.removeEntity()` for proper removal
- Modifies `TransformComponent.setPosition()` for teleportation

---

## Complete Example: Item Cleanup System

```java
// Clean up old dropped items periodically
WorldHelper.scheduleRepeating(world, 300, () -> { // Every 5 minutes
 // Get all dropped items
 List<ItemHelper.ItemEntity> items = ItemHelper.getItemEntities(world);
 
 // Log what we found
 WorldHelper.log(world, "Found " + items.size() + " dropped items");
 
 // Count by type
 int diamonds = ItemHelper.countItemEntitiesByItemId(world, "Rock_Gem_Diamond");
 int torches = ItemHelper.countItemEntitiesByItemId(world, "Furniture_Crude_Torch");
 WorldHelper.log(world, " Diamonds: " + diamonds + ", Torches: " + torches);
 
 // Remove all junk items
 int removed = ItemHelper.removeItemEntitiesByItemId(world, "Ingredient_Bone_Fragment");
 if (removed > 0) {
 WorldHelper.broadcast(world, "Cleaned up " + removed + " bone fragments");
 }
 
 // Remove items far from players
 for (Entity player : world.getPlayers()) {
 Vector3d playerPos = EntityHelper.getPosition(player);
 List<ItemHelper.ItemEntity> farItems = new ArrayList<>();
 
 for (ItemHelper.ItemEntity item : items) {
 double distance = EntityHelper.getDistance(playerPos, item.getPosition());
 if (distance > 100.0) { // More than 100 blocks away
 farItems.add(item);
 }
 }
 
 for (ItemHelper.ItemEntity item : farItems) {
 ItemHelper.removeItemEntity(world, item);
 }
 }
});
```

---

## Related Helpers
- [BlockStateHelper](BlockStateHelper.md) - For working with block states and containers
- [InventoryHelper](InventoryHelper.md) - For player inventory operations
- [BlockHelper](BlockHelper.md) - For placing and manipulating blocks
- [EntityHelper](EntityHelper.md) - For entity operations and queries
