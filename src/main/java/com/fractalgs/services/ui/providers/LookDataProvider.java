package com.fractalgs.services.ui.providers;

import com.fractalgs.services.events.LookAtBlockService;
import com.hypixel.hytale.server.core.Message;

public final class LookDataProvider implements HelmetHudProvider {

    @Override
    public void populate(HelmetHudContext ctx, HelmetHudData out) {
        LookAtBlockService.LookData look = ctx.look();

        if (look == null || look.materialId() == null || look.materialId().isBlank()) {
            out.setBlockName(Message.raw(""));
            out.setOwner("");
            out.setIconItemId("");
            return;
        }

        out.setBlockName(look.materialName());

        String owner = look.modOwner();
        if (owner == null || owner.isBlank()) owner = "unknown:unknown";

        String ownerNorm = owner.toLowerCase();
        if (ownerNorm.startsWith("hytale:")) ownerNorm = "hytale:hytale";

        out.setOwner(ownerNorm);

        String icon = look.iconItemId();
        if (icon != null && !icon.isBlank()) out.setIconItemId(icon);
        else out.setIconItemId(look.materialId());
    }
}
