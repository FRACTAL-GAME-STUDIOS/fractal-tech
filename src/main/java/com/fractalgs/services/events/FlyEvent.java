package com.fractalgs.services.events;

import com.fractalgs.services.managers.LegsManager;
import com.fractalgs.utils.ArmorUtils;
import com.fractalgs.utils.api.WorldHelper;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.protocol.SavedMovementStates;
import com.hypixel.hytale.protocol.packets.player.SetMovementStates;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class FlyEvent {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String ANTI_GRAVITY_TIER_1 = "Old_Gravityinverter";
    private static final String ANTI_GRAVITY_TIER_2 = "Ancient_Gravityinverter";

    private static final Map<UUID, Long> flightExpiryTime = new ConcurrentHashMap<>();

    private static final Set<UUID> activePlayersFlight = ConcurrentHashMap.newKeySet();

    public static void run(@Nonnull Player player, @Nonnull String itemId) {

        long durationMs;

        if (itemId.equals(ANTI_GRAVITY_TIER_1)) {

            durationMs = 30_000L;

        } else if (itemId.equals(ANTI_GRAVITY_TIER_2)) {

            durationMs = 60_000L;

        } else {

            return;

        }

        long newExpiryTime = System.currentTimeMillis() + durationMs;

        flightExpiryTime.put(player.getUuid(), newExpiryTime);

        applyFly(player);

        if (!activePlayersFlight.contains(player.getUuid())) {

            activePlayersFlight.add(player.getUuid());

            startRealTimeMonitor(player);

        }
    }

    private static void startRealTimeMonitor(Player player) {

        if (Objects.isNull(player)
                || !Objects.requireNonNull(player.getReference()).isValid()) {

            cleanup(player);

            return;
        }

        if (Objects.nonNull(player.getWorld())) {

            WorldHelper.waitTicks(player.getWorld(), 10, () ->
                    checkTime(player));

        } else {

            cleanup(player);

        }
    }

    private static void checkTime(Player player) {

        if (Objects.isNull(player)
                || !Objects.requireNonNull(player.getReference()).isValid()) {

            cleanup(player);

            return;
        }

        long expiry = flightExpiryTime.getOrDefault(player.getUuid(), 0L);
        long now = System.currentTimeMillis();

        if (now >= expiry) {

            removeFly(player);

            cleanup(player);

        } else {

            startRealTimeMonitor(player);

        }
    }

    private static void cleanup(Player player) {

        if (Objects.nonNull(player)) {

            activePlayersFlight.remove(player.getUuid());
            flightExpiryTime.remove(player.getUuid());

        }
    }

    private static void applyFly(Player player) {

        MovementManager movement = ArmorUtils.getMovementManager(player);

        if (Objects.nonNull(movement)) {

            movement.applyDefaultSettings();

            MovementSettings settings = movement.getSettings();
            settings.canFly = true;
            settings.horizontalFlySpeed = 15.0f;
            settings.verticalFlySpeed = 15.0f;

            int legsTier = LegsManager.getEquippedTier(player);

            if (legsTier > 0)
                LegsManager.applyStatsToSettings(settings, legsTier);

            movement.update(player.getPlayerConnection());

            player.getPlayerConnection().write(new SetMovementStates(new SavedMovementStates(true)));

        }
    }

    private static void removeFly(Player player) {

        MovementManager movement = ArmorUtils.getMovementManager(player);

        if (Objects.nonNull(movement)) {

            try {

                movement.applyDefaultSettings();

                MovementSettings settings = movement.getSettings();
                settings.canFly = false;
                settings.horizontalFlySpeed = 10.32F;
                settings.verticalFlySpeed = 10.32F;

                int legTier = LegsManager.getEquippedTier(player);

                if (legTier > 0)
                    LegsManager.applyStatsToSettings(settings, legTier);

                movement.update(player.getPlayerConnection());

                player.getPlayerConnection().write(new SetMovementStates(new SavedMovementStates(false)));

            } catch (Exception e) {

                LOGGER.at(Level.WARNING).log(e.getMessage());

            }
        }
    }
}