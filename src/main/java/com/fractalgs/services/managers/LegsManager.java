package com.fractalgs.services.managers;

import com.fractalgs.utils.ArmorUtils;
import com.fractalgs.utils.api.WorldHelper;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Objects;
import java.util.logging.Level;

public class LegsManager {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String LEGS_ID_TIER_1 = "Lost_Legs";
    private static final String LEGS_ID_TIER_2 = "Old_Legs";
    private static final String LEGS_ID_TIER_3 = "Ancient_Legs";

    private static final float SPEED_MULTIPLIER = 2.0f;
    private static final float JUMP_MULTIPLIER = 1.5f;

    public void register(JavaPlugin plugin) {

        plugin.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {

            Holder<EntityStore> holder = event.getHolder();

            Player player = holder.getComponent(Player.getComponentType());

            if (Objects.nonNull(player))
                WorldHelper.waitTicks(event.getWorld(), 10, () ->
                        checkPlayer(player));
        });

        plugin.getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, event -> {

            if (event.getEntity() instanceof Player player)
                if (Objects.equals(event.getItemContainer(), player.getInventory().getArmor()))
                    checkPlayer(player);
        });
    }

    private void checkPlayer(Player player) {

        try {

            int tier = getEquippedTier(player);

            if (tier >= 1) {

                applyPhysics(player, tier);

            } else {

                resetPhysics(player);

            }

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }
    }

    private void applyPhysics(Player player, int tier) {

        MovementManager movement = ArmorUtils.getMovementManager(player);

        if (Objects.nonNull(movement)) {

            movement.applyDefaultSettings();

            applyStatsToSettings(movement.getSettings(), tier);

            movement.update(player.getPlayerConnection());
        }
    }

    public static void applyStatsToSettings(MovementSettings settings, int tier) {

        if (tier >= 1) {

            settings.maxSpeedMultiplier *= SPEED_MULTIPLIER;
            settings.forwardSprintSpeedMultiplier *= SPEED_MULTIPLIER;

        }

        if (tier >= 3)
            settings.jumpForce *= JUMP_MULTIPLIER;
    }

    private void resetPhysics(Player player) {

        MovementManager movement = ArmorUtils.getMovementManager(player);

        if (Objects.nonNull(movement)) {

            movement.applyDefaultSettings();

            movement.update(player.getPlayerConnection());
        }
    }

    public static int getEquippedTier(Player player) {

        try {

            if (Objects.isNull(player.getInventory())
                    || Objects.isNull(player.getInventory().getArmor()))
                return 0;

            ItemContainer armor = player.getInventory().getArmor();

            for (int i = 0; i < armor.getCapacity(); i++) {

                ItemStack stack = armor.getItemStack((short) i);

                if (Objects.nonNull(stack)) {

                    String id = stack.getItemId();

                    switch (id) {
                        case LEGS_ID_TIER_3 -> {
                            return 3;
                        }
                        case LEGS_ID_TIER_2 -> {
                            return 2;
                        }
                        case LEGS_ID_TIER_1 -> {
                            return 1;
                        }
                    }

                }
            }

        } catch (Exception e) {

            return 0;

        }

        return 0;
    }
}