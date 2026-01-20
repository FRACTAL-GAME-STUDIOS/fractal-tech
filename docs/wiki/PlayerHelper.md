# PlayerHelper

PlayerHelper provides simplified player-specific utilities for working with player entities in Hytale.

## Overview

PlayerHelper focuses on simple, direct operations available on the Player class, such as game mode checking, messaging, and permissions. For more complex operations like health, effects, and velocity, you'll need to work with the ECS system directly.

## Methods

### Game Mode

#### `getGameMode(Entity entity)`

Get the player's current game mode.

**Parameters:**
- `entity` - The entity (must be a Player)

**Returns:** `GameMode` - The player's current game mode, or null if not a player

**Example:**
```java
// Check player's game mode
GameMode mode = PlayerHelper.getGameMode(player);
if (mode != null) {
 WorldHelper.log(world, "Player is in " + mode.toString() + " mode");
}
```

**Use Cases:**
- Game mode restrictions
- Mode-specific features
- Admin tools
- Minigame mechanics

---

### Messaging

#### `sendMessage(Entity entity, String message)`

Send a plain text message to a player.

**Parameters:**
- `entity` - The entity (must be a Player)
- `message` - The message text to send

**Returns:** `boolean` - true if message was sent successfully

**Example:**
```java
// Send a simple message
boolean sent = PlayerHelper.sendMessage(player, "Welcome to the server!");
if (sent) {
 WorldHelper.log(world, "Welcome message sent");
}
```

**Use Cases:**
- Player notifications
- Quest updates
- System messages
- Chat responses

---

#### `sendMessage(Entity entity, Message message)`

Send a formatted Message object to a player.

**Parameters:**
- `entity` - The entity (must be a Player)
- `message` - The Message object to send (supports formatting)

**Returns:** `boolean` - true if message was sent successfully

**Example:**
```java
// Send a formatted message
Message formattedMsg = Message.raw("Server announcement!");
boolean sent = PlayerHelper.sendMessage(player, formattedMsg);
```

**Use Cases:**
- Formatted announcements
- Colored messages
- Rich text notifications
- Custom message styling

---

### Permissions

#### `hasPermission(Entity entity, String permission)`

Check if a player has a specific permission.

**Parameters:**
- `entity` - The entity (must be a Player)
- `permission` - The permission string to check (e.g., "admin.teleport")

**Returns:** `boolean` - true if the player has the permission

**Example:**
```java
// Check if player can use admin commands
if (PlayerHelper.hasPermission(player, "admin.commands")) {
 WorldHelper.log(world, "Player has admin permissions");
} else {
 PlayerHelper.sendMessage(player, "You don't have permission!");
}
```

**Use Cases:**
- Command access control
- Feature restrictions
- Admin tools
- Role-based systems

---

#### `hasPermission(Entity entity, String permission, boolean defaultValue)`

Check if a player has a specific permission with a default value.

**Parameters:**
- `entity` - The entity (must be a Player)
- `permission` - The permission string to check
- `defaultValue` - The default value if permission is not set

**Returns:** `boolean` - true if the player has the permission or default value

**Example:**
```java
// Check permission with default (allow if not set)
boolean canBuild = PlayerHelper.hasPermission(player, "world.build", true);
if (canBuild) {
 WorldHelper.log(world, "Player can build");
}
```

**Use Cases:**
- Opt-out permissions
- Default access levels
- Graceful permission handling
- New feature rollouts

---

## Important Notes

### Player Type Requirement
All PlayerHelper methods require the entity to be a `Player` instance. Methods will return false/null if called on non-player entities.

### Simplified API
PlayerHelper focuses on simple, direct operations. For complex player operations like:
- Health management
- Status effects
- Velocity/knockback
- Flying state
- Invulnerability

You'll need to work with the ECS system directly or wait for future helper expansions (see `FUTURE_IDEAS.md`).

### Thread Safety
All PlayerHelper operations are thread-safe and handle exceptions gracefully. Failed operations will log warnings but won't crash your plugin.

### Message Formatting
For basic messages, use the `String` version. For advanced formatting (colors, styles), create a `Message` object and use the `Message` version.

---

## Complete Example

Here's a complete example showing common player operations:

