package com.fractalgs.services.ui.providers;

import java.util.List;

public final class HelmetHudAssembler {

    private final List<HelmetHudProvider> providers;

    public HelmetHudAssembler(List<HelmetHudProvider> providers) {
        this.providers = providers;
    }

    public HelmetHudData build(HelmetHudContext ctx) {
        HelmetHudData out = new HelmetHudData();
        for (HelmetHudProvider p : providers) {
            p.populate(ctx, out);
        }
        return out;
    }
}
