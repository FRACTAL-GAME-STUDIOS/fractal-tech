package com.fractalgs.utils;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Objects;
import java.util.logging.Level;

public class ArmorUtils {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static void applyThorns(Damage originalEvent, Player victim, CommandBuffer<EntityStore> commandBuffer, int armorTier) {

        if (armorTier < 2)
            return;

        Damage.Source source = originalEvent.getSource();

        if (source instanceof Damage.EntitySource entitySource) {

            Ref<EntityStore> attackerRef = entitySource.getRef();

            if (attackerRef.isValid()) {

                if (Objects.equals(attackerRef, victim.getReference()))
                    return;

                float reflectedAmount = originalEvent.getAmount() * 0.25f;

                if (reflectedAmount < 0.5f)
                    return;

                Damage.EntitySource thornsSource = new Damage.EntitySource(
                        Objects.requireNonNull(victim.getReference()));

                Damage thornsDamage = new Damage(thornsSource, originalEvent.getDamageCauseIndex(), reflectedAmount);

                DamageSystems.executeDamage(attackerRef, commandBuffer, thornsDamage);
            }
        }
    }

    public static MovementManager getMovementManager(Player player) {

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

}
