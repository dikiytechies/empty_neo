package net.minecraft.client.gui.font;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record GlyphRenderTypes(RenderType normal, RenderType seeThrough, RenderType polygonOffset, RenderType normalBlur, RenderType seeThroughBlur, RenderType polygonOffsetBlur) {
    /** @deprecated Neo: Use {@link GlyphRenderTypes(RenderType,RenderType,RenderType,RenderType,RenderType,RenderType)} instead */
    @Deprecated
    public GlyphRenderTypes(RenderType normal, RenderType seeThrough, RenderType polygonOffset) {
        this(normal, seeThrough, polygonOffset, normal, seeThrough, polygonOffset);
    }

    public static GlyphRenderTypes createForIntensityTexture(ResourceLocation p_285411_) {
        return new GlyphRenderTypes(
            RenderType.textIntensity(p_285411_), RenderType.textIntensitySeeThrough(p_285411_), RenderType.textIntensityPolygonOffset(p_285411_)
            , net.neoforged.neoforge.client.NeoForgeRenderTypes.getTextIntensityFiltered(p_285411_),
            net.neoforged.neoforge.client.NeoForgeRenderTypes.getTextIntensitySeeThroughFiltered(p_285411_),
            net.neoforged.neoforge.client.NeoForgeRenderTypes.getTextIntensityPolygonOffsetFiltered(p_285411_)
        );
    }

    public static GlyphRenderTypes createForColorTexture(ResourceLocation p_285486_) {
        return new GlyphRenderTypes(RenderType.text(p_285486_), RenderType.textSeeThrough(p_285486_), RenderType.textPolygonOffset(p_285486_),
                net.neoforged.neoforge.client.NeoForgeRenderTypes.getTextFiltered(p_285486_),
                net.neoforged.neoforge.client.NeoForgeRenderTypes.getTextSeeThroughFiltered(p_285486_),
                net.neoforged.neoforge.client.NeoForgeRenderTypes.getTextPolygonOffsetFiltered(p_285486_)
        );
    }

    public RenderType select(Font.DisplayMode p_285259_) {
        return this.select(p_285259_, false);
    }

    /**
     * Neo: returns the {@link RenderType} to use for the given {@link Font.DisplayMode} and blur setting
     */
    public RenderType select(Font.DisplayMode p_285259_, boolean blur) {
        return switch (p_285259_) {
            case NORMAL -> blur ? this.normalBlur : this.normal;
            case SEE_THROUGH -> blur ? this.seeThroughBlur : this.seeThrough;
            case POLYGON_OFFSET -> blur ? this.polygonOffsetBlur : this.polygonOffset;
        };
    }
}
