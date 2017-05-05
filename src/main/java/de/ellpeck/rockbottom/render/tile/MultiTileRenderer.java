package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.util.Pos2;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.tile.MultiTile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class MultiTileRenderer<T extends MultiTile> extends DefaultTileRenderer<T>{

    public MultiTileRenderer(String texture){
        super(texture);
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, T tile, int x, int y, float renderX, float renderY, Color filter){
        Pos2 innerCoord = tile.getInnerCoord(world.getMeta(x, y));
        manager.getImage(this.texture+"."+innerCoord.getX()+"."+innerCoord.getY()).draw(renderX, renderY, 1F, 1F, filter);
    }

    @Override
    public Image getParticleTexture(RockBottom game, AssetManager manager, Graphics g, T tile, int meta){
        return manager.getImage(this.texture+"."+tile.getMainX()+"."+tile.getMainY());
    }
}
