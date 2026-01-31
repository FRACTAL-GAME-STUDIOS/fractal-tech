package com.fractalgs;

import com.fractalgs.services.events.NoDamageEvent;
import com.fractalgs.services.managers.ChestManager;
import com.fractalgs.services.managers.LegsManager;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    public void setup() {

        registerEvents();

        new ChestManager().register(this);
        new LegsManager().register(this);

    }

    private void registerEvents() {

        try {

            EntityStore.REGISTRY.registerSystem(new NoDamageEvent());

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }

    }
}