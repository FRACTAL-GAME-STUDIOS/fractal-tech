# SoundHelper

`SoundHelper` provides simplified methods for playing sound effects in Hytale. It wraps Hytale's `SoundUtil` to make sound playback more accessible to modders.

## Features

- Play 2D sounds (UI sounds, no position)
- Play 3D sounds (positional audio in the world)
- Control volume and pitch
- Play sounds to all players or specific players
- Automatic distance-based attenuation for 3D sounds
- Multiple sound categories (SFX, Music, Ambient, Voice)

## Sound Categories

Hytale supports different sound categories that players can control independently in their settings:

- **`SoundCategory.SFX`** - Sound effects (default) - footsteps, impacts, explosions
- **`SoundCategory.MUSIC`** - Background music
- **`SoundCategory.AMBIENT`** - Ambient environmental sounds
- **`SoundCategory.VOICE`** - Voice/dialogue
- **`SoundCategory.MASTER`** - Master volume

See [SoundList.md](SoundList.md) for all available sound event IDs.

## 2D Sounds (UI Sounds)

2D sounds have no position and are heard equally by all players. Perfect for UI feedback, notifications, and global events.

### Basic 2D Sound

```java
import org.hytaledevlib.lib.SoundHelper;

// Play a sound to all players
SoundHelper.playSound2D(world, "SFX_Alchemy_Bench_Open");
```

### 2D Sound with Volume and Pitch

```java
// Play a sound at half volume and higher pitch
SoundHelper.playSound2D(world, "SFX_Creative_Play_Error", 0.5f, 1.5f);
```

### 2D Sound to Specific Player

```java
// Only this player hears the sound
SoundHelper.playSound2DToPlayer(world, "SFX_Axe_Special_Impact", player);

// With custom volume and pitch
SoundHelper.playSound2DToPlayer(world, "SFX_Coins_Land", player, 0.8f, 1.2f);
```

### 2D Sound with Custom Category

```java
import com.hypixel.hytale.protocol.SoundCategory;

// Play music to all players
SoundHelper.playSound2D(world, "Music_MainTheme", SoundCategory.MUSIC, 1.0f, 1.0f);
```

## 3D Sounds (Positional Audio)

3D sounds have a position in the world and are heard by nearby players. Volume decreases with distance.

### Basic 3D Sound

```java
import com.hypixel.hytale.math.vector.Vector3d;

// Play impact sound at a position
Vector3d position = new Vector3d(100.5, 64.0, 200.5);
SoundHelper.playSound3D(world, "SFX_Battleaxe_T1_Impact", position);
```

### 3D Sound with Volume and Pitch

```java
// Play with custom volume and pitch
SoundHelper.playSound3D(world, "SFX_Crystal_Break", position, 1.5f, 0.9f);
```

### 3D Sound at Block Position

```java
import com.hypixel.hytale.math.vector.Vector3i;

// Play sound at block center
Vector3i blockPos = new Vector3i(100, 64, 200);
SoundHelper.playSound3DAtBlock(world, "SFX_Wood_Build", blockPos);

// With custom volume and pitch
SoundHelper.playSound3DAtBlock(world, "SFX_Glass_Break", blockPos, 1.0f, 1.2f);
```

### 3D Sound at Entity

```java
// Play sound at entity's position
SoundHelper.playSound3DAtEntity(world, "SFX_Player_Hurt", player);

// With custom volume and pitch
SoundHelper.playSound3DAtEntity(world, "SFX_Cow_Death", entity, 1.0f, 0.8f);
```

### 3D Sound to Specific Player

```java
// Only this player hears the sound (if within range)
SoundHelper.playSound3DToPlayer(world, "Secret_Discovery", position, player);

// With custom volume and pitch
SoundHelper.playSound3DToPlayer(world, "Quest_Complete", position, player, 1.0f, 1.0f);
```

## API Reference

### 2D Sound Methods

#### `playSound2D(World, String)`
Play a 2D sound to all players.

**Parameters:**
- `world` - The world
- `soundEventId` - Sound event ID (see SoundList.md)

**Example:**
```java
SoundHelper.playSound2D(world, "SFX_Alchemy_Bench_Open");
```

---

#### `playSound2D(World, String, float, float)`
Play a 2D sound with custom volume and pitch.

