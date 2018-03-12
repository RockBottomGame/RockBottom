package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.StaticTileProps.LogType;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.TileLiquid;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.TileLogRenderer;

import java.util.ArrayList;
import java.util.List;

public class TileLog extends TileBasic{

    public TileLog(){
        super(RockBottomAPI.createInternalRes("log"));
        this.addProps(StaticTileProps.LOG_VARIANT);
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer){
        return world.getState(layer, x, y).get(StaticTileProps.LOG_VARIANT).isNatural() ? null : super.getBoundBox(world, x, y, layer);
    }

    @Override
    public float getHardness(IWorld world, int x, int y, TileLayer layer){
        float hardness = super.getHardness(world, x, y, layer);
        return world.getState(layer, x, y).get(StaticTileProps.LOG_VARIANT).isNatural() ? 3F*hardness : hardness;
    }

    @Override
    public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean shouldDrop){
        super.onDestroyed(world, x, y, destroyer, layer, shouldDrop);

        if(destroyer != null && !world.isClient()){
            if(world.getState(layer, x, y).get(StaticTileProps.LOG_VARIANT).isNatural()){
                List<Pos2> alreadyChecked = new ArrayList<>();
                this.recursiveTreeCheck(world, x, y, layer, y, alreadyChecked);
            }
        }
    }

    private void recursiveTreeCheck(IWorld world, int x, int y, TileLayer layer, int originalY, List<Pos2> alreadyChecked){
        for(Direction direction : Direction.ADJACENT){
            if(direction != Direction.DOWN){
                Pos2 pos = new Pos2(x+direction.x, y+direction.y);
                if(!alreadyChecked.contains(pos)){
                    alreadyChecked.add(pos);

                    TileState state = world.getState(layer, pos.getX(), pos.getY());
                    if(state.getTile() == this && state.get(StaticTileProps.LOG_VARIANT).isNatural()){
                        world.scheduleUpdate(pos.getX(), pos.getY(), layer, (y-originalY)*3);
                        this.recursiveTreeCheck(world, pos.getX(), pos.getY(), layer, originalY, alreadyChecked);
                    }
                }
            }
        }
    }

    @Override
    public void onScheduledUpdate(IWorld world, int x, int y, TileLayer layer, int scheduledMeta){
        if(!world.isClient()){
            if(world.getState(layer, x, y).get(StaticTileProps.LOG_VARIANT).isNatural()){
                world.destroyTile(x, y, layer, null, true);
            }
        }
    }

    @Override
    public boolean doesSustainLeaves(IWorld world, int x, int y, TileLayer layer){
        return world.getState(layer, x, y).get(StaticTileProps.LOG_VARIANT).isNatural();
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new TileLogRenderer(name);
    }

    @Override
    public boolean hasSolidSurface(IWorld world, int x, int y, TileLayer layer){
        return !world.getState(layer, x, y).get(StaticTileProps.LOG_VARIANT).isNatural();
    }

    @Override
    public boolean canLiquidSpreadInto(IWorld world, int x, int y, TileLiquid liquid){
        LogType type = world.getState(x, y).get(StaticTileProps.LOG_VARIANT);
        return type != LogType.TRUNK_BOTTOM && type != LogType.TRUNK_MIDDLE;
    }

    @Override
    public boolean canBreak(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player, boolean isRightTool){
        if(isRightTool){
            LogType type = world.getState(layer, x, y).get(StaticTileProps.LOG_VARIANT);
            return !type.isNatural() || type == LogType.TRUNK_BOTTOM || type == LogType.TRUNK_MIDDLE;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean obscuresBackground(IWorld world, int x, int y, TileLayer layer){
        LogType type = world.getState(layer, x, y).get(StaticTileProps.LOG_VARIANT);
        return !type.isNatural() || type == LogType.TRUNK_BOTTOM || type == LogType.TRUNK_MIDDLE;
    }

    @Override
    public boolean factorsIntoHeightMap(IWorld world, int x, int y, TileLayer layer){
        return false;
    }
}
