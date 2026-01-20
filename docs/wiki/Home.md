# HytaleBoilerplate

A utility library that simplifies common Hytale modding tasks by providing tested helpers, utilities, and workarounds for the Hytale API.

## Helper Classes

### Core Helpers
- [EventHelper](EventHelper) - Simple global event registration (chat, items with player entity, player join/disconnect)
- [EcsEventHelper](EcsEventHelper) - ECS-based events (block breaking, placing, damage with player entity, zone discovery)
- [DeathHelper](DeathHelper) - Entity death tracking with killer info, damage source, and death position
- [StatsHelper](StatsHelper) - Entity stats management (health, stamina, mana), modifiers, buffs/debuffs
- [WorldHelper](WorldHelper) - World operations, tick tracking, time/day system
- [EntityHelper](EntityHelper) - Entity queries, teleportation, NPC spawning
- [ZoneHelper](ZoneHelper) - Zone discovery tracking, current zone queries, player-in-zone searches
- [ComponentHelper](ComponentHelper) - ECS component operations

### Block & Item Helpers
- [BlockHelper](BlockHelper) - Block manipulation, region operations
- [BlockStateHelper](BlockStateHelper) - Block state management (chests, signs, containers)
- [ItemHelper](ItemHelper) - Item creation, container management, random slot placement
- [LootHelper](LootHelper) - Custom block drops, loot tables, item entity spawning
- [ContainerHelper](ContainerHelper) - Container change tracking, chest protection, item logging
- [EquipmentHelper](EquipmentHelper) - Equipment change tracking (armor, offhand/utility, tools)

### Player & UI Helpers
- [PlayerHelper](PlayerHelper) - Player utilities, messaging, permissions, game mode, skin data
- [InventoryHelper](InventoryHelper) - Inventory management, item giving/removing, hotbar operations
- [UIHelper](UIHelper) - Custom pages, HUD management, UI animations

## Useful Information

- [Complete Block List](BlockList) - All 3,947+ blocks with IDs and names
- [Full Entity List](https://hytalemodding.dev/en/docs/server/entities)


## Features

This project provides a comprehensive utility library for Hytale modding with helpers for:

- **Event handling** - Item drops/pickups/crafting with player entity access, player joins, chat, disconnects, block breaking/placing/damage with player entity
- **Death tracking** - Entity death detection with killer info, damage source, death position, automatic name resolution
- **Stats management** - Get/set entity stats (health, stamina, mana), additive/multiplicative modifiers, buffs/debuffs
- **Entity management** - Teleportation, proximity searches, player homes, entity iteration, NPC spawning
- **World operations** - Tick tracking, messaging, logging, time/day system
- **Zone tracking** - Zone discovery tracking, current zone queries, player-in-zone searches
- **Block operations** - Name-based block setting, region filling, block finding
- **Block state management** - Working with chests, signs, and other stateful blocks
- **Custom loot drops** - Override block drops, custom loot tables, physical item entity spawning
- **Container tracking** - Automatic transaction parsing with ContainerTransaction API, interaction-based registration, correct shift-click detection, protection systems
- **Equipment tracking** - Detect equip/unequip/replace for armor, offhand/utility and tools with parsed `EquipmentChange`
- **Item & container operations** - Item creation, container filling (sequential & random), inventory queries
- **ECS component manipulation** - Type-safe component operations
- **Inventory management** - Give/remove items, check inventory, hotbar operations, slot management
- **Player utilities** - Messaging, permissions, game mode checking, skin data access
- **UI management** - Custom pages, HUD control, fade animations

## Quick Start

```java
// Example: Detect item drops with player entity
EventHelper.onItemDrop(plugin, (itemId, quantity, playerEntity) -> {
 String playerName = EntityHelper.getName(playerEntity);
 WorldHelper.log(world, playerName + " dropped: " + itemId + " x" + quantity);
 // Apply stamina cost for dropping items
 StatsHelper.addStat(playerEntity, "Stamina", -1.0f);
});

// Example: Teleport all cows to a location
List<Entity> allEntities = EntityHelper.getAllEntities(world);
for (Entity entity : allEntities) {
 if ("Cow".equals(EntityHelper.getEntityType(entity))) {
 EntityHelper.teleport(entity, new Vector3d(100, 64, 100));
 }
}

// Example: Detect block breaking
this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, (event) -> {
 World world = event.getWorld();
 
 EcsEventHelper.onBlockBreak(world, (position, blockTypeId) -> {
 WorldHelper.log(world, "Block broken: " + blockTypeId + " at " + position);
 });
});

// Example: Custom block drops
EventHelper.onPlayerJoinWorld(plugin, world -> {
 // Make stone drop diamonds
 LootHelper.registerBlockLootReplacement(world, "Rock_Stone", (pos, blockType) -> {
 return Arrays.asList(
 new LootHelper.ItemDrop("Rock_Gem_Diamond", 3)
 );
 });
});
```

## Links

- [GitHub Repository](https://github.com//HytaleBoilerplate)
- [Hytale Modding Docs](https://hytalemodding.dev)
- [Hytale API Reference](https://hytalemodding.dev/en/docs/server/api)