**Parameters:**
- `world` - The world
- `soundEventId` - Sound event ID
- `volume` - Volume multiplier (1.0 = normal, 0.5 = half, 2.0 = double)
- `pitch` - Pitch multiplier (1.0 = normal, 0.5 = lower, 2.0 = higher)

**Example:**
```java
SoundHelper.playSound2D(world, "SFX_Creative_Play_Error", 0.8f, 1.2f);
```

---

#### `playSound2D(World, String, SoundCategory, float, float)`
Play a 2D sound with custom category, volume, and pitch.

**Parameters:**
- `world` - The world
- `soundEventId` - Sound event ID
- `category` - Sound category (SFX, MUSIC, AMBIENT, VOICE, MASTER)
- `volume` - Volume multiplier
- `pitch` - Pitch multiplier

**Example:**
```java
SoundHelper.playSound2D(world, "Music_Combat", SoundCategory.MUSIC, 1.0f, 1.0f);
```

---

#### `playSound2DToPlayer(World, String, Entity)`
Play a 2D sound to a specific player.

**Parameters:**
- `world` - The world
- `soundEventId` - Sound event ID
- `player` - The player entity

**Example:**
```java
SoundHelper.playSound2DToPlayer(world, "UI_Success", player);
```

---

#### `playSound2DToPlayer(World, String, Entity, float, float)`
Play a 2D sound to a specific player with custom volume and pitch.

**Parameters:**
- `world` - The world
- `soundEventId` - Sound event ID
- `player` - The player entity
- `volume` - Volume multiplier
- `pitch` - Pitch multiplier

**Example:**
```java
SoundHelper.playSound2DToPlayer(world, "SFX_Coins_Land", player, 0.7f, 1.1f);
```

---

### 3D Sound Methods

#### `playSound3D(World, String, Vector3d)`
Play a 3D sound at a position.

**Parameters:**
- `world` - The world
- `soundEventId` - Sound event ID
- `position` - World position

**Example:**
```java
SoundHelper.playSound3D(world, "SFX_Battleaxe_T1_Impact", new Vector3d(100, 64, 200));
```

---

#### `playSound3D(World, String, Vector3d, float, float)`
Play a 3D sound with custom volume and pitch.

**Parameters:**
- `world` - The world
- `soundEventId` - Sound event ID
- `position` - World position
- `volume` - Volume multiplier
- `pitch` - Pitch multiplier

**Example:**
```java
SoundHelper.playSound3D(world, "SFX_Battleaxe_T2_Impact", position, 2.0f, 0.9f);
```

---

#### `playSound3DAtBlock(World, String, Vector3i)`
Play a 3D sound at a block position (centered).

**Parameters:**
- `world` - The world
- `soundEventId` - Sound event ID
- `blockPosition` - Block position

**Example:**
```java
SoundHelper.playSound3DAtBlock(world, "SFX_Stone_Break", blockPos);
```

---

#### `playSound3DAtEntity(World, String, Entity)`
Play a 3D sound at an entity's position.

**Parameters:**
- `world` - The world
- `soundEventId` - Sound event ID
- `entity` - The entity

**Example:**
```java
SoundHelper.playSound3DAtEntity(world, "Player_Hurt", player);
```

---

#### `playSound3DToPlayer(World, String, Vector3d, Entity)`
Play a 3D sound to a specific player only.

**Parameters:**
- `world` - The world
- `soundEventId` - Sound event ID
- `position` - World position
- `player` - The player who should hear it

**Example:**
```java
SoundHelper.playSound3DToPlayer(world, "SFX_Coins_Land", position, player);
```

## Practical Examples

### Block Break Sound

```java
EcsEventHelper.onBlockBreak(world, (position, blockTypeId, playerEntity) -> {
 // Play break sound at block position
 SoundHelper.playSound3DAtBlock(world, "SFX_Stone_Break", position);
});
```

### Player Damage Sound

```java
EcsEventHelper.onPlayerDamaged(world, (player, damageAmount, damageSource) -> {
 // Play hurt sound at player position
 SoundHelper.playSound3DAtEntity(world, "SFX_Player_Hurt", player);
 
 // Play different pitch based on damage
 float pitch = 1.0f + (damageAmount / 20.0f); // Higher pitch for more damage
 SoundHelper.playSound3DAtEntity(world, "SFX_Player_Hurt", player, 1.0f, pitch);
});
```

### UI Feedback

