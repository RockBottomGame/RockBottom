package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class LiquidTileRenderer extends DefaultTileRenderer{

    public LiquidTileRenderer(String texture){
        super(texture);
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, Tile tile, int x, int y, float renderX, float renderY, Color filter){
        float f = (float)world.getMeta(x, y)/8F;
        manager.getImage(this.texture).draw(renderX, renderY+(1F-f), 1F, f, filter);
    }
}
