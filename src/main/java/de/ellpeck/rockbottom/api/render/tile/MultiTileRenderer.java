package de.ellpeck.rockbottom.api.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.world.IWorld;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class MultiTileRenderer<T extends MultiTile> extends DefaultTileRenderer<T>{

    public MultiTileRenderer(String texture){
        super(texture);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, T tile, int x, int y, float renderX, float renderY, Color filter){
        Pos2 innerCoord = tile.getInnerCoord(world.getMeta(x, y));
        manager.getImage(this.texture+"."+innerCoord.getX()+"."+innerCoord.getY()).draw(renderX, renderY, 1F, 1F, filter);
    }

    @Override
    public Image getParticleTexture(IGameInstance game, IAssetManager manager, Graphics g, T tile, int meta){
        Pos2 innerCoord = tile.getInnerCoord(meta);
        return manager.getImage(this.texture+"."+innerCoord.getX()+"."+innerCoord.getY());
    }

    @Override
    public void renderItem(IGameInstance game, IAssetManager manager, Graphics g, T tile, int meta, float x, float y, float scale, Color filter){
        manager.getImage(this.texture+".item").draw(x, y, scale, scale, filter);
    }
}