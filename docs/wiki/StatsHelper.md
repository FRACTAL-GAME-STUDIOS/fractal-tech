# StatsHelper

**StatsHelper** provides convenient methods for managing entity stats like health, stamina, mana, and oxygen. It simplifies working with Hytale's EntityStats system by providing easy-to-use methods for getting/setting stats and applying modifiers.

## Features

- **Get/Set Stats** - Read and modify entity stat values (health, stamina, mana, oxygen, etc.)
- **Stat Modifiers** - Add additive or multiplicative modifiers to stats (buffs/debuffs)
- **Min/Max Operations** - Set stats to minimum, maximum, or reset to default
- **Stat Queries** - Get min/max values, percentages, and check stat existence
- **Convenience Methods** - Quick access to common stats (health, stamina, mana, oxygen)
- **Auto-Clamping** - Values are automatically clamped between min and max

## When to use

- RPG-style health/mana systems
- Stamina-based mechanics
- Buff/debuff systems
- Status effects
- Armor/equipment bonuses
- Potion effects
- Difficulty scaling
- Custom stat systems

## API Reference

### Get/Set Stats

**getStat(entity, statName)**
Get the current value of a stat.

**Parameters:**
- `entity` - Entity to get stat from
- `statName` - Stat name (e.g., "Health", "Stamina", "Mana", "Oxygen")

**Returns:** Current stat value, or 0 if stat not found

**Example:**
```java
float health = StatsHelper.getStat(player, "Health");
float stamina = StatsHelper.getStat(player, "Stamina");
```

---

**setStat(entity, statName, value)**
Set the value of a stat (will be clamped to min/max).

**Parameters:**
- `entity` - Entity to set stat on
- `statName` - Stat name
- `value` - New stat value

**Returns:** Actual value set (after clamping)

**Example:**
```java
StatsHelper.setStat(player, "Health", 50.0f);
StatsHelper.setStat(player, "Mana", 100.0f);
```

---

**addStat(entity, statName, amount)**
Add to a stat value (can be negative to subtract).

**Parameters:**
- `entity` - Entity to modify stat on
- `statName` - Stat name
- `amount` - Amount to add (negative to subtract)

**Returns:** New stat value (after clamping)

**Example:**
```java
// Heal 20 health
StatsHelper.addStat(player, "Health", 20.0f);

// Drain 10 stamina
StatsHelper.addStat(player, "Stamina", -10.0f);
```

### Convenience Methods

**getHealth(entity)** / **setHealth(entity, value)**
Quick access to health stat.

**getStamina(entity)** / **setStamina(entity, value)**
Quick access to stamina stat.

**getMana(entity)** / **setMana(entity, value)**
Quick access to mana stat.

**getOxygen(entity)** / **setOxygen(entity, value)**
Quick access to oxygen stat.

**Example:**
```java
float health = StatsHelper.getHealth(player);
StatsHelper.setHealth(player, 100.0f);

float stamina = StatsHelper.getStamina(player);
StatsHelper.setStamina(player, 50.0f);
```

### Stat Queries

**getStatMin(entity, statName)**
Get the minimum value for a stat.

**getStatMax(entity, statName)**
Get the maximum value for a stat.

**getStatPercentage(entity, statName)**
Get stat as percentage (0.0 to 1.0) between min and max.

**hasStat(entity, statName)**
Check if entity has a specific stat.

**Example:**
```java
float maxHealth = StatsHelper.getStatMax(player, "Health");
float healthPercent = StatsHelper.getStatPercentage(player, "Health");

if (healthPercent < 0.25f) {
 // Player is low on health!
}

boolean hasMana = StatsHelper.hasStat(player, "Mana");
```

### Min/Max Operations

**maximizeStat(entity, statName)**
Set a stat to its maximum value.

**minimizeStat(entity, statName)**
Set a stat to its minimum value.

**resetStat(entity, statName)**
Reset a stat to its initial/default value.

