package com.fractalgs.services.managers;

import com.fractalgs.utils.api.WorldHelper;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.component.DynamicLight;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentDynamicLight;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Objects;
import java.util.logging.Level;

public class ChestManager {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String CHEST_ID = "Armor_Copper_Chest";

    public void register(JavaPlugin plugin) {

        plugin.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {

            Holder<EntityStore> holder = event.getHolder();

            Player player = holder.getComponent(Player.getComponentType());

            if (Objects.nonNull(player))
                WorldHelper.waitTicks(event.getWorld(), 10, () ->
                        checkPlayer(player));
        });

        plugin.getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, event -> {

            if (event.getEntity() instanceof Player player) {

                if (Objects.equals(event.getItemContainer(), player.getInventory().getArmor()))
                    WorldHelper.waitTicks(event.getEntity().getWorld(), 1, () ->
                            checkPlayer(player));
            }
        });
    }

    private void checkPlayer(Player player) {

        try {

            if (isWearingChest(player)) {

                applyLighting(player);

            } else {

                removeLighting(player);

            }

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }
    }

    private void applyLighting(Player player) {

        try {

            if (Objects.isNull(player.getWorld()))
                return;

            Store<EntityStore> store = player.getWorld().getEntityStore().getStore();
            Ref<EntityStore> ref = player.getReference();

            boolean isWearing = isWearingChest(player);

            ColorLight targetColor = getColorLight(isWearing);

            assert ref != null;
            PersistentDynamicLight currentLightComp = (store.getComponent(ref, PersistentDynamicLight.getComponentType()));

            ColorLight currentColor = Objects.nonNull(currentLightComp)
                    ? currentLightComp.getColorLight()
                    : null;

            if (Objects.nonNull(currentLightComp)
                    && Objects.equals(currentColor, targetColor))
                return;

            if (Objects.isNull(currentLightComp) && !isWearing)
                return;

            PersistentDynamicLight newLightComp = new PersistentDynamicLight(targetColor);

            store.putComponent(ref, PersistentDynamicLight.getComponentType(), newLightComp);

            DynamicLight dynamicLight = store.getComponent(ref, DynamicLight.getComponentType());

            if (Objects.isNull(dynamicLight)) {

                dynamicLight = new DynamicLight(targetColor);

                store.putComponent(ref, DynamicLight.getComponentType(), dynamicLight);

            } else {

                dynamicLight.setColorLight(targetColor);

            }

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }
    }

    private void removeLighting(Player player) {

        try {

            if (Objects.isNull(player.getWorld()))
                return;

            Store<EntityStore> store = player.getWorld().getEntityStore().getStore();
            Ref<EntityStore> ref = player.getReference();

            assert ref != null;
            if (Objects.nonNull(store.getComponent(ref, PersistentDynamicLight.getComponentType())))
                store.removeComponent(ref, PersistentDynamicLight.getComponentType());

            if (Objects.nonNull(store.getComponent(ref, DynamicLight.getComponentType())))
                store.removeComponent(ref, DynamicLight.getComponentType());

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }
    }

    private static ColorLight getColorLight(boolean isWearing) {

        ColorLight targetColor;

        if (isWearing) {

            byte radius = 10;

            byte red = 0;
            byte green = 15;
            byte blue = 15;

            targetColor = new ColorLight(radius, red, green, blue);

        } else {

            targetColor = new ColorLight((byte) 0, (byte) 0, (byte) 0, (byte) 0);

        }

        return targetColor;
    }

    public static void applyThorns(Damage originalEvent, Player victim, CommandBuffer<EntityStore> commandBuffer) {

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

    public static boolean isWearingChest(Player player) {

        try {

            if (Objects.isNull(player.getInventory())
                    || Objects.isNull(player.getInventory().getArmor()))
                return false;

            ItemContainer armor = player.getInventory().getArmor();

            for (int i = 0; i < armor.getCapacity(); i++) {

                ItemStack stack = armor.getItemStack((short) i);

                if (Objects.nonNull(stack)
                        && CHEST_ID.equals(stack.getItemId()))
                    return true;
            }

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

            return false;
        }

        return false;
    }
}