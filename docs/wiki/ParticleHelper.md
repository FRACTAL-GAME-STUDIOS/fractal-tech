# ParticleHelper

`ParticleHelper` provides simplified methods for spawning particle effects in Hytale. It wraps Hytale's `ParticleUtil` to make particle spawning more accessible to modders.

## Features

- Spawn particles at positions, blocks, or entities
- Control visibility (all nearby players or specific players)
- Customize particle scale
- Automatic player detection within range (75 blocks)
- Access to 535+ particle systems

## Particle Lifetimes

**Important:** Particle duration is controlled by the particle system definition itself, not by code.

- Most particle systems are designed to play once and disappear automatically
- Each particle system has its own configured duration in Hytale's assets
- Some particles (like ambient effects) may loop indefinitely by design

### Temporary vs. Looping Particles

**Good for temporary effects:**
- `Impact_*` - Hit/impact effects (e.g., `Impact_Fire`, `Impact_Poison`)
- `Explosion_*` - Explosions (e.g., `Explosion_Medium`, `Explosion_Small`)
- `Dust_*` - Dust/sparkle effects (e.g., `Dust_Sparkles`)
- `Block_Hit_*` - Block hit particles (e.g., `Block_Hit_Crystal`)
- `Effect_*` - Various temporary effects (e.g., `Effect_Death`)

**Avoid for temporary effects (these loop):**
- `*_AoE` - Area of Effect particles (e.g., `Fire_AoE`)
- `*_Constant` - Continuous effects
- `*_Charging` - Charging animations
- `Portal_*` - Portal effects
- `MagicPortal_*` - Magic portals

See [ParticleList.md](ParticleList.md) for all 535 available particle system IDs.

## Basic Usage

### Spawn at Position

```java
import org.hytaledevlib.lib.ParticleHelper;
import com.hypixel.hytale.math.vector.Vector3d;

// Spawn at a specific position
Vector3d position = new Vector3d(100.5, 64.0, 200.5);
ParticleHelper.spawnParticle(world, "Impact_Fire", position);
```

### Spawn at Block Position

```java
import com.hypixel.hytale.math.vector.Vector3i;

// Spawn at a block position
Vector3i blockPos = new Vector3i(100, 64, 200);
ParticleHelper.spawnParticleAtBlock(world, "Block_Sprint_Dirt", blockPos);
```

### Spawn at Entity

```java
// Spawn at entity's position
ParticleHelper.spawnParticleAtEntity(world, "Explosion_Medium", entity);

// Spawn at entity's feet (useful for ground effects)
ParticleHelper.spawnParticleAtEntityFeet(world, "Block_Sprint_Dirt", player);
```

### Custom Scale

```java
// Spawn with custom scale (2.0 = double size)
ParticleHelper.spawnParticle(world, "Impact_Fire", position, 2.0f);
```

### Visible to Specific Player Only

```java
// Only the specified player will see this particle
ParticleHelper.spawnParticleForPlayer(world, "Question", position, player);
```

## API Reference

### `spawnParticle(World, String, Vector3d)`
Spawn a particle at a position, visible to all nearby players (within 75 blocks).

**Parameters:**
- `world` - The world to spawn particles in
- `particleSystemId` - The particle system ID (see ParticleList.md)
- `position` - The position to spawn at

**Example:**
```java
ParticleHelper.spawnParticle(world, "Impact_Fire", new Vector3d(100, 64, 200));
```

---

### `spawnParticle(World, String, Vector3d, float)`
Spawn a particle with custom scale.

**Parameters:**
- `world` - The world to spawn particles in
- `particleSystemId` - The particle system ID
- `position` - The position to spawn at
- `scale` - Scale multiplier (1.0 = normal, 2.0 = double size)

**Example:**
```java
// Spawn a large explosion
ParticleHelper.spawnParticle(world, "Explosion_Medium", position, 3.0f);
```

---

### `spawnParticleAtBlock(World, String, Vector3i)`
Spawn a particle at a block position (centered on the block).

**Parameters:**
- `world` - The world to spawn particles in
- `particleSystemId` - The particle system ID
- `blockPosition` - The block position

