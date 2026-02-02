package com.fractalgs.services.managers;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Objects;
import java.util.logging.Level;

public class HeadManager {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String HEAD_ID = "Armor_Copper_Head";

    public void register(JavaPlugin plugin) {}

    public static void applyHeadThorns(Damage originalEvent, Player victim, CommandBuffer<EntityStore> commandBuffer) {

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

    public static boolean isWearingHead(Player player) {

        try {

            if (Objects.isNull(player.getInventory())
                    || Objects.isNull(player.getInventory().getArmor()))
                return false;

            ItemContainer armor = player.getInventory().getArmor();

            for (int i = 0; i < armor.getCapacity(); i++) {

                ItemStack stack = armor.getItemStack((short) i);

                if (Objects.nonNull(stack)
                        && HEAD_ID.equals(stack.getItemId()))
                    return true;
            }

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

            return false;
        }

        return false;
    }
}