package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.TileStardropRenderer;

import java.util.List;

public class TileStardrop extends TileBasic{

    public TileStardrop(){
        super(ResourceName.intern("stardrop"));
        this.addProps(StaticTileProps.STARDROP_GROWTH);
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced){
        super.describeItem(manager, instance, desc, isAdvanced);
        desc.add(FormattingCode.GRAY+manager.localize(ResourceName.intern("info.stardrop")));
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name){
        return new TileStardropRenderer(name);
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer){
        return null;
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer){
        if(Util.RANDOM.nextDouble() >= 0.75){
            if(world.isDaytime() && world.getSkyLight(x, y) >= 25){
                TileState state = world.getState(layer, x, y);
                if(state.get(StaticTileProps.STARDROP_GROWTH) < 4){
                    world.setState(layer, x, y, state.cycleProp(StaticTileProps.STARDROP_GROWTH));
                }
            }
        }
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return false;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player){
        return false;
    }
}
