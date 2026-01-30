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

import static com.fractalgs.services.managers.LegsManager.isWearingLegs;

public class NoDamageEvent extends EntityEventSystem<EntityStore, Damage> {

    private static final String FALL = "fall";

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

        if (Objects.nonNull(cause)
                && FALL.equalsIgnoreCase(cause.getId())) {

            Player player = chunk.getComponent(index, Player.getComponentType());

            if (Objects.nonNull(player)
                    && isWearingLegs(player))
                event.setCancelled(true);
        }
    }
}
