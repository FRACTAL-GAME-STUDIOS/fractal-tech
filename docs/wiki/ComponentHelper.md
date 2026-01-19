# ComponentHelper

Type-safe ECS component operations using ComponentType for proper API usage.

## Overview

**What it does:**
- Get/put/add/remove components with proper type safety
- Simplified display name management
- Extract item details (ID, quantity) from ItemComponent
- Handles null checks and exceptions gracefully

**When to use:**
- Adding/removing components from entities
- Setting display names on entities
- Reading item data from ground items
- Any ECS component manipulation

## Available Methods

### Setup: Get Component Accessor

First, get the component accessor from the world:

```java
ComponentAccessor<EntityStore> accessor = world.getEntityStore().getStore().getAccessor();
```

### getComponent(accessor, entityRef, componentType)

Get a component from an entity.

```java
ComponentType<EntityStore, ItemComponent> itemType = ItemComponent.getComponentType();
ItemComponent item = ComponentHelper.getComponent(accessor, entityRef, itemType);

if (item != null) {
 String itemId = item.getItemStack().getItemId();
}
```

### putComponent(accessor, entityRef, componentType, component)

Add or update a component on an entity.

```java
ComponentType<EntityStore, DisplayNameComponent> nameType = DisplayNameComponent.getComponentType();
DisplayNameComponent nameComp = new DisplayNameComponent(Message.raw("Custom Label"));
ComponentHelper.putComponent(accessor, entityRef, nameType, nameComp);
```

### addComponent(accessor, entityRef, componentType, component)

Add a new component (fails if already exists).

```java
ComponentHelper.addComponent(accessor, entityRef, componentType, component);
```

### removeComponent(accessor, entityRef, componentType)

Remove a component from an entity.

```java
ComponentHelper.removeComponent(accessor, entityRef, nameType);
```

### hasComponent(accessor, entityRef, componentType)

Check if an entity has a component.

```java
if (ComponentHelper.hasComponent(accessor, entityRef, itemType)) {
 // Entity is an item
}
```

### setDisplayName(accessor, entityRef, displayName)

Simplified method to set display name (creates component automatically).

```java
ComponentHelper.setDisplayName(accessor, entityRef, "Diamond Sword +5");
```

### getItemId(accessor, entityRef) / getItemQuantity(accessor, entityRef)

Convenience methods for item entities.

```java
String itemId = ComponentHelper.getItemId(accessor, entityRef);
int quantity = ComponentHelper.getItemQuantity(accessor, entityRef);

if (itemId != null) {
 getLogger().at(Level.INFO).log("Found item: " + itemId + " x" + quantity);
}
```

## Component Paths

Common component types you'll work with:

```java
// Items
com.hypixel.hytale.server.core.modules.entity.item.ItemComponent
com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent

// Players
com.hypixel.hytale.server.core.entity.entities.Player
com.hypixel.hytale.server.core.universe.PlayerRef
```

## See Also

- [EntityHelper](EntityHelper) - For entity operations
- [WorldHelper](WorldHelper) - For world operations
- [Home](Home) - Back to main page
