package com.fractalgs.services.events;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemTranslationProperties;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import com.hypixel.hytale.server.core.util.TargetUtil;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class LookAtBlockService extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final AtomicBoolean ownershipReady = new AtomicBoolean(false);

    public record LookData(
            String materialId,
            Message materialName,
            String modOwner,
            String iconItemId
    ) {}

    private static final LookData NONE = new LookData(null, null, null, null);

    private final Map<Ref<EntityStore>, LookData> lastByPlayer = new ConcurrentHashMap<>();
    private final Map<String, String> ownerByBlockId = new ConcurrentHashMap<>();
    private final Map<String, Boolean> vanillaByBlockId = new ConcurrentHashMap<>();

    @Nonnull
    private final Query<EntityStore> query = Query.and(
            Player.getComponentType(),
            PlayerRef.getComponentType()
    );

    private final double maxDistance;

    public LookAtBlockService(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void register(JavaPlugin plugin) {
        rebuildOwnershipMap();
        plugin.getEntityStoreRegistry().registerSystem(this);
    }

    public LookData getLastLook(Player player) {
        if (player == null) return NONE;
        return lastByPlayer.getOrDefault(player.getReference(), NONE);
    }

    public void clear(Player player) {
        if (player == null) return;
        lastByPlayer.remove(player.getReference());
    }

    private static Message getBlockDisplayName(BlockType blockType) {
        if (blockType == null) return Message.raw("Unknown");

        Item item = blockType.getItem();
        if (item == null) return Message.raw(blockType.getId());

        ItemTranslationProperties tp = item.getTranslationProperties();
        String key = (tp != null) ? tp.getName() : null;

        if (key == null || key.isBlank()) return Message.raw(blockType.getId());
        return Message.translation(key);
    }

    private static String toTitleToken(String s) {
        if (s == null || s.isBlank()) return "Unknown";
        String t = s.trim();
        if (t.length() == 1) return t.toUpperCase();
        return Character.toUpperCase(t.charAt(0)) + t.substring(1).toLowerCase();
    }

    private static String formatOwnerDisplay(String group, String name) {
        return toTitleToken(group) + ":" + toTitleToken(name);
    }

    public final void rebuildOwnershipMap() {
        ownerByBlockId.clear();
        vanillaByBlockId.clear();

        var blockMap = BlockType.getAssetMap();

        for (AssetPack pack : AssetModule.get().getAssetPacks()) {
            var mf = pack.getManifest();

            String group = (mf != null && mf.getGroup() != null) ? mf.getGroup() : "unknown";
            String name  = (mf != null && mf.getName()  != null) ? mf.getName()  : pack.getName();

            boolean isVanilla =
                    "hytale".equalsIgnoreCase(group) ||
                            "hytale".equalsIgnoreCase(name) ||
                            "hytale".equalsIgnoreCase(pack.getName());

            String finalOwner = isVanilla ? "Hytale:Hytale" : formatOwnerDisplay(group, name);

            String packKeyA = pack.getName();
            String packKeyB = name;

            Set<String> idsA = blockMap.getKeysForPack(packKeyA);
            if (idsA != null) {
                for (String blockId : idsA) {
                    ownerByBlockId.put(blockId, finalOwner);
                    vanillaByBlockId.put(blockId, isVanilla);
                }
            }

            if (packKeyB != null && !packKeyB.equals(packKeyA)) {
                Set<String> idsB = blockMap.getKeysForPack(packKeyB);
                if (idsB != null) {
                    for (String blockId : idsB) {
                        ownerByBlockId.put(blockId, finalOwner);
                        vanillaByBlockId.put(blockId, isVanilla);
                    }
                }
            }
        }

        ownershipReady.set(true);
    }

    public String getOwner(String blockId) {
        return ownerByBlockId.getOrDefault(blockId, "Unknown:Unknown");
    }

    public boolean isVanillaBlock(String blockId) {
        return vanillaByBlockId.getOrDefault(blockId, false);
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return query;
    }

    @Override
    public void tick(float dt, int index,
                     @Nonnull ArchetypeChunk<EntityStore> chunk,
                     @Nonnull Store<EntityStore> store,
                     @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        Holder<EntityStore> holder = EntityUtils.toHolder(index, chunk);

        Player player = holder.getComponent(Player.getComponentType());
        PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (player == null || playerRef == null) return;

        if (!ownershipReady.get() || ownerByBlockId.isEmpty()) {
            rebuildOwnershipMap();
        }

        try {
            World world = store.getExternalData().getWorld();
            if (world == null) {
                lastByPlayer.put(player.getReference(), NONE);
                return;
            }

            Vector3i hit = TargetUtil.getTargetBlock(chunk.getReferenceTo(index), maxDistance, commandBuffer);
            if (hit == null) {
                lastByPlayer.put(player.getReference(), NONE);
                return;
            }

            WorldChunk hitChunk = world.getChunkIfLoaded(ChunkUtil.indexChunkFromBlock(hit.x, hit.z));
            if (hitChunk == null) {
                lastByPlayer.put(player.getReference(), NONE);
                return;
            }

            Vector3i base = resolveBaseBlock(hitChunk, hit);

            WorldChunk baseChunk = world.getChunkIfLoaded(ChunkUtil.indexChunkFromBlock(base.x, base.z));
            if (baseChunk == null) {
                lastByPlayer.put(player.getReference(), NONE);
                return;
            }

            BlockType block = baseChunk.getBlockType(base.x, base.y, base.z);
            if (block == null) {
                lastByPlayer.put(player.getReference(), NONE);
                return;
            }

            String materialId = block.getId();
            if (materialId == null || materialId.isBlank() || "Empty".equals(materialId)) {
                lastByPlayer.put(player.getReference(), NONE);
                return;
            }

            Message materialName = getBlockDisplayName(block);

            String owner = ownerByBlockId.get(materialId);

            if (owner == null || owner.isBlank() || "unknown:unknown".equalsIgnoreCase(owner)) {
                if (!materialId.contains(":")) {
                    owner = "Hytale:Hytale";
                } else {
                    owner = "Unknown:Unknown";
                }
            }

            Item item = block.getItem();
            String iconItemId = (item != null) ? item.getId() : null;

            lastByPlayer.put(player.getReference(),
                    new LookData(materialId, materialName, owner, iconItemId));

        } catch (Exception e) {
            lastByPlayer.put(player.getReference(), NONE);
        }
    }

    private static Vector3i resolveBaseBlock(WorldChunk chunk, Vector3i p) {
        int filler = chunk.getFiller(p.x, p.y, p.z);
        if (filler == 0) return p;

        return new Vector3i(
                p.x - FillerBlockUtil.unpackX(filler),
                p.y - FillerBlockUtil.unpackY(filler),
                p.z - FillerBlockUtil.unpackZ(filler)
        );
    }
}