**Example:**
```java
// Fully heal player
StatsHelper.maximizeStat(player, "Health");

// Fully restore stamina
StatsHelper.maximizeStat(player, "Stamina");

// Reset mana to default
StatsHelper.resetStat(player, "Mana");
```

### Stat Modifiers

**addModifier(entity, statName, modifierKey, target, calculationType, amount)**
Add a modifier to a stat.

**Parameters:**
- `entity` - Entity to add modifier to
- `statName` - Stat name
- `modifierKey` - Unique key for this modifier (e.g., "armor_bonus", "speed_potion")
- `target` - `Modifier.ModifierTarget.MIN` or `Modifier.ModifierTarget.MAX`
- `calculationType` - `StaticModifier.CalculationType.ADDITIVE` or `MULTIPLICATIVE`
- `amount` - Amount to add or multiply by

**Returns:** Previous modifier with same key, or null

**Example:**
```java
// Add +20 to max health (armor bonus)
StatsHelper.addModifier(player, "Health", "armor_bonus", 
 Modifier.ModifierTarget.MAX, 
 StaticModifier.CalculationType.ADDITIVE, 
 20.0f);

// Multiply max stamina by 1.5 (strength potion)
StatsHelper.addModifier(player, "Stamina", "strength_potion",
 Modifier.ModifierTarget.MAX,
 StaticModifier.CalculationType.MULTIPLICATIVE,
 1.5f);
```

---

**addAdditiveModifier(entity, statName, modifierKey, amount)**
Add an additive modifier to a stat's max value.

**Example:**
```java
// Add +20 max health from armor
StatsHelper.addAdditiveModifier(player, "Health", "armor_bonus", 20.0f);

// Add +10 max stamina from training
StatsHelper.addAdditiveModifier(player, "Stamina", "training_bonus", 10.0f);
```

---

**addMultiplicativeModifier(entity, statName, modifierKey, multiplier)**
Add a multiplicative modifier to a stat's max value.

**Example:**
```java
// Multiply max health by 1.5 (strength potion)
StatsHelper.addMultiplicativeModifier(player, "Health", "strength_potion", 1.5f);

// Multiply max mana by 2.0 (wizard class)
StatsHelper.addMultiplicativeModifier(player, "Mana", "wizard_class", 2.0f);
```

---

**removeModifier(entity, statName, modifierKey)**
Remove a modifier from a stat.

**Example:**
```java
// Remove armor bonus when armor is unequipped
StatsHelper.removeModifier(player, "Health", "armor_bonus");

// Remove potion effect when it expires
StatsHelper.removeModifier(player, "Stamina", "strength_potion");
```

---

**getModifier(entity, statName, modifierKey)**
Get a modifier from a stat.

**Example:**
```java
Modifier armorBonus = StatsHelper.getModifier(player, "Health", "armor_bonus");
if (armorBonus != null) {
 // Modifier exists
}
```

## Examples

### Basic Health Management

```java
// Get current health
float health = StatsHelper.getHealth(player);
float maxHealth = StatsHelper.getStatMax(player, "Health");

getLogger().at(Level.INFO).log("Health: " + health + " / " + maxHealth);

// Damage player
StatsHelper.addStat(player, "Health", -10.0f);

// Heal player
StatsHelper.addStat(player, "Health", 20.0f);

// Fully heal
StatsHelper.maximizeStat(player, "Health");
```

### Health Percentage Check

```java
float healthPercent = StatsHelper.getStatPercentage(player, "Health");

if (healthPercent < 0.25f) {
 player.sendMessage("⚠️ Low health! Find shelter!");
} else if (healthPercent < 0.5f) {
 player.sendMessage("⚠️ Health is getting low.");
}
```

### Armor Bonus System

