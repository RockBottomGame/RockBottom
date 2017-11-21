package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.item.ItemTile;
import de.ellpeck.rockbottom.api.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileOreMaterial extends TileBasic{

    public TileOreMaterial(IResourceName name){
        super(name);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer){
        return false;
    }

    @Override
    protected ItemTile createItemTile(){
        return new ItemTile(this.getName()){
            @Override
            protected IItemRenderer createRenderer(IResourceName name){
                return new DefaultItemRenderer(name);
            }
        };
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return false;
    }
}
