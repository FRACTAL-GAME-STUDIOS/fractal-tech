# UIHelper

A utility class for working with UI elements in Hytale. Provides methods for managing custom pages, HUD components, and UI animations.
This class is very early work in progress and has not been fully tested.

## Table of Contents
- [Custom Pages](#custom-pages)
- [HUD Management](#hud-management)
- [UI Animations](#ui-animations)
- [Utility Methods](#utility-methods)

---

## Custom Pages

### `openCustomPage(Entity entity, CustomUIPage page)`
Open a custom UI page for a player.

**Parameters:**
- `entity` - The player entity
- `page` - The custom UI page to open

**Returns:** `boolean` - true if successful

**Example:**
```java
CustomUIPage myPage = new MyCustomPage();
boolean opened = UIHelper.openCustomPage(player, myPage);
```

### `closeCustomPage(Entity entity)`
Close the currently open custom page for a player.

**Parameters:**
- `entity` - The player entity

**Returns:** `boolean` - true if successful

**Example:**
```java
UIHelper.closeCustomPage(player);
```

### `hasOpenCustomPage(Entity entity)`
Check if a player has a custom page open.

**Parameters:**
- `entity` - The player entity

**Returns:** `boolean` - true if a custom page is open

**Example:**
```java
if (UIHelper.hasOpenCustomPage(player)) {
 LOGGER.log("Player has a UI open");
}
```

---

## HUD Management

### `showHudComponents(Entity entity, HudComponent... components)`
Show specific HUD components for a player.

**Parameters:**
- `entity` - The player entity
- `components` - The HUD components to show

**Returns:** `boolean` - true if successful

**Example:**
```java
UIHelper.showHudComponents(player, 
 HudComponent.HOTBAR,
 HudComponent.HEALTH,
 HudComponent.HUNGER
);
```

### `hideHudComponents(Entity entity, HudComponent... components)`
Hide specific HUD components for a player.

**Parameters:**
- `entity` - The player entity
- `components` - The HUD components to hide

**Returns:** `boolean` - true if successful

**Example:**
```java
// Hide hotbar and health for a cutscene
UIHelper.hideHudComponents(player,
 HudComponent.HOTBAR,
 HudComponent.HEALTH
);
```

### `setVisibleHudComponents(Entity entity, HudComponent... components)`
Set which HUD components are visible (hides all others).

**Parameters:**
- `entity` - The player entity
- `components` - The HUD components to make visible

**Returns:** `boolean` - true if successful

**Example:**
```java
// Show only health and hunger
UIHelper.setVisibleHudComponents(player,
 HudComponent.HEALTH,
 HudComponent.HUNGER
);
```

### Available HUD Components

- `HudComponent.HOTBAR` - Player hotbar
- `HudComponent.HEALTH` - Health bar
- `HudComponent.HUNGER` - Hunger bar
- `HudComponent.ARMOR` - Armor display
- `HudComponent.EXPERIENCE` - Experience bar
- `HudComponent.CROSSHAIR` - Crosshair
- And more...

---

## UI Animations

### `fadeOutCustomPage(Entity entity, String pageId, int durationMs)`
Fade out a custom UI page over a specified duration.

**Parameters:**
- `entity` - The player entity
- `pageId` - The ID of the custom page
- `durationMs` - Duration of the fade in milliseconds

**Returns:** `boolean` - true if fade started successfully

**Example:**
```java
// Fade out over 1 second
UIHelper.fadeOutCustomPage(player, "myCustomPage", 1000);
```

### `fadeInCustomPage(Entity entity, String pageId, int durationMs)`
Fade in a custom UI page over a specified duration.

**Parameters:**
- `entity` - The player entity
- `pageId` - The ID of the custom page
- `durationMs` - Duration of the fade in milliseconds

**Returns:** `boolean` - true if fade started successfully

**Example:**
```java
// Fade in over 1 second
UIHelper.fadeInCustomPage(player, "myCustomPage", 1000);
```

### `fadeCustomPage(Entity entity, String pageId, float startOpacity, float endOpacity, int durationMs)`
Fade a custom UI page from one opacity to another.

**Parameters:**
- `entity` - The player entity
- `pageId` - The ID of the custom page
- `startOpacity` - Starting opacity (0.0 to 1.0)
- `endOpacity` - Ending opacity (0.0 to 1.0)
- `durationMs` - Duration of the fade in milliseconds

**Returns:** `boolean` - true if fade started successfully

**Example:**
```java
// Fade from 50% to 100% opacity over 2 seconds
UIHelper.fadeCustomPage(player, "myCustomPage", 0.5f, 1.0f, 2000);
```

---

## Utility Methods

### `getPageManager(Entity entity)`
Get the PageManager for a player entity.

**Parameters:**
- `entity` - The player entity

**Returns:** `PageManager` or `null` if not available

**Example:**
```java
PageManager pageManager = UIHelper.getPageManager(player);
if (pageManager != null) {
 // Work with page manager directly
}
```

### `getHudManager(Entity entity)`
Get the HudManager for a player entity.

**Parameters:**
- `entity` - The player entity

**Returns:** `HudManager` or `null` if not available

**Example:**
```java
HudManager hudManager = UIHelper.getHudManager(player);
if (hudManager != null) {
 // Work with HUD manager directly
}
```

---

## Complete Examples

### Example 1: Cutscene System

```java
public void startCutscene(Entity player) {
 // Hide all HUD elements
 UIHelper.hideHudComponents(player,
 HudComponent.HOTBAR,
 HudComponent.HEALTH,
 HudComponent.HUNGER,
 HudComponent.ARMOR,
 HudComponent.CROSSHAIR
 );
 
 // Show cutscene UI
 CustomUIPage cutscenePage = new CutsceneUIPage();
 UIHelper.openCustomPage(player, cutscenePage);
 
 // Fade in the cutscene UI
 UIHelper.fadeInCustomPage(player, "cutscene", 1000);
 
 // After cutscene ends (e.g., 10 seconds)
 WorldHelper.waitTicks(world, 200, () -> {
 endCutscene(player);
 });
}

public void endCutscene(Entity player) {
 // Fade out cutscene UI
 UIHelper.fadeOutCustomPage(player, "cutscene", 1000);
 
 // Wait for fade to complete, then close and restore HUD
 WorldHelper.waitTicks(world, 20, () -> {
 UIHelper.closeCustomPage(player);
 
 // Restore HUD
 UIHelper.showHudComponents(player,
 HudComponent.HOTBAR,
 HudComponent.HEALTH,
 HudComponent.HUNGER,
 HudComponent.ARMOR,
 HudComponent.CROSSHAIR
 );
 });
}
```

### Example 2: Custom Menu with Fade

```java
public void openShopMenu(Entity player) {
 // Create and open shop page
 CustomUIPage shopPage = new ShopUIPage();
 UIHelper.openCustomPage(player, shopPage);
 
 // Fade in the shop
 UIHelper.fadeInCustomPage(player, "shop", 500);
 
 // Hide game HUD while shopping
 UIHelper.hideHudComponents(player,
 HudComponent.HOTBAR,
 HudComponent.CROSSHAIR
 );
}

public void closeShopMenu(Entity player) {
 // Fade out shop
 UIHelper.fadeOutCustomPage(player, "shop", 500);
 
 // Wait for fade, then close and restore HUD
 WorldHelper.waitTicks(world, 10, () -> {
 UIHelper.closeCustomPage(player);
 UIHelper.showHudComponents(player,
 HudComponent.HOTBAR,
 HudComponent.CROSSHAIR
 );
 });
}
```

### Example 3: Minimal HUD Mode

```java
public void enableMinimalHUD(Entity player) {
 // Show only essential elements
 UIHelper.setVisibleHudComponents(player,
 HudComponent.HEALTH,
 HudComponent.CROSSHAIR
 );
}

public void enableFullHUD(Entity player) {
 // Show all standard elements
 UIHelper.setVisibleHudComponents(player,
 HudComponent.HOTBAR,
 HudComponent.HEALTH,
 HudComponent.HUNGER,
 HudComponent.ARMOR,
 HudComponent.EXPERIENCE,
 HudComponent.CROSSHAIR
 );
}
```

### Example 4: Notification System

```java
public void showNotification(Entity player, String message) {
 // Create notification page
 CustomUIPage notification = new NotificationPage(message);
 UIHelper.openCustomPage(player, notification);
 
 // Fade in quickly
 UIHelper.fadeInCustomPage(player, "notification", 300);
 
 // Show for 3 seconds
 WorldHelper.waitTicks(world, 60, () -> {
 // Fade out
 UIHelper.fadeOutCustomPage(player, "notification", 500);
 
 // Close after fade
 WorldHelper.waitTicks(world, 10, () -> {
 UIHelper.closeCustomPage(player);
 });
 });
}
```

---

## Tips

1. **Always wait for fades to complete** - Use `WorldHelper.waitTicks()` before closing pages
2. **Restore HUD after custom UIs** - Remember to show HUD components when closing custom pages
3. **Use fade animations** - Makes UI transitions feel more polished
4. **Check if page is open** - Use `hasOpenCustomPage()` before opening new pages
5. **Combine with WorldHelper** - Use tick scheduling for timed UI events

---

## Common Patterns

### Pattern 1: Temporary UI Overlay

```java
// Open
UIHelper.openCustomPage(player, overlay);
UIHelper.fadeInCustomPage(player, "overlay", 500);

// Auto-close after delay
WorldHelper.waitTicks(world, 100, () -> {
 UIHelper.fadeOutCustomPage(player, "overlay", 500);
 WorldHelper.waitTicks(world, 10, () -> {
 UIHelper.closeCustomPage(player);
 });
});
```

### Pattern 2: Toggle HUD Visibility

```java
boolean hudVisible = true;

public void toggleHUD(Entity player) {
 if (hudVisible) {
 UIHelper.hideHudComponents(player, HudComponent.values());
 } else {
 UIHelper.showHudComponents(player, HudComponent.values());
 }
 hudVisible = !hudVisible;
}
```

### Pattern 3: Smooth Page Transition

```java
public void transitionPage(Entity player, CustomUIPage newPage, String newPageId) {
 // Fade out current page
 UIHelper.fadeOutCustomPage(player, currentPageId, 300);
 
 // Wait for fade
 WorldHelper.waitTicks(world, 6, () -> {
 // Close old page
 UIHelper.closeCustomPage(player);
 
 // Open new page
 UIHelper.openCustomPage(player, newPage);
 
 // Fade in new page
 UIHelper.fadeInCustomPage(player, newPageId, 300);
 });
}
```

---

## Notes

- **Fade animations** use `PageManager.updateCustomPage()` to modify opacity over time
- **HUD components** are managed server-side and synced to the client
- **Custom pages** must extend `CustomUIPage` and implement required methods
- **Page IDs** should be unique and consistent for fade operations

---

## Related Helpers
- [WorldHelper](WorldHelper) - For tick scheduling and timing
- [PlayerHelper](PlayerHelper) - For player-specific operations
- [EntityHelper](EntityHelper) - For entity operations
