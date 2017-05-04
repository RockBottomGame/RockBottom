package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class SeparatorTileRenderer extends DefaultTileRenderer{

    public SeparatorTileRenderer(String texture){
        super(texture);
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, Tile tile, int x, int y, float renderX, float renderY, Color filter){
        String tex = this.texture;

        if(world.getMeta(x, y) == 1){
            tex += ".bottom";
        }

        manager.getImage(tex).draw(renderX, renderY, 1F, 1F, filter);
    }
}