```java
// Admin command system
EventHelper.onPlayerChat(plugin, (username, message) -> {
 Entity player = EntityHelper.getPlayerByName(world, username);
 if (player == null) return;
 
 if (message.startsWith("!gamemode")) {
 // Check admin permission
 if (!PlayerHelper.hasPermission(player, "admin.gamemode")) {
 PlayerHelper.sendMessage(player, "You don't have permission to change game mode!");
 return;
 }
 
 // Get current game mode
 GameMode currentMode = PlayerHelper.getGameMode(player);
 PlayerHelper.sendMessage(player, "Your current game mode: " + currentMode);
 
 // Note: Setting game mode requires ECS access
 // See FUTURE_IDEAS.md for planned setGameMode() method
 }
 
 if (message.equals("!info")) {
 // Show player info
 GameMode mode = PlayerHelper.getGameMode(player);
 int itemCount = InventoryHelper.getAllItems(player).size();
 
 PlayerHelper.sendMessage(player, "=== Player Info ===");
 PlayerHelper.sendMessage(player, "Game Mode: " + mode);
 PlayerHelper.sendMessage(player, "Items: " + itemCount);
 
 // Check various permissions
 boolean isAdmin = PlayerHelper.hasPermission(player, "admin");
 boolean canFly = PlayerHelper.hasPermission(player, "fly", false);
 
 PlayerHelper.sendMessage(player, "Admin: " + isAdmin);
 PlayerHelper.sendMessage(player, "Can Fly: " + canFly);
 }
});
```

---

## Integration with Other Helpers

PlayerHelper works seamlessly with other helpers:

```java
// Combined example: Quest system with inventory and messaging
EventHelper.onPlayerChat(plugin, (username, message) -> {
 if (message.equals("!quest")) {
 Entity player = EntityHelper.getPlayerByName(world, username);
 if (player == null) return;
 
 // Check if player is in correct game mode
 GameMode mode = PlayerHelper.getGameMode(player);
 if (mode != GameMode.ADVENTURE) {
 PlayerHelper.sendMessage(player, "Quests only work in adventure mode!");
 return;
 }
 
 // Check quest permission
 if (!PlayerHelper.hasPermission(player, "quests.access", true)) {
 PlayerHelper.sendMessage(player, "You don't have access to quests!");
 return;
 }
 
 // Check inventory for quest items
 if (InventoryHelper.hasItem(player, "QuestItem", 10)) {
 InventoryHelper.removeItem(player, "QuestItem", 10);
 InventoryHelper.giveItem(player, "Diamond", 5);
 PlayerHelper.sendMessage(player, "Quest complete! You received 5 diamonds!");
 } else {
 int count = InventoryHelper.countItem(player, "QuestItem");
 PlayerHelper.sendMessage(player, "You need 10 Quest Items. You have: " + count);
 }
 }
});
```

---

## Future Enhancements

The following features are planned for future versions (see `FUTURE_IDEAS.md`):

### Health & Combat
- `getHealth(entity)` - Get player's current health
- `setHealth(entity, health)` - Set player's health
- `getMaxHealth(entity)` - Get player's max health
- `heal(entity, amount)` - Heal player
- `damage(entity, amount)` - Damage player

### Status Effects
- `giveEffect(entity, effect, duration, amplifier)` - Apply status effect
- `removeEffect(entity, effect)` - Remove status effect
- `clearEffects(entity)` - Clear all effects
- `getActiveEffects(entity)` - Get list of active effects

### Movement & Physics
- `getVelocity(entity)` - Get player velocity
- `setVelocity(entity, velocity)` - Set player velocity
- `knockback(entity, direction, strength)` - Apply knockback
- `launch(entity, upwardForce)` - Launch player upward
- `setFlying(entity, flying)` - Set flying state
- `isFlying(entity)` - Check if flying

### Game Mode
- `setGameMode(entity, gameMode)` - Change player's game mode

These features require deeper ECS system integration and will be added in future updates.

---

## See Also

- [InventoryHelper](InventoryHelper) - Inventory management utilities
- [EntityHelper](EntityHelper) - Entity management utilities
- [EventHelper](EventHelper) - Event handling utilities
- [FUTURE_IDEAS.md](../FUTURE_IDEAS.md) - Planned features and enhancements
