package com.fractalgs.services.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.fractalgs.services.managers.ChestManager.*;
import static com.fractalgs.services.managers.HeadManager.applyHeadThorns;
import static com.fractalgs.services.managers.HeadManager.isWearingHead;
import static com.fractalgs.services.managers.LegsManager.isWearingLegs;

public class NoDamageEvent extends EntityEventSystem<EntityStore, Damage> {

    private static final String FALL = "fall";
    private static final String FIRE = "fire";
    private static final String PHYSICAL = "physical";
    private static final String PROJECTILE = "projectile";
    private static final String DROWNING = "drowning";
    private static final String POISON = "poison";

    public NoDamageEvent() {
        super(Damage.class);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.first());
    }

    @Override
    public void handle(int index,
                       @Nonnull ArchetypeChunk<EntityStore> chunk,
                       @Nonnull Store<EntityStore> store,
                       @Nonnull CommandBuffer<EntityStore> commandBuffer,
                       @Nonnull Damage event) {

        DamageCause cause = event.getCause();

        if (Objects.nonNull(cause)) {

            String causeId = cause.getId().toLowerCase();

            Player player = chunk.getComponent(index, Player.getComponentType());

            if (Objects.nonNull(player)) {

                if (causeId.contains(FALL)
                        && isWearingLegs(player))
                    event.setCancelled(true);

                if (causeId.contains(FIRE)
                        && isWearingChest(player))
                    event.setCancelled(true);

                if ((causeId.contains(DROWNING) || causeId.contains(POISON))
                        && isWearingHead(player))
                    event.setCancelled(true);

                if (!event.isCancelled()) {

                    if (causeId.contains(PHYSICAL)
                            && isWearingChest(player))
                        applyChestThorns(event, player, commandBuffer);

                    if (causeId.contains(PROJECTILE)
                            && isWearingHead(player))
                        applyHeadThorns(event, player, commandBuffer);

                }
            }
        }
    }
}