**Example:**
```java
Vector3i blockPos = new Vector3i(100, 64, 200);
ParticleHelper.spawnParticleAtBlock(world, "Block_Hit_Crystal", blockPos);
```

---

### `spawnParticleAtEntity(World, String, Entity)`
Spawn a particle at an entity's position.

**Parameters:**
- `world` - The world to spawn particles in
- `particleSystemId` - The particle system ID
- `entity` - The entity to spawn particles at

**Example:**
```java
ParticleHelper.spawnParticleAtEntity(world, "Explosion_Medium", player);
```

---

### `spawnParticleAtEntityFeet(World, String, Entity)`
Spawn a particle at an entity's feet position (useful for ground-based effects).

**Parameters:**
- `world` - The world to spawn particles in
- `particleSystemId` - The particle system ID
- `entity` - The entity to spawn particles at

**Example:**
```java
// Spawn water splash at player's feet
ParticleHelper.spawnParticleAtEntityFeet(world, "Water_Sprint", player);
```

---

### `spawnParticleForPlayer(World, String, Vector3d, Entity)`
Spawn a particle visible only to a specific player.

**Parameters:**
- `world` - The world to spawn particles in
- `particleSystemId` - The particle system ID
- `position` - The position to spawn at
- `player` - The player who should see the particles

**Example:**
```java
// Only this player sees the question mark
ParticleHelper.spawnParticleForPlayer(world, "Question", position, player);
```

## Practical Examples

### Block Break Effect

```java
EcsEventHelper.onBlockBreak(world, (position, blockTypeId, playerEntity) -> {
 if (playerEntity != null) {
 // Spawn impact at block position
 ParticleHelper.spawnParticleAtBlock(world, "Impact_Fire", position);
 
 // Spawn sparkles at player's feet
 ParticleHelper.spawnParticleAtEntityFeet(world, "Water_Sprint", playerEntity);
 }
});
```

### Player Hit Effect

```java
EcsEventHelper.onPlayerDamaged(world, (player, damageAmount, damageSource) -> {
 // Spawn impact effect at player position
 ParticleHelper.spawnParticleAtEntity(world, "Impact_Poison", player);
});
```

### Custom Spell Effect

```java
public void castFireball(World world, Entity caster, Vector3d targetPos) {
 // Spawn charging effect at caster
 ParticleHelper.spawnParticleAtEntity(world, "Fire_Charge_Charging_Constant", caster);
 
 // Spawn explosion at target
 ParticleHelper.spawnParticle(world, "Explosion_Medium", targetPos, 2.0f);
 
 // Spawn fire impact around explosion
 for (int i = 0; i < 8; i++) {
 double angle = (Math.PI * 2 * i) / 8;
 Vector3d offset = new Vector3d(
 targetPos.getX() + Math.cos(angle) * 2,
 targetPos.getY(),
 targetPos.getZ() + Math.sin(angle) * 2
 );
 ParticleHelper.spawnParticle(world, "Impact_Fire", offset);
 }
}
```

### Trail Effect

```java
// Create a particle trail behind a moving entity
WorldHelper.onTickInterval(world, 5, callback) -> {
 if (entity.isValid()) {
 ParticleHelper.spawnParticleAtEntityFeet(world, "Dust_Sparkles", entity);
 }
});
```

## Tips & Best Practices

1. **Test particle effects** - Use ParticleList.md to find appropriate particles
2. **Avoid looping particles** for temporary effects (check particle name patterns)
3. **Use appropriate scale** - Most particles look good at 1.0-2.0 scale
4. **Consider performance** - Don't spawn too many particles at once
5. **Use entity-based spawning** when following moving objects
6. **Use player-specific particles** for UI feedback only that player should see

## Related Helpers

- [EcsEventHelper](EcsEventHelper.md) - For triggering particles on game events
- [WorldHelper](WorldHelper.md) - For scheduling repeating particle effects
- [EntityHelper](EntityHelper.md) - For getting entity positions and information

## See Also

- [ParticleList.md](ParticleList.md) - Complete list of all 535 particle system IDs
