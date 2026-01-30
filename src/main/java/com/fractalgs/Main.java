package com.fractalgs;

import com.fractalgs.services.managers.LegsManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import javax.annotation.Nonnull;

public class Main extends JavaPlugin {

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    public void setup() {

        LegsManager legsManager = new LegsManager();

        legsManager.register(this);
    }
}