package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.TileGrassTorchRenderer;

public class TileGrassTorch extends TileTorch{

    public TileGrassTorch(){
        super(RockBottomAPI.createInternalRes("torch_grass"));
        this.addProps(StaticTileProps.TORCH_TIMER);
    }

    @Override
    public int getLight(IWorld world, int x, int y, TileLayer layer){
        int timer = world.getState(layer, x, y).get(StaticTileProps.TORCH_TIMER);
        switch(timer){
            case 4:
                return 3;
            case 3:
                return 10;
            case 2:
                return 15;
            default:
                return 20;
        }
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new TileGrassTorchRenderer(name);
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer){
        if(Util.RANDOM.nextDouble() >= 0.95){
            TileState state = world.getState(layer, x, y);
            if(state.get(StaticTileProps.TORCH_TIMER) < 4){
                world.setState(layer, x, y, state.cycleProp(StaticTileProps.TORCH_TIMER));
            }
        }
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player){
        TileState state = world.getState(layer, x, y);
        if(state.get(StaticTileProps.TORCH_TIMER) > 0){
            if(!world.isClient()){
                world.setState(layer, x, y, state.prop(StaticTileProps.TORCH_TIMER, 0));
            }
            return true;
        }
        return false;
    }
}
