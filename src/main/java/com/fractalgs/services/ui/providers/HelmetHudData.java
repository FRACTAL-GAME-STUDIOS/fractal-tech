package com.fractalgs.services.ui.providers;

import com.hypixel.hytale.server.core.Message;

public final class HelmetHudData {

    private Message blockName = Message.raw("");
    private String owner = "";
    private String coords = "";
    private String zoneBiome = "";
    private String iconItemId = "Empty";

    public Message getBlockName() { return blockName; }
    public String getOwner() { return owner; }
    public String getCoords() { return coords; }
    private String biome = "";
    private String zone = "";

    public String getBiome() { return biome; }
    public String getZone() { return zone; }

    public void setBiome(String biome) { this.biome = biome != null ? biome : ""; }
    public void setZone(String zone) { this.zone = zone != null ? zone : ""; }

    public String getIconItemId() { return iconItemId; }

    public void setBlockName(Message blockName) {
        this.blockName = blockName != null ? blockName : Message.raw("");
    }

    public void setOwner(String owner) {
        this.owner = owner != null ? owner : "";
    }

    public void setCoords(String coords) {
        this.coords = coords != null ? coords : "";
    }

    public void setZoneBiome(String zoneBiome) {
        this.zoneBiome = zoneBiome != null ? zoneBiome : "";
    }

    public void setIconItemId(String iconItemId) {
        this.iconItemId = (iconItemId == null || iconItemId.isBlank()) ? "Empty" : iconItemId;
    }
}