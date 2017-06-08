package de.ellpeck.rockbottom.game.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class LiquidTileRenderer extends DefaultTileRenderer{

    public LiquidTileRenderer(String texture){
        super(texture);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, Tile tile, int x, int y, float renderX, float renderY, Color filter){
        float f = (float)world.getMeta(x, y)/8F;
        manager.getImage(this.texture).draw(renderX, renderY+(1F-f), 1F, f, filter);
    }
}
