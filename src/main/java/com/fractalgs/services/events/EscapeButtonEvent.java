package com.fractalgs.services.events;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class EscapeButtonEvent {

    private static final String ESCAPE_BUTTON = "Ancient_Escape";

    private static final int TELEPORT_RADIUS = 1000;

    public static void run(@Nonnull Player player, @Nonnull String itemId) {

        if (itemId.equals(ESCAPE_BUTTON))
            randomTeleport(player);
    }

    private static void randomTeleport(@Nonnull Player player) {

        World world = player.getWorld();

        if (Objects.isNull(world)
                || Objects.isNull(player.getReference()))
            return;

        Vector3d base = readPosition(player);

        if (Objects.isNull(base)) base = new Vector3d(0, 80, 0);

        int baseX = (int) Math.round(base.x);
        int baseY = (int) Math.round(base.y);
        int baseZ = (int) Math.round(base.z);

        int tries = 1000;

        world.execute(() -> {

            for (int i = 0; i < tries; i++) {

                int x = baseX + ThreadLocalRandom.current().nextInt(-TELEPORT_RADIUS, TELEPORT_RADIUS + 1);
                int z = baseZ + ThreadLocalRandom.current().nextInt(-TELEPORT_RADIUS, TELEPORT_RADIUS + 1);

                Integer safeY = findSafeCoordY(world, x, baseY, z);

                if (Objects.isNull(safeY))
                    continue;

                teleportPlayer(player, world, x, safeY, z);

                return;
            }

        });
    }

    private static void teleportPlayer(@Nonnull Player player, @Nonnull World world, int x, int y, int z) {

        if (Objects.isNull(player.getReference()))
            return;

        Store<EntityStore> store = player.getReference().getStore();

        Teleport teleport = Teleport.createForPlayer(world, new Vector3d(x, y, z), new Vector3f(0, 0, 0));

        store.addComponent(player.getReference(), Teleport.getComponentType(), teleport);
    }

    private static Integer findSafeCoordY(@Nonnull World world, int x, int y, int z) {

        int minY = 1;
        int maxY = 255;

        int start = clamp(y + 64, minY + 2, maxY - 2);
        int end = clamp(y - 128, minY + 2, maxY - 2);

        for (int coordY = start; coordY >= end; coordY--) {

            BlockType below = world.getBlockType(x, coordY - 1, z);
            BlockType body = world.getBlockType(x, coordY, z);
            BlockType head = world.getBlockType(x, coordY + 1, z);

            if (below == BlockType.UNKNOWN
                    || body == BlockType.UNKNOWN
                    || head == BlockType.UNKNOWN)
                return null;

            if (isSolid(below)
                    && isAir(body)
                    && isAir(head))
                return coordY;
        }

        return null;
    }

    private static boolean isAir(BlockType blockType) {
        return blockType == BlockType.EMPTY;
    }

    private static boolean isSolid(BlockType blockType) {
        return blockType != BlockType.EMPTY && blockType != BlockType.UNKNOWN;
    }

    private static int clamp(int input, int lowerBound, int upperBound) {
        return Math.max(lowerBound, Math.min(upperBound, input));
    }

    private static Vector3d readPosition(@Nonnull Player player) {

        try {

            TransformComponent transform = player.getTransformComponent();

            if (Objects.isNull(transform))
                return null;

            return transform.getPosition();

        } catch (Throwable ignored) {

            return null;

        }
    }

}
