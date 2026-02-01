package com.fractalgs.services.ui.providers;

import com.fractalgs.services.events.LookAtBlockService;

public final class HelmetHudContext {

    private final double x;
    private final double y;
    private final double z;
    private final String zoneAndBiome;
    private final LookAtBlockService.LookData look;

    public HelmetHudContext(double x, double y, double z, String zoneAndBiome, LookAtBlockService.LookData look) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.zoneAndBiome = zoneAndBiome;
        this.look = look;
    }

    public double x() { return x; }
    public double y() { return y; }
    public double z() { return z; }
    public String zoneAndBiome() { return zoneAndBiome; }
    public LookAtBlockService.LookData look() { return look; }
}
