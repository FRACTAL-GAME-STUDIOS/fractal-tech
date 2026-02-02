package com.fractalgs.services.managers;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;

import java.util.Objects;

public class HeadManager {

    private static final String HEAD_ID_TIER_1 = "Lost_Head";
    private static final String HEAD_ID_TIER_2 = "Old_Head";
    private static final String HEAD_ID_TIER_3 = "Ancient_Head";

    public void register(JavaPlugin plugin) {}

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
                        case HEAD_ID_TIER_3 -> {
                            return 3;
                        }
                        case HEAD_ID_TIER_2 -> {
                            return 2;
                        }
                        case HEAD_ID_TIER_1 -> {
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