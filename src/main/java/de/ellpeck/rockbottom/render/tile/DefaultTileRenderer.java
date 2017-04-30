package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class DefaultTileRenderer implements ITileRenderer{

    public final String texture;

    public DefaultTileRenderer(String texture){
        this.texture = "tiles."+texture;
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, Tile tile, int x, int y, float renderX, float renderY, Color filter){
        manager.getImage(this.texture).draw(renderX, renderY, 1F, 1F, filter);
    }

    @Override
    public void renderItem(RockBottom game, AssetManager manager, Graphics g, Tile tile, int meta, float x, float y, float scale, Color filter){
        manager.getImage(this.texture).draw(x, y, scale, scale, filter);
    }

    @Override
    public Image getParticleTexture(RockBottom game, AssetManager manager, Graphics g, Tile tile, int meta){
        return manager.getImage(this.texture);
    }
}
