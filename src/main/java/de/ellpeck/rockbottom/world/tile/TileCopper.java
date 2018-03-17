package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.TileCopperRenderer;

import java.util.List;

public class TileCopper extends TileOreMaterial{

    public TileCopper(){
        super(RockBottomAPI.createInternalRes("copper"));
        this.addProps(StaticTileProps.HAS_CANISTER);
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new TileCopperRenderer(name);
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer){
        List<ItemInstance> drops = super.getDrops(world, x, y, layer, destroyer);
        if(world.getState(layer, x, y).get(StaticTileProps.HAS_CANISTER)){
            drops.add(new ItemInstance(GameContent.ITEM_COPPER_CANISTER));
        }
        return drops;
    }
}