```java
// Success sound only to the player who completed the action
SoundHelper.playSound2DToPlayer(world, "SFX_Axe_Special_Impact", player);

// Error sound to all players
SoundHelper.playSound2D(world, "SFX_Creative_Play_Error");
```

### Explosion with Varying Volume

```java
public void createExplosion(World world, Vector3d position, float power) {
 // Volume based on explosion power
 float volume = Math.min(power / 10.0f, 2.0f);
 
 SoundHelper.playSound3D(world, "SFX_Battleaxe_T2_Impact", position, volume, 1.0f);
}
```

### Footstep Sounds

```java
WorldHelper.onTickInterval(world, 10, tick -> {
 for (Entity player : world.getPlayers()) {
 if (isPlayerMoving(player)) {
 // Random pitch variation for natural footsteps
 float pitch = 0.9f + (float)(Math.random() * 0.2f);
 SoundHelper.playSound3DAtEntity(world, "SFX_Player_Walk_Stone", player, 0.5f, pitch);
 }
 }
});
```

### Ambient Sound Loop

```java
// Play ambient sound at a location
Vector3d campfirePos = new Vector3d(100, 64, 200);

WorldHelper.onTickInterval(world, 100, tick -> {
 // Play ambient sound every 5 seconds
 SoundHelper.playSound3D(world, "SFX_Cocoon_Active", campfirePos, 0.8f, 1.0f);
});
```

### Music System

```java
import com.hypixel.hytale.protocol.SoundCategory;

public void playBackgroundMusic(World world, String musicId) {
 // Play music to all players
 SoundHelper.playSound2D(world, musicId, SoundCategory.MUSIC, 1.0f, 1.0f);
}

// Usage
playBackgroundMusic(world, "Music_Exploration");
```

### Proximity-Based Sound

```java
public void playProximitySound(World world, Vector3d source, Entity listener, String soundId) {
 // Calculate distance
 TransformComponent transform = ComponentHelper.getComponent(
 world.getEntityStore().getStore(),
 listener.getReference(),
 TransformComponent.getComponentType()
 );
 
 if (transform != null) {
 double distance = transform.getPosition().distanceTo(source);
 
 // Adjust volume based on distance
 float volume = Math.max(0.1f, 1.0f - (float)(distance / 50.0));
 
 SoundHelper.playSound3DToPlayer(world, soundId, source, listener, volume, 1.0f);
 }
}
```

### Random Sound Variation

```java
public void playRandomSound(World world, Vector3d position, String[] soundIds) {
 // Pick random sound from array
 String soundId = soundIds[(int)(Math.random() * soundIds.length)];
 
 // Random pitch variation
 float pitch = 0.8f + (float)(Math.random() * 0.4f);
 
 SoundHelper.playSound3D(world, soundId, position, 1.0f, pitch);
}

// Usage
String[] hitSounds = {"SFX_Sword_T1_Impact", "SFX_Axe_Iron_Impact", "SFX_Club_Steel_Impact"};
playRandomSound(world, position, hitSounds);
```

## Tips & Best Practices

1. **Use appropriate categories** - Respect player volume settings by using correct categories
2. **Volume control** - Keep volumes reasonable (0.5-1.5 range for most sounds)
3. **Pitch variation** - Add slight random pitch (±10%) for natural sound variation
4. **Distance matters** - 3D sounds automatically attenuate with distance
5. **Performance** - Don't spam sounds every tick; use intervals for repeating sounds
6. **Player-specific sounds** - Use `playSound2DToPlayer` for UI feedback only that player should hear
7. **Test sound IDs** - Check SoundList.md for available sounds before using

## Volume and Pitch Guidelines

### Volume Multipliers
- `0.0` - Silent
- `0.5` - Half volume (quiet sounds)
- `1.0` - Normal volume (default)
- `1.5` - 50% louder
- `2.0` - Double volume (very loud)

### Pitch Multipliers
- `0.5` - One octave lower
- `0.8` - Slightly lower
- `1.0` - Normal pitch (default)
- `1.2` - Slightly higher
- `2.0` - One octave higher

## Related Helpers

- [ParticleHelper](ParticleHelper.md) - For visual particle effects
- [EcsEventHelper](EcsEventHelper.md) - For triggering sounds on game events
- [WorldHelper](WorldHelper.md) - For scheduling repeating sounds

## See Also

- [SoundList.md](SoundList.md) - Complete list of all sound event IDs
