# TitleHelper

## Overview

TitleHelper provides utilities for displaying on-screen event titles to players using Hytale's built-in title system. Event titles are large, centered text displays perfect for important announcements, quest updates, boss encounters, and zone notifications.

## Features

- **Major & Minor Titles** - Large prominent titles or smaller subtle notifications
- **Primary & Secondary Text** - Main title with optional subtitle
- **Custom Icons** - Display icons alongside titles
- **Fade Animations** - Control fade in/out timing
- **Easy Presets** - Convenience methods for common use cases

## When to Use

- Quest notifications and updates
- Boss encounter announcements
- Zone entry/discovery messages
- Achievement unlocks
- Important game events
- Player notifications

## Basic Usage

### Show a Major Title

```java
// Simple major title (3 second duration)
TitleHelper.showMajorTitle(playerEntity, "Quest Started", "Find the Ancient Sword", 3.0f);

// Boss encounter
TitleHelper.showBossTitle(playerEntity, "Ancient Dragon", "Prepare for battle!");
```

### Show a Minor Title

```java
// Subtle notification (2 second duration)
TitleHelper.showMinorTitle(playerEntity, "Item Acquired", "Ancient Key", 2.0f);

// Zone entry
TitleHelper.showZoneEntry(playerEntity, "Emerald Grove");
```

### Quick Notification

```java
// 1 second notification
TitleHelper.showNotification(playerEntity, "Checkpoint reached!");
```

## API Reference

### Major Titles

#### `showMajorTitle(player, primaryText, secondaryText, durationSeconds)`

Show a large, prominent title to a player.

**Parameters:**
- `player` (Entity) - The player entity
- `primaryText` (String) - Main title text (large)
- `secondaryText` (String, nullable) - Subtitle text (smaller, optional)
- `durationSeconds` (float) - How long to display in seconds

**Returns:** `boolean` - true if successful

**Example:**
```java
TitleHelper.showMajorTitle(player, "Level Up!", "You are now level 10", 3.0f);
```

### Minor Titles

#### `showMinorTitle(player, primaryText, secondaryText, durationSeconds)`

Show a smaller, less intrusive title to a player.

**Parameters:**
- `player` (Entity) - The player entity
- `primaryText` (String) - Main title text
- `secondaryText` (String, nullable) - Subtitle text (optional)
- `durationSeconds` (float) - How long to display in seconds

**Returns:** `boolean` - true if successful

**Example:**
```java
TitleHelper.showMinorTitle(player, "New Area", "Ancient Ruins", 2.0f);
```

### Custom Titles

#### `showTitle(player, isMajor, primaryText, secondaryText, iconPath, durationSeconds, fadeInSeconds, fadeOutSeconds)`

Show a title with full control over all parameters.

**Parameters:**
- `player` (Entity) - The player entity
- `isMajor` (boolean) - true for major title (large), false for minor (smaller)
- `primaryText` (String) - Main title text
- `secondaryText` (String, nullable) - Subtitle text (optional)
- `iconPath` (String, nullable) - Path to icon asset (optional)
- `durationSeconds` (float) - How long to display in seconds
- `fadeInSeconds` (float) - Fade in duration in seconds
- `fadeOutSeconds` (float) - Fade out duration in seconds

**Returns:** `boolean` - true if successful

**Example:**
```java
TitleHelper.showTitle(
 player,
 true, // Major title
 "Achievement Unlocked",
 "Master Explorer",
 "icons/achievement_star",
 4.0f, // 4 second duration
 0.5f, // 0.5 second fade in
 0.5f // 0.5 second fade out
);
```

#### `showTitleWithIcon(player, isMajor, primaryText, secondaryText, iconPath, durationSeconds)`

Show a title with a custom icon using default fade timings.

**Parameters:**
- `player` (Entity) - The player entity
- `isMajor` (boolean) - true for major, false for minor
- `primaryText` (String) - Main title text
- `secondaryText` (String, nullable) - Subtitle text (optional)
- `iconPath` (String) - Path to icon asset
- `durationSeconds` (float) - How long to display in seconds

**Returns:** `boolean` - true if successful

### Convenience Methods

#### `showNotification(player, text)`

Show a quick 1-second minor title notification.

**Example:**
```java
TitleHelper.showNotification(player, "Checkpoint saved!");
```

#### `showQuestUpdate(player, questName, objective)`

Show a 3-second major title for quest updates.

**Example:**
```java
TitleHelper.showQuestUpdate(player, "The Lost Artifact", "Collect 5 Ancient Fragments");
```

#### `showBossTitle(player, bossName, subtitle)`

Show a 4-second major title for boss encounters.

**Example:**
```java
TitleHelper.showBossTitle(player, "Shadow Lord", "Prepare yourself!");
```

#### `showZoneEntry(player, zoneName)`

Show a 2-second minor title for zone entry.

**Example:**
```java
TitleHelper.showZoneEntry(player, "Whispering Woods");
```

#### `showAchievement(player, achievementName)`

Show a 3-second major title for achievement unlocks.

**Example:**
```java
TitleHelper.showAchievement(player, "First Steps");
```

### Hiding Titles

#### `hideTitle(player, fadeOutSeconds)`

Hide/clear the current title with a fade out animation.

**Parameters:**
- `player` (Entity) - The player entity
- `fadeOutSeconds` (float) - Fade out duration in seconds

**Returns:** `boolean` - true if successful

**Example:**
```java
TitleHelper.hideTitle(player, 0.5f);
```

#### `clearTitle(player)`

Instantly clear the current title (no fade).

