package com.fractalgs.interactions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;

import static java.util.Map.entry;

public final class FoodActions {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static final Map<String, Consumer<Player>> ACTIONS = Map.ofEntries(
            entry("Food_Cake", p -> randomTeleport(p, 1000, "")),
            entry("Food_Hotdog", p -> randomTeleport(p, 500, ""))
    );

    public static void run(@Nonnull Player player, @Nonnull String itemId) {
        Consumer<Player> action = ACTIONS.get(itemId);
        if (action == null) return;
        try {
            action.accept(player);
        } catch (Throwable t) {
            LOGGER.at(Level.WARNING).log("Food action failed itemId=" + itemId + " player=" + safeName(player) + " err=" + t);
        }
    }

    public static void randomTeleport(@Nonnull Player player, int radius, @Nonnull String message) {
        World world = player.getWorld();
        if (world == null || player.getReference() == null) return;

        Vector3d base = readPosition(player);
        if (base == null) base = new Vector3d(0, 80, 0);

        int baseX = (int) Math.round(base.x);
        int baseY = (int) Math.round(base.y);
        int baseZ = (int) Math.round(base.z);

        int tries = 12;

        world.execute(() -> {
            for (int i = 0; i < tries; i++) {
                int x = baseX + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
                int z = baseZ + ThreadLocalRandom.current().nextInt(-radius, radius + 1);

                Integer safeY = findSafeYInColumn(world, x, baseY, z, 64, 128);
                if (safeY == null) {
                    continue;
                }

                teleportPlayerNoExecute(player, world, x, safeY, z);
                sendMessage(player, message);
                return;
            }

            sendMessage(player, "No safe spot found.");
        });
    }

    private static void teleportPlayerNoExecute(@Nonnull Player player, @Nonnull World world, int x, int y, int z) {
        if (player.getReference() == null) return;
        Store<EntityStore> store = player.getReference().getStore();

        Teleport teleport = Teleport.createForPlayer(
                world,
                new Vector3d(x, y, z),
                new Vector3f(0, 0, 0)
        );

        store.addComponent(player.getReference(), Teleport.getComponentType(), teleport);
    }

    private static Integer findSafeYInColumn(@Nonnull World world, int x, int baseY, int z, int searchUp, int searchDown) {
        int minY = 1;
        int maxY = 255;

        int start = clamp(baseY + searchUp, minY + 2, maxY - 2);
        int end = clamp(baseY - searchDown, minY + 2, maxY - 2);

        for (int y = start; y >= end; y--) {
            BlockType below = world.getBlockType(x, y - 1, z);
            BlockType body  = world.getBlockType(x, y, z);
            BlockType head  = world.getBlockType(x, y + 1, z);

            if (below == BlockType.UNKNOWN || body == BlockType.UNKNOWN || head == BlockType.UNKNOWN) {
                return null;
            }

            if (isSolid(below) && isAir(body) && isAir(head)) {
                return y;
            }
        }

        return null;
    }

    private static boolean isAir(BlockType t) {
        return t == BlockType.EMPTY;
    }

    private static boolean isSolid(BlockType t) {
        return t != BlockType.EMPTY && t != BlockType.UNKNOWN;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static Vector3d readPosition(@Nonnull Player player) {
        try {
            var transform = player.getTransformComponent();
            if (transform == null) return null;
            return transform.getPosition();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Vector3f readRotation(@Nonnull Player player) {
        var t = player.getTransformComponent();
        return t != null ? t.getRotation() : null;
    }

    private static void sendMessage(@Nonnull Player player, @Nonnull String text) {
        player.sendMessage(Message.raw(text));
    }

    private static String safeName(@Nonnull Player player) {
        try {
            return String.valueOf(player.getDisplayName());
        } catch (Throwable t) {
            return "unknown";
        }
    }

    private FoodActions() {
    }
}
