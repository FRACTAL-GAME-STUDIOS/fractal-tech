package com.fractalgs.services.ui;

import com.fractalgs.services.ui.providers.HelmetHudData;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public final class HelmetTelemetryHud extends CustomUIHud {

    private static final String UI_PATH = "Hud/HelmetTelemetry.ui";

    private HelmetHudData data = new HelmetHudData();

    public HelmetTelemetryHud(PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(UICommandBuilder ui) {
        ui.append(UI_PATH);
        apply(ui);
    }

    public void setData(HelmetHudData data) {
        this.data = data != null ? data : new HelmetHudData();

        UICommandBuilder ui = new UICommandBuilder();
        apply(ui);
        update(false, ui);
        show();
    }

    private void apply(UICommandBuilder ui) {
        ui.set("#BlockIcon.ItemId", data.getIconItemId());
        ui.set("#BlockName.TextSpans", data.getBlockName());
        ui.set("#Owner.TextSpans", com.hypixel.hytale.server.core.Message.raw(data.getOwner()));
        ui.set("#Coords.TextSpans", com.hypixel.hytale.server.core.Message.raw(data.getCoords()));
        ui.set("#Biome.TextSpans", com.hypixel.hytale.server.core.Message.raw(data.getBiome()));
        ui.set("#Zone.TextSpans", com.hypixel.hytale.server.core.Message.raw(data.getZone()));
    }
}