```java
// When player equips armor
EventHelper.onItemPickup(plugin, (itemId, quantity) -> {
 if (itemId.contains("Armor")) {
 // Add +20 max health per armor piece
 StatsHelper.addAdditiveModifier(player, "Health", "armor_" + itemId, 20.0f);
 
 float newMaxHealth = StatsHelper.getStatMax(player, "Health");
 player.sendMessage("Max health increased to " + newMaxHealth);
 }
});

// When player unequips armor
EventHelper.onItemDrop(plugin, (itemId, quantity) -> {
 if (itemId.contains("Armor")) {
 // Remove armor bonus
 StatsHelper.removeModifier(player, "Health", "armor_" + itemId);
 
 float newMaxHealth = StatsHelper.getStatMax(player, "Health");
 player.sendMessage("Max health decreased to " + newMaxHealth);
 }
});
```

### Potion Effect System

```java
public void applyStrengthPotion(Entity player, float duration) {
 // Add 1.5x health multiplier
 StatsHelper.addMultiplicativeModifier(player, "Health", "strength_potion", 1.5f);
 
 // Add 1.3x stamina multiplier
 StatsHelper.addMultiplicativeModifier(player, "Stamina", "strength_potion", 1.3f);
 
 player.sendMessage("Strength potion applied!");
 
 // Remove after duration
 WorldHelper.scheduleTask(world, () -> {
 StatsHelper.removeModifier(player, "Health", "strength_potion");
 StatsHelper.removeModifier(player, "Stamina", "strength_potion");
 player.sendMessage("Strength potion wore off.");
 }, (int)(duration * 20)); // Convert seconds to ticks
}
```

### Stamina Drain System

```java
// Drain stamina when sprinting
WorldHelper.onTick(world, () -> {
 for (Entity player : world.getPlayers()) {
 if (isPlayerSprinting(player)) {
 float stamina = StatsHelper.getStamina(player);
 
 if (stamina > 0) {
 // Drain 0.5 stamina per tick
 StatsHelper.addStat(player, "Stamina", -0.5f);
 } else {
 // Out of stamina - slow down player
 stopSprinting(player);
 player.sendMessage("Out of stamina!");
 }
 } else {
 // Regenerate stamina when not sprinting
 float stamina = StatsHelper.getStamina(player);
 float maxStamina = StatsHelper.getStatMax(player, "Stamina");
 
 if (stamina < maxStamina) {
 StatsHelper.addStat(player, "Stamina", 0.2f);
 }
 }
 }
});
```

### Class-Based Stat Bonuses

```java
public void applyClassBonuses(Entity player, String playerClass) {
 switch (playerClass) {
 case "Warrior":
 // Warriors get +50 max health
 StatsHelper.addAdditiveModifier(player, "Health", "class_bonus", 50.0f);
 // And +20 max stamina
 StatsHelper.addAdditiveModifier(player, "Stamina", "class_bonus", 20.0f);
 break;
 
 case "Mage":
 // Mages get 2x max mana
 StatsHelper.addMultiplicativeModifier(player, "Mana", "class_bonus", 2.0f);
 // But -20 max health
 StatsHelper.addAdditiveModifier(player, "Health", "class_bonus", -20.0f);
 break;
 
 case "Rogue":
 // Rogues get 1.5x max stamina
 StatsHelper.addMultiplicativeModifier(player, "Stamina", "class_bonus", 1.5f);
 break;
 }
 
 player.sendMessage("Class bonuses applied: " + playerClass);
}
```

### Difficulty Scaling

```java
public void applyDifficultyModifiers(Entity player, String difficulty) {
 // Remove old difficulty modifiers
 StatsHelper.removeModifier(player, "Health", "difficulty");
 
 switch (difficulty) {
 case "Easy":
 // 1.5x health on easy
 StatsHelper.addMultiplicativeModifier(player, "Health", "difficulty", 1.5f);
 break;
 
 case "Normal":
 // No modifier
 break;
 
 case "Hard":
 // 0.75x health on hard
 StatsHelper.addMultiplicativeModifier(player, "Health", "difficulty", 0.75f);
 break;
 
 case "Nightmare":
 // 0.5x health on nightmare
 StatsHelper.addMultiplicativeModifier(player, "Health", "difficulty", 0.5f);
 break;
 }
}
```

