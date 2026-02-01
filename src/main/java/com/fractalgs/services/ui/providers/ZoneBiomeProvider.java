package com.fractalgs.services.ui.providers;

import com.fractalgs.services.ui.providers.HelmetHudContext;
import com.fractalgs.services.ui.providers.HelmetHudData;
import com.fractalgs.services.ui.providers.HelmetHudProvider;

public final class ZoneBiomeProvider implements HelmetHudProvider {

    @Override
    public void populate(HelmetHudContext ctx, HelmetHudData out) {
        String s = ctx.zoneAndBiome();
        if (s == null || s.isBlank()) {
            out.setBiome("N/A");
            out.setZone("N/A");
            return;
        }

        String[] parts = s.split("\\|");
        if (parts.length >= 2) {
            out.setBiome(parts[0].trim());
            out.setZone(parts[1].trim());
        } else {
            out.setBiome(s.trim());
            out.setZone("N/A");
        }
    }
}
