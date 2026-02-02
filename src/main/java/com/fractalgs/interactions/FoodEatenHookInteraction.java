package com.fractalgs.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public final class FoodEatenHookInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<FoodEatenHookInteraction> CODEC =
            BuilderCodec.builder(FoodEatenHookInteraction.class, FoodEatenHookInteraction::new, SimpleInstantInteraction.CODEC)
                    .build();

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType,
                            @Nonnull InteractionContext interactionContext,
                            @Nonnull CooldownHandler cooldownHandler) {

        LOGGER.atInfo().log("FoodEatenHookInteraction CALLED");

        var commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) {
            LOGGER.atInfo().log("CommandBuffer is null");
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        var ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        if (player == null) {
            LOGGER.atInfo().log("Player is null");
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        ItemStack held = interactionContext.getHeldItem();
        LOGGER.atInfo().log("Held item is: " + (held == null ? "null" : held.getItemId()));
        LOGGER.atInfo().log("EAT HOOK OK by " + player.getDisplayName());

        FoodActions.run(player, held.getItemId());
    }

    private void onFoodEaten(@Nonnull Player player, @Nonnull String itemId) {
        LOGGER.atInfo().log("Food eaten: " + itemId + " by " + player.getDisplayName());
    }
}
