# InventoryHelper

InventoryHelper provides simplified inventory management utilities for working with player inventories in Hytale.

## Overview

InventoryHelper makes it easy to give/remove items, check inventory contents, and manage hotbar slots without dealing with the complex inventory container API directly.

## Methods

### Give Items

#### `giveItem(Entity entity, String itemId, int quantity)`

Give items to a player's inventory.

**Parameters:**
- `entity` - The entity (must be a LivingEntity)
- `itemId` - The item ID (e.g., "Ingredient_Bone_Fragment", "Rock_Stone")
- `quantity` - The number of items to give

**Returns:** `boolean` - true if items were successfully added

**Example:**
```java
// Give 10 Ingredient_Bone_Fragments to a player
boolean success = InventoryHelper.giveItem(player, "Ingredient_Bone_Fragment", 10);
if (success) {
 WorldHelper.log(world, "Gave 10 Ingredient_Bone_Fragments to player!");
}
```

**Use Cases:**
- Quest rewards
- Shop purchases
- Admin commands
- Custom loot systems

---

### Remove Items

#### `removeItem(Entity entity, String itemId, int quantity)`

Remove items from a player's inventory.

**Parameters:**
- `entity` - The entity (must be a LivingEntity)
- `itemId` - The item ID to remove
- `quantity` - The number of items to remove

**Returns:** `boolean` - true if items were successfully removed

**Example:**
```java
// Remove 5 Ingredient_Bone_Fragments from player
boolean removed = InventoryHelper.removeItem(player, "Ingredient_Bone_Fragments", 5);
if (removed) {
 WorldHelper.log(world, "Removed 5 Ingredient_Bone_Fragments from player");
} else {
 WorldHelper.log(world, "Player doesn't have enough Ingredient_Bone_Fragments");
}
```

**Use Cases:**
- Shop purchases (taking payment)
- Crafting requirements
- Quest item consumption
- Item durability systems

---

### Check Items

#### `hasItem(Entity entity, String itemId, int quantity)`

Check if a player has at least the specified quantity of an item.

**Parameters:**
- `entity` - The entity (must be a LivingEntity)
- `itemId` - The item ID to check
- `quantity` - The minimum quantity required

**Returns:** `boolean` - true if player has at least the specified quantity

**Example:**
```java
// Check if player has at least 10 Ingredient_Bone_Fragments
if (InventoryHelper.hasItem(player, "Ingredient_Bone_Fragments", 10)) {
 WorldHelper.log(world, "Player has enough Ingredient_Bone_Fragments!");
} else {
 PlayerHelper.sendMessage(player, "You need 10 Ingredient_Bone_Fragments!");
}
```

**Use Cases:**
- Quest requirements
- Shop purchase validation
- Crafting prerequisites
- Access control (key items)

---

### Count Items

#### `countItem(Entity entity, String itemId)`

Count the total quantity of a specific item in the player's inventory.

**Parameters:**
- `entity` - The entity (must be a LivingEntity)
- `itemId` - The item ID to count

**Returns:** `int` - The total quantity of the item (0 if none found)

**Example:**
```java
// Count how many Ingredient_Bone_Fragments the player has
int fragmentCount = InventoryHelper.countItem(player, "Ingredient_Bone_Fragments");
WorldHelper.log(world, "Player has " + fragmentCount + " Ingredient_Bone_Fragments");
```

**Use Cases:**
- Displaying inventory statistics
- Leaderboards (richest player)
- Quest progress tracking
- Economy systems

---

### Get All Items

#### `getAllItems(Entity entity)`

Get a list of all items in the player's inventory (hotbar, storage, backpack, armor, utility).

**Parameters:**
- `entity` - The entity (must be a LivingEntity)

**Returns:** `List<ItemStack>` - List of all items in inventory

**Example:**
```java
// Get all items and log them
List<ItemStack> items = InventoryHelper.getAllItems(player);
WorldHelper.log(world, "Player has " + items.size() + " item stacks");

for (ItemStack item : items) {
 WorldHelper.log(world, " - " + item.getItemId() + " x" + item.getQuantity());
}
```

**Use Cases:**
- Inventory analysis
- Backup/restore systems
- Death drop mechanics
- Inventory search features

---

### Hotbar Management

#### `getActiveHotbarItem(Entity entity)`

Get the item in the player's currently selected hotbar slot.

**Parameters:**
- `entity` - The entity (must be a LivingEntity)

**Returns:** `ItemStack` - The active hotbar item, or null if slot is empty

**Example:**
```java
// Check what item the player is holding
ItemStack activeItem = InventoryHelper.getActiveHotbarItem(player);
if (activeItem != null) {
 WorldHelper.log(world, "Player is holding: " + activeItem.getItemId());
} else {
 WorldHelper.log(world, "Player's hand is empty");
}
```

**Use Cases:**
- Tool requirement checks
- Item-specific abilities
- Context-sensitive actions
- Custom item interactions

---

#### `getActiveHotbarSlotIndex(Entity entity)`

Get the index of the player's currently selected hotbar slot.