### Stat Display HUD

```java
public void updateStatDisplay(Entity player) {
 float health = StatsHelper.getHealth(player);
 float maxHealth = StatsHelper.getStatMax(player, "Health");
 float healthPercent = StatsHelper.getStatPercentage(player, "Health");
 
 float stamina = StatsHelper.getStamina(player);
 float maxStamina = StatsHelper.getStatMax(player, "Stamina");
 float staminaPercent = StatsHelper.getStatPercentage(player, "Stamina");
 
 float mana = StatsHelper.getMana(player);
 float maxMana = StatsHelper.getStatMax(player, "Mana");
 float manaPercent = StatsHelper.getStatPercentage(player, "Mana");
 
 String display = String.format(
 "❤️ %.0f/%.0f (%.0f%%) | ⚡ %.0f/%.0f (%.0f%%) | 🔮 %.0f/%.0f (%.0f%%)",
 health, maxHealth, healthPercent * 100,
 stamina, maxStamina, staminaPercent * 100,
 mana, maxMana, manaPercent * 100
 );
 
 // Display on HUD using UIHelper
 // UIHelper.showActionBar(player, display);
}
```

### Level-Up System

```java
public void levelUp(Entity player) {
 // Increase max health by 10
 float currentHealthBonus = 0;
 Modifier healthMod = StatsHelper.getModifier(player, "Health", "level_bonus");
 if (healthMod instanceof StaticModifier staticMod) {
 currentHealthBonus = staticMod.getAmount();
 }
 StatsHelper.addAdditiveModifier(player, "Health", "level_bonus", currentHealthBonus + 10);
 
 // Increase max stamina by 5
 float currentStaminaBonus = 0;
 Modifier staminaMod = StatsHelper.getModifier(player, "Stamina", "level_bonus");
 if (staminaMod instanceof StaticModifier staticMod) {
 currentStaminaBonus = staticMod.getAmount();
 }
 StatsHelper.addAdditiveModifier(player, "Stamina", "level_bonus", currentStaminaBonus + 5);
 
 // Fully restore stats on level up
 StatsHelper.maximizeStat(player, "Health");
 StatsHelper.maximizeStat(player, "Stamina");
 StatsHelper.maximizeStat(player, "Mana");
 
 player.sendMessage("Level Up! Stats increased!");
}
```

## Available Stats

Common stats in Hytale:
- **Health** - Entity health
- **Stamina** - Stamina/energy for sprinting
- **Mana** - Magic/ability resource
- **Oxygen** - Underwater breathing
- **SignatureEnergy** - Special ability resource
- **Ammo** - Ammunition count

You can also use custom stats if they're defined in the game's asset files.

## How It Works

StatsHelper uses Hytale's EntityStats system:
- **EntityStatMap** - Component that stores all stats for an entity
- **EntityStatValue** - Individual stat with current value, min, max, and modifiers
- **Modifiers** - Additive or multiplicative modifications to stat values
- **Auto-clamping** - Values are automatically clamped between min and max

Modifiers are applied in this order:
1. Additive modifiers to MIN
2. Multiplicative modifiers to MIN
3. Additive modifiers to MAX
4. Multiplicative modifiers to MAX
5. Current value is clamped to new min/max

## Notes

- **Modifier Keys**: Use unique, descriptive keys (e.g., "iron_armor", "speed_potion_1")
- **Stacking**: Multiple modifiers with different keys stack
- **Overwriting**: Adding a modifier with an existing key overwrites the old one
- **Persistence**: Modifiers persist until removed or entity is unloaded
- **Thread-Safe**: All operations are thread-safe when used with the ECS store
- **Ref Support**: Alternative methods accept `Ref<EntityStore>` and `Store<EntityStore>` for ECS operations

## See Also

- **EffectHelper** - Apply status effects to entities (coming soon)
- [EntityHelper](EntityHelper) - Entity management and queries
