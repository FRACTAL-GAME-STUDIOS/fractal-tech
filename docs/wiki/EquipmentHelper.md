# EquipmentHelper

Track equipment changes (armor, utility/offhand, and tools) for any `LivingEntity`.

This helper listens to a living entity’s inventory containers (armor/utility/tools) and fires a callback whenever equipment is equipped, unequipped, or replaced.

## Features

- Detect armor changes (Head/Chest/Hands/Legs)
- Detect utility/offhand changes
- Detect tool slot changes
- Provides a structured `EquipmentChange` object
 - Slot type + slot index
 - Old/new item stacks
 - Convenience getters for item IDs and quantities
 - Equip/unequip helpers
- Works with normal drag/drop and shift-click equip

## Basic Usage

### Track a single entity

```java
EquipmentHelper.onEquipmentChange(world, livingEntity, (change, container) -> {
 String action = change.isEquipping() ? "EQUIPPED" : "UNEQUIPPED";

 LOGGER.at(Level.INFO).log("EQUIPMENT CHANGE");
 LOGGER.at(Level.INFO).log("Slot Type: " + change.getSlotType());
 LOGGER.at(Level.INFO).log("Slot Index: " + change.getSlotIndex());
 LOGGER.at(Level.INFO).log("Action: " + action);

 if (change.getSlotType() == EquipmentHelper.EquipmentSlotType.ARMOR) {
 ItemArmorSlot armorSlot = change.getArmorSlot();
 if (armorSlot != null) {
 LOGGER.at(Level.INFO).log("Armor Slot: " + armorSlot.name());
 }
 }

 LOGGER.at(Level.INFO).log("Old Item: " + (change.getOldItemId() != null ? change.getOldItemId() : "(empty)"));
 LOGGER.at(Level.INFO).log("New Item: " + (change.getNewItemId() != null ? change.getNewItemId() : "(empty)"));
});
```

### Track players when they join

`EquipmentHelper` only needs a valid `LivingEntity` with a fully initialized inventory.

A good pattern is registering inside your join callback:

```java
EventHelper.onPlayerJoinWorldWithUUID(plugin, (world, uuid, username) -> {
 // Find the player entity by UUID
 for (Entity entity : world.getPlayers()) {
 if (EntityHelper.getUUID(entity).equals(uuid) && entity instanceof LivingEntity livingEntity) {
 EquipmentHelper.onEquipmentChange(world, livingEntity, (change, container) -> {
 LOGGER.at(Level.INFO).log(username + " changed equipment: " + change.getSlotType());
 });
 break;
 }
 }
});
```

## EquipmentChange

The callback receives an `EquipmentChange` describing the change:

### Key fields

- `getEntity()` - the entity whose equipment changed
- `getSlotType()` - `ARMOR`, `UTILITY`, or `TOOLS`
- `getSlotIndex()` - slot index inside that container
- `getOldItem()` / `getNewItem()` - old/new `ItemStack` (may be null)
- `getAction()` - internal action string (`ADD`, `REMOVE`, `REPLACE`, `SET`, etc.)
- `getTransaction()` - the raw underlying `Transaction`

### Convenience methods

- `isEquipping()` - true for equip/put-on operations
- `isUnequipping()` - true for unequip/take-off operations
- `getOldItemId()` / `getNewItemId()` - item IDs (or null)
- `getOldQuantity()` / `getNewQuantity()` - quantities
- `getArmorSlot()` - returns `ItemArmorSlot` for armor changes (or null)

### Cancellation

- `isCancelled()` - check if the change has been cancelled
- `setCancelled(boolean)` - cancel the equipment change

When you call `setCancelled(true)`, the equipment change will be reverted:
- The old item is restored to the equipment slot
- The new item is returned to its source inventory slot (preserving durability, metadata, etc.)

## Cancelling Equipment Changes

You can prevent equipment changes by calling `setCancelled(true)` in your callback:

```java
EquipmentHelper.onEquipmentChange(world, player, (change, container) -> {
 // Prevent equipping diamond armor
 if (change.isEquipping() && change.getNewItemId() != null) {
 if (change.getNewItemId().contains("Adamantite")) {
 change.setCancelled(true);
 WorldHelper.log(world, "You cannot equip Adamantite armor!");
 }
 }
});
```

### Use Cases for Cancellation

**Class/Level Restrictions:**
```java
if (change.isEquipping() && change.getNewItemId().contains("Adamantite")) {
 if (playerLevel < 10) {
 change.setCancelled(true);
 WorldHelper.log(world, "You must be level 10 to wear Adamantite armor!");
 }
}
```

**Cursed Items:**
```java
if (change.isUnequipping() && change.getOldItemId().contains("Cursed")) {
 change.setCancelled(true);
 WorldHelper.log(world, "The cursed item cannot be removed!");
}
```

**Zone Restrictions:**
```java
if (change.isEquipping() && isInPeacefulZone(player)) {
 if (change.getSlotType() == EquipmentSlotType.ARMOR) {
 change.setCancelled(true);
 WorldHelper.log(world, "No armor allowed in peaceful zones!");
 }
}
```

**Permission-Based Equipment:**
```java
if (change.isEquipping() && change.getNewItemId().contains("VIP")) {
 if (!hasVIPPermission(player)) {
 change.setCancelled(true);
 WorldHelper.log(world, "This is VIP-only equipment!");
 }
}
```

## Notes

- `EquipmentHelper` listens to equipment containers. Inventory (storage/hotbar) events may still fire separately when items are moved around.
- If you want only equipment changes, filter by `change.getSlotType()`.
- Cancelled equipment changes will restore items to their exact original slots, preserving durability and metadata.
