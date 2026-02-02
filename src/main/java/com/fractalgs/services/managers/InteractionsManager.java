package com.fractalgs.services.managers;

import com.fractalgs.services.events.EscapeButtonEvent;
import com.fractalgs.services.events.FlyEvent;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Objects;

public class InteractionsManager extends SimpleInstantInteraction {

    public static final BuilderCodec<InteractionsManager> CODEC =
            BuilderCodec.builder(InteractionsManager.class, InteractionsManager::new, SimpleInstantInteraction.CODEC).build();

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {

        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();

        if (Objects.isNull(commandBuffer)) {

            interactionContext.getState().state = InteractionState.Failed;

            return;
        }

        Ref<EntityStore> ref = interactionContext.getEntity();

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());

        if (Objects.isNull(player)) {

            interactionContext.getState().state = InteractionState.Failed;

            return;
        }

        ItemStack held = interactionContext.getHeldItem();

        assert held != null;
        EscapeButtonEvent.run(player, held.getItemId());
        FlyEvent.run(player, held.getItemId());
    }

}