**Parameters:**
- `entity` - The entity (must be a LivingEntity)

**Returns:** `byte` - The active hotbar slot index (0-8), or -1 if error

**Example:**
```java
// Get the active slot index
byte slotIndex = InventoryHelper.getActiveHotbarSlotIndex(player);
WorldHelper.log(world, "Active hotbar slot: " + slotIndex);
```

---

#### `setHotbarSlot(Entity entity, int slotIndex, ItemStack itemStack)`

Set a specific hotbar slot to contain an item.

**Parameters:**
- `entity` - The entity (must be a LivingEntity)
- `slotIndex` - The hotbar slot index (0-8)
- `itemStack` - The ItemStack to place in the slot (or null to clear)

**Returns:** `boolean` - true if slot was successfully set

**Example:**
```java
// Give player a Ingredient_Bone_Fragment in hotbar slot 0
ItemStack fragment = new ItemStack("Ingredient_Bone_Fragment", 1);
boolean success = InventoryHelper.setHotbarSlot(player, 0, fragment);
if (success) {
 WorldHelper.log(world, "Placed Ingredient_Bone_Fragment in hotbar slot 0");
}
```

**Use Cases:**
- Custom starting equipment
- Ability hotbar systems
- Quick-access item placement
- Tutorial systems

---

### Inventory Status

#### `clearInventory(Entity entity)`

Clear all items from the player's inventory (hotbar, storage, backpack).

**Parameters:**
- `entity` - The entity (must be a LivingEntity)

**Returns:** `boolean` - true if inventory was successfully cleared

**Example:**
```java
// Clear player's inventory
boolean cleared = InventoryHelper.clearInventory(player);
if (cleared) {
 PlayerHelper.sendMessage(player, "Your inventory has been cleared!");
}
```

**Use Cases:**
- Admin commands
- Minigame resets
- Death penalties
- Fresh start systems

---

#### `isInventoryFull(Entity entity)`

Check if the player's inventory is full (cannot accept any more items).

**Parameters:**
- `entity` - The entity (must be a LivingEntity)

**Returns:** `boolean` - true if inventory is full

**Example:**
```java
// Check if inventory is full before giving items
if (InventoryHelper.isInventoryFull(player)) {
 PlayerHelper.sendMessage(player, "Your inventory is full!");
} else {
 InventoryHelper.giveItem(player, "Ingredient_Bone_Fragment", 10);
}
```

**Use Cases:**
- Preventing item loss
- Inventory warnings
- Shop purchase validation
- Loot distribution

---

#### `getEmptySlotCount(Entity entity)`

Count the number of empty slots in the player's inventory.

**Parameters:**
- `entity` - The entity (must be a LivingEntity)

**Returns:** `int` - The number of empty slots

**Example:**
```java
// Check how much space the player has
int emptySlots = InventoryHelper.getEmptySlotCount(player);
WorldHelper.log(world, "Player has " + emptySlots + " empty slots");

if (emptySlots < 5) {
 PlayerHelper.sendMessage(player, "Your inventory is almost full!");
}
```

**Use Cases:**
- Inventory space warnings
- Loot distribution calculations
- Storage management
- UI indicators

---

## Important Notes

### Entity Type Requirement
All InventoryHelper methods require the entity to be a `LivingEntity` (which includes Players). Methods will return false/null/0 if called on non-living entities.

### Item IDs
Item IDs must match the exact item identifiers used in Hytale (e.g., "Ingredient_Bone_Fragment", "Rock_Stone", "Torch"). Invalid item IDs will cause the operation to fail.

### Thread Safety
All inventory operations are thread-safe and handle exceptions gracefully. Failed operations will log warnings but won't crash your plugin.

### Inventory Sections
The helper works with the following inventory sections:
- **Hotbar** - 9 slots (quick access bar)
- **Storage** - 36 slots (main inventory)
- **Backpack** - Additional storage slots
- **Armor** - Equipment slots
- **Utility** - Utility item slots

---

## Complete Example

Here's a complete example showing common inventory operations:

```java
// Quest system example
EventHelper.onPlayerChat(plugin, (username, message) -> {
 if (message.equals("!quest")) {
 Entity player = EntityHelper.getPlayerByName(world, username);
 
 if (player == null) return;
 
 // Check if player has quest items
 if (InventoryHelper.hasItem(player, "QuestItem", 10)) {
 // Remove quest items
 InventoryHelper.removeItem(player, "QuestItem", 10);
 
 // Give reward
 InventoryHelper.giveItem(player, "Ingredient_Bone_Fragment", 5);
 InventoryHelper.giveItem(player, "Gold", 100);
 
 PlayerHelper.sendMessage(player, "Quest complete! Rewards given!");
 } else {
 int currentCount = InventoryHelper.countItem(player, "QuestItem");
 PlayerHelper.sendMessage(player, "You need 10 Quest Items. You have: " + currentCount);
 }
 }
});
```

---

## See Also

- [PlayerHelper](PlayerHelper) - Player-specific utilities
- [EntityHelper](EntityHelper) - Entity management utilities
- [EventHelper](EventHelper) - Event handling utilities