**Example:**
```java
TitleHelper.clearTitle(player);
```

## Practical Examples

### Quest System

```java
// Quest started
EcsEventHelper.onBlockInteract(world, (position, blockTypeId, playerEntity) -> {
 if ("Quest_Board".equals(blockTypeId)) {
 TitleHelper.showQuestUpdate(
 playerEntity,
 "The Ancient Prophecy",
 "Speak to the Elder in the village"
 );
 }
});

// Quest completed
public void completeQuest(Entity player, String questName) {
 TitleHelper.showMajorTitle(
 player,
 "Quest Complete!",
 questName,
 4.0f
 );
}
```

### Boss Encounter System

```java
// Boss spawned
public void spawnBoss(World world, Vector3i position, String bossName) {
 // Spawn the boss entity
 Entity boss = EntityHelper.spawnNPC(world, position, "Boss_Dragon");
 
 // Show title to all nearby players
 for (Entity player : EntityHelper.getPlayersInRadius(world, position, 50.0)) {
 TitleHelper.showBossTitle(player, bossName, "A powerful foe approaches!");
 }
}

// Boss defeated
DeathHelper.onEntityDeath(world, (deadEntity, killerEntity, damageSource) -> {
 if (EntityHelper.getEntityType(deadEntity).contains("Boss")) {
 if (killerEntity != null && PlayerHelper.isPlayer(killerEntity)) {
 TitleHelper.showMajorTitle(
 killerEntity,
 "Victory!",
 "Boss Defeated",
 5.0f
 );
 }
 }
});
```

### Zone Discovery System

```java
EcsEventHelper.onZoneDiscovery(world, (discoveryInfo) -> {
 Entity player = discoveryInfo.getPlayerEntity();
 String zoneName = discoveryInfo.getZoneName();
 
 // Show zone entry title
 TitleHelper.showZoneEntry(player, zoneName);
 
 // Check if it's a special zone
 if (zoneName.contains("Ancient") || zoneName.contains("Forbidden")) {
 WorldHelper.waitTicks(world, 60, () -> {
 TitleHelper.showMinorTitle(
 player,
 "Danger Ahead",
 "Powerful enemies lurk here",
 3.0f
 );
 });
 }
});
```

### Achievement System

```java
public class AchievementManager {
 private final Map<String, Set<String>> playerAchievements = new ConcurrentHashMap<>();
 
 public void unlockAchievement(Entity player, String achievementId, String achievementName) {
 String playerName = EntityHelper.getName(player);
 
 playerAchievements.computeIfAbsent(playerName, k -> new HashSet<>());
 
 if (playerAchievements.get(playerName).add(achievementId)) {
 // First time unlocking this achievement
 TitleHelper.showAchievement(player, achievementName);
 
 // Play sound effect
 SoundHelper.playSound2DToPlayer(
 WorldHelper.getWorld(player),
 "Achievement_Unlock",
 player
 );
 }
 }
}
```

### Custom Event Titles with Icons

```java
// Rare item drop with icon
EventHelper.onItemPickup(world, (transaction, playerEntity) -> {
 String itemId = transaction.getItemId();
 
 if (itemId != null && itemId.contains("Legendary")) {
 TitleHelper.showTitleWithIcon(
 playerEntity,
 true, // Major title
 "Legendary Item!",
 itemId.replace("_", " "),
 "icons/legendary_star",
 4.0f
 );
 }
});
```

### Timed Title Sequence

```java
public void showTitleSequence(World world, Entity player) {
 // First title
 TitleHelper.showMajorTitle(player, "Welcome", "To the Adventure", 2.0f);
 
 // Second title after 3 seconds
 WorldHelper.waitTicks(world, 60, () -> {
 TitleHelper.showMinorTitle(player, "Your journey begins", "Good luck!", 2.0f);
 });
 
 // Clear after 6 seconds total
 WorldHelper.waitTicks(world, 120, () -> {
 TitleHelper.clearTitle(player);
 });
}
```

## Technical Details

### How It Works

1. **FormattedMessage Creation** - Converts plain text strings into Hytale's FormattedMessage format
2. **Packet Construction** - Creates ShowEventTitle or HideEventTitle packets with timing parameters
3. **Packet Delivery** - Sends packets via PlayerRef's PacketHandler to the client
4. **Client Display** - Client receives packet and displays title with fade animations

### Title Types

- **Major Titles** - Large, prominent text displayed in the center of the screen
- **Minor Titles** - Smaller, less intrusive text for subtle notifications

### Timing

- **Duration** - How long the title remains fully visible (in seconds)
- **Fade In** - Time taken to fade in from transparent to opaque (default: 0.5s)
- **Fade Out** - Time taken to fade out from opaque to transparent (default: 0.5s)

### Thread Safety

- All methods are thread-safe
- Can be called from any thread
- Packet sending is handled by Hytale's networking system

## Important Notes

- **Player Validation** - Always returns false if entity is not a player
- **Null Safety** - Secondary text and icon paths are optional (can be null)
- **Duration Range** - Use reasonable durations (0.5 - 10 seconds recommended)
- **Fade Timing** - Keep fade times short (0.3 - 1.0 seconds) for best UX
- **Title Overlap** - New titles replace existing ones immediately
- **Icon Paths** - Icons must be valid asset paths in the game's resource system

## See Also

- [PlayerHelper](PlayerHelper) - Player messaging and management
- [UIHelper](UIHelper) - Custom pages and HUD control
- [WorldHelper](WorldHelper) - Timing and scheduling
- [ZoneHelper](ZoneHelper) - Zone discovery tracking
