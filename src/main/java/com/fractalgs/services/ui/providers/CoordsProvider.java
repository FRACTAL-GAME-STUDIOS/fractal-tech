package com.fractalgs.services.ui.providers;

public final class CoordsProvider implements HelmetHudProvider {

    @Override
    public void populate(HelmetHudContext ctx, HelmetHudData out) {
        out.setCoords(String.format("X: %.2f  Y: %.2f  Z: %.2f", ctx.x(), ctx.y(), ctx.z()));
    }
}
