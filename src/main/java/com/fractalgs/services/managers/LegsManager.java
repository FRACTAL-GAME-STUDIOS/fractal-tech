package com.fractalgs.services.managers;

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

    private static final String LEGS_ID = "Armor_Copper_Legs";

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

            if (isWearingLegs(player)) {

                applyPhysics(player);

            } else {

                resetPhysics(player);

            }

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }
    }

    private void applyPhysics(Player player) {

        MovementManager movement = getMovementManager(player);

        if (Objects.nonNull(movement)) {

            if (movement.getSettings().forwardSprintSpeedMultiplier >= (SPEED_MULTIPLIER - 0.1f))
                return;

            movement.applyDefaultSettings();

            MovementSettings settings = movement.getSettings();
            settings.maxSpeedMultiplier *= SPEED_MULTIPLIER;
            settings.forwardSprintSpeedMultiplier *= SPEED_MULTIPLIER;
            settings.jumpForce *= JUMP_MULTIPLIER;

            movement.update(player.getPlayerConnection());
        }
    }

    private void resetPhysics(Player player) {

        MovementManager movement = getMovementManager(player);

        if (Objects.nonNull(movement)) {

            movement.applyDefaultSettings();

            movement.update(player.getPlayerConnection());
        }
    }

    private MovementManager getMovementManager(Player player) {

        try {

            if (Objects.isNull(player.getWorld()))
                return null;

            return player.getWorld().getEntityStore().getStore().getComponent(
                    Objects.requireNonNull(player.getReference()), MovementManager.getComponentType());

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

            return null;
        }
    }

    public static boolean isWearingLegs(Player player) {

        try {

            if (Objects.isNull(player.getInventory())
                    || Objects.isNull(player.getInventory().getArmor()))
                return false;

            ItemContainer armor = player.getInventory().getArmor();

            for (int i = 0; i < armor.getCapacity(); i++) {

                ItemStack stack = armor.getItemStack((short) i);

                if (Objects.nonNull(stack)
                        && LEGS_ID.equals(stack.getItemId()))
                    return true;
            }

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

            return false;
        }

        return false;
    }
}