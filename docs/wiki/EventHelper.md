# EventHelper

Simplified event registration for common game events. Handles the boilerplate of registering global event listeners and parsing transaction data.

## Overview

**What it does:**
- Registers event listeners without manual EventRegistry calls
- Parses `LivingEntityInventoryChangeEvent` transactions to extract item details
- Provides callbacks for item drops, pickups, crafting, player joins, chat, and disconnects

**When to use:**
- Detecting when players drop, pick up, or craft items
- Tracking player join/disconnect events
- Monitoring chat messages
- Avoiding manual transaction string parsing

## Available Methods

### onItemDrop(plugin, callback)

Detects when a player drops an item from their inventory.

**Callback Parameters (Basic):**
- `String itemId` - The item ID that was dropped
- `int quantity` - The actual quantity dropped (not the full stack size)

**Callback Parameters (With Player Entity):**
- `String itemId` - The item ID that was dropped
- `int quantity` - The actual quantity dropped
- `Entity playerEntity` - The player who dropped the item

**Example (Basic):**
```java
EventHelper.onItemDrop(this, (itemId, quantity) -> {
 getLogger().at(Level.INFO).log("Item dropped: " + itemId + " x" + quantity);
});
```

**Example (With Player Entity):**
```java
EventHelper.onItemDrop(this, (itemId, quantity, playerEntity) -> {
 String playerName = EntityHelper.getName(playerEntity);
 getLogger().at(Level.INFO).log(playerName + " dropped: " + quantity + "x " + itemId);
 // Example: Apply stat changes, check permissions, track player-specific stats
 StatsHelper.addStat(playerEntity, "Stamina", -1.0f);
});
```

### onItemPickup(plugin, callback)

Detects when a player picks up an item.

**Callback Parameters (Basic):**
- `String itemId` - The item ID that was picked up
- `int quantity` - The quantity picked up

**Callback Parameters (With Player Entity):**
- `String itemId` - The item ID that was picked up
- `int quantity` - The quantity picked up
- `Entity playerEntity` - The player who picked up the item

**Example (Basic):**
```java
EventHelper.onItemPickup(this, (itemId, quantity) -> {
 getLogger().at(Level.INFO).log("Player picked up: " + itemId + " x" + quantity);
});
```

**Example (With Player Entity):**
```java
EventHelper.onItemPickup(this, (itemId, quantity, playerEntity) -> {
 String playerName = EntityHelper.getName(playerEntity);
 getLogger().at(Level.INFO).log(playerName + " picked up: " + quantity + "x " + itemId);
 // Example: Reward player, grant XP, check inventory permissions
 if (itemId.contains("Ingredient_")) {
 StatsHelper.addStat(playerEntity, "Mana", 5.0f);
 }
});
```

### onPlayerJoinWorld(plugin, callback)

Fires when a player joins the world.

**Callback Parameters:**
- `World world` - The world the player joined

**Note:** This event fires when a player is being added to the world, but the player may not be in `world.getPlayers()` yet. Use this to get the world reference, then check for existing players or wait for them to be fully added.

**Example:**
```java
EventHelper.onPlayerJoinWorld(this, world -> {
 getLogger().at(Level.INFO).log("Player joined world: " + world.getName());
 // Example: Send welcome message, initialize player data
});
```

### onPlayerJoinWorldWithUUID(plugin, callback)

Fires when a player joins the world with access to their UUID and username.

**Callback Parameters:**
- `World world` - The world the player joined
- `UUID uuid` - The player's unique identifier
- `String username` - The player's username

**How it works:**
- Extracts the player UUID from the event's Holder component immediately
- Waits 50 ticks (2.5 seconds) for the player to be fully added to the world
- Retrieves the username using EntityHelper methods
- Calls the callback with all three parameters

**Example:**
```java
EventHelper.onPlayerJoinWorldWithUUID(this, (world, uuid, username) -> {
 getLogger().at(Level.INFO).log("Player " + username + " (" + uuid + ") joined " + world.getName());
 // Example: Load player data by UUID, send personalized welcome message
 loadPlayerData(uuid);
 sendWelcomeMessage(username);
});
```

### onPlayerChat(plugin, callback)

Detects when a player sends a chat message.

**Callback Parameters:**
- `String username` - The player's username
- `String message` - The chat message content

**Example:**
```java
EventHelper.onPlayerChat(this, (username, message) -> {
 getLogger().at(Level.INFO).log(username + " said: " + message);
 // Example: Chat filtering, command detection, logging
});
```

### onPlayerDisconnect(plugin, callback)

Fires when a player disconnects from the server.

**Callback Parameters:**
- `UUID uuid` - The player's unique identifier
- `String username` - The username of the player who disconnected

**Example:**
```java
EventHelper.onPlayerDisconnect(this, (uuid, username) -> {
 getLogger().at(Level.INFO).log(username + " (" + uuid + ") left the server");
 // Example: Save player data by UUID, broadcast leave message
 savePlayerData(uuid);
 broadcastLeaveMessage(username);
});
```

### onPlayerDisconnectByName(plugin, callback)

Fires when a player disconnects from the server (legacy method, username only).

**Callback Parameters:**
- `String username` - The username of the player who disconnected

**Example:**
```java
EventHelper.onPlayerDisconnectByName(this, (username) -> {
 getLogger().at(Level.INFO).log(username + " left the server");
 // Example: Simple leave message without UUID tracking
});
```

### onCraftRecipe(plugin, callback)

Detects when a player crafts an item.

**Callback Parameters (Basic):**
- `String outputItemId` - The crafted item ID (e.g., "Bench_Campfire", "Weapon_Arrow_Crude")
- `int quantity` - The quantity crafted

**Callback Parameters (With Player Entity):**
- `String outputItemId` - The crafted item ID
- `int quantity` - The quantity crafted
- `Entity playerEntity` - The player who crafted the item

**How it works:**
- Detects crafting through `LivingEntityInventoryChangeEvent` transaction patterns
- Distinguishes crafting from regular item pickups by analyzing transaction structure
- Crafting creates two transactions: materials removed, then crafted item added
- Only the crafted item (output) is reported, not the materials consumed

**Example (Basic):**
```java
EventHelper.onCraftRecipe(this, (outputItemId, quantity) -> {
 getLogger().at(Level.INFO).log("Player crafted: " + quantity + "x " + outputItemId);
});
```

**Example (With Player Entity):**
```java
EventHelper.onCraftRecipe(this, (outputItemId, quantity, playerEntity) -> {
 String playerName = EntityHelper.getName(playerEntity);
 getLogger().at(Level.INFO).log(playerName + " crafted: " + quantity + "x " + outputItemId);
 
 // Example: Grant XP for crafting
 if (outputItemId.contains("Weapon_")) {
 StatsHelper.addStat(playerEntity, "Mana", 10.0f);
 }
 
 // Example: Check player permissions before allowing craft
 if ("Bench_Campfire".equals(outputItemId)) {
 // Give bonus items to specific players
 InventoryHelper.giveItem(playerEntity, "Furniture_Crude_Torch", 4);
 }
});
```

**Use Cases:**
- Crafting statistics and achievements
- Custom crafting rewards or bonuses
- Economy systems (track item creation)
- Recipe unlock progression
- Crafting restrictions or limits
- Material consumption tracking

## Complete Usage Example

```java
@Override
protected void setup() {
 getLogger().at(Level.INFO).log("Setting up plugin...");
 
 // Register all event listeners
 EventHelper.onPlayerJoinWorldWithUUID(this, (world, uuid, username) -> {
 getLogger().at(Level.INFO).log("[Join] " + username + " (" + uuid + ") joined " + world.getName());
 // Load player data by UUID
 loadPlayerData(uuid);
 });
 
 EventHelper.onPlayerChat(this, (username, message) -> {
 getLogger().at(Level.INFO).log("[Chat] " + username + ": " + message);
 });
 
 EventHelper.onItemDrop(this, (itemId, quantity, playerEntity) -> {
 String playerName = EntityHelper.getName(playerEntity);
 getLogger().at(Level.INFO).log("[Drop] " + playerName + " dropped " + quantity + "x " + itemId);
 });
 
 EventHelper.onItemPickup(this, (itemId, quantity, playerEntity) -> {
 String playerName = EntityHelper.getName(playerEntity);
 getLogger().at(Level.INFO).log("[Pickup] " + playerName + " picked up " + quantity + "x " + itemId);
 });
 
 EventHelper.onCraftRecipe(this, (outputItemId, quantity, playerEntity) -> {
 String playerName = EntityHelper.getName(playerEntity);
 getLogger().at(Level.INFO).log("[Craft] " + playerName + " crafted " + quantity + "x " + outputItemId);
 });
 
 EventHelper.onPlayerDisconnect(this, (uuid, username) -> {
 getLogger().at(Level.INFO).log("[Disconnect] " + username + " (" + uuid + ") left");
 // Save player data by UUID
 savePlayerData(uuid);
 });
}
```

## See Also

- [EcsEventHelper](EcsEventHelper) - For block-related events
- [WorldHelper](WorldHelper) - For world operations
- [Home](Home) - Back to main page
