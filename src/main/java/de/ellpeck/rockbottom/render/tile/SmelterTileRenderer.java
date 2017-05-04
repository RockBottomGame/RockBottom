package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.tile.Tile;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySmelter;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class SmelterTileRenderer extends DefaultTileRenderer{

    public SmelterTileRenderer(String texture){
        super(texture);
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, Tile tile, int x, int y, float renderX, float renderY, Color filter){
        String tex = this.texture;

        if(world.getMeta(x, y) == 0){
            tex += ".top";
        }
        else{
            TileEntitySmelter tileEntity = world.getTileEntity(x, y, TileEntitySmelter.class);
            if(tileEntity != null && tileEntity.isActive()){
                tex += ".active";
            }
        }

        manager.getImage(tex).draw(renderX, renderY, 1F, 1F, filter);
    }

    @Override
    public void renderItem(RockBottom game, AssetManager manager, Graphics g, Tile tile, int meta, float x, float y, float scale, Color filter){
        manager.getImage(this.texture+".item").draw(x, y, scale, scale, filter);
    }
}
