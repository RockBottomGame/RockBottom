package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileGlowOre;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class GlowOreTileRenderer implements ITileRenderer<TileGlowOre>{

    private final IResourceName textureBase;
    private final IResourceName textureGlow;

    public GlowOreTileRenderer(IResourceName texture){
        IResourceName pref = texture.addPrefix("tiles.");
        this.textureBase = pref.addSuffix(".base");
        this.textureGlow = pref.addSuffix(".glow");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, TileGlowOre tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, Color[] light){
        manager.getTexture(this.textureBase).drawWithLight(renderX, renderY, scale, scale, light);
        manager.getTexture(this.textureGlow).draw(renderX, renderY, scale, scale);
    }

    @Override
    public void renderItem(IGameInstance game, IAssetManager manager, Graphics g, TileGlowOre tile, int meta, float x, float y, float scale, Color filter){
        manager.getTexture(this.textureBase).draw(x, y, scale, scale, filter);
        manager.getTexture(this.textureGlow).draw(x, y, scale, scale);
    }

    @Override
    public Image getParticleTexture(IGameInstance game, IAssetManager manager, Graphics g, TileGlowOre tile, TileState state){
        return manager.getTexture(this.textureBase);
    }
}
