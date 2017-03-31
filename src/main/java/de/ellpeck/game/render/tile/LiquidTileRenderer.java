package de.ellpeck.game.render.tile;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class LiquidTileRenderer extends DefaultTileRenderer{

    public LiquidTileRenderer(String texture){
        super(texture);
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g, IWorld world, Tile tile, int x, int y, float renderX, float renderY, Color filter){
        float f = (float)world.getMeta(x, y)/8F;
        manager.getImage(this.texture).draw(renderX, renderY+(1F-f), 1F, f, filter);
    }
}
