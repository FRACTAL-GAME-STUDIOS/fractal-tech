package com.fractalgs;

import com.fractalgs.services.events.NoDamageEvent;
import com.fractalgs.services.managers.*;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private OreGenerationManager oreGenerationManager;

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    public void setup() {

        registerEvents();

        new HeadManager().register(this);
        new ChestManager().register(this);
        new LegsManager().register(this);
        new HandsManager().register(this);

        this.oreGenerationManager = new OreGenerationManager();
        this.oreGenerationManager.register(this);

        this.getCodecRegistry(Interaction.CODEC).register("fractal_interaction_hook", InteractionsManager.class, InteractionsManager.CODEC);
    }

    @Override
    protected void shutdown() {

        if (Objects.nonNull(this.oreGenerationManager))
            this.oreGenerationManager.shutdown();
    }

    private void registerEvents() {

        try {

            EntityStore.REGISTRY.registerSystem(new NoDamageEvent());

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }

    }
}