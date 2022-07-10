package net.untitledcreaturemod.toad;

import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ToadRenderer extends GeoEntityRenderer<ToadEntity> {
    public ToadRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ToadModel());
        this.shadowRadius = 0.3f;
    }
}
