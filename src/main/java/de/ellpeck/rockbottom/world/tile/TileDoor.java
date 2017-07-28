package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.tile.state.BoolProp;
import de.ellpeck.rockbottom.api.tile.state.TileProp;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.render.tile.DoorTileRenderer;

public class TileDoor extends MultiTile{

    public static final BoolProp RIGHT_PROP = new BoolProp("right", false);
    public static final BoolProp OPEN_PROP = new BoolProp("open", false);

    private static final BoundBox CLOSED_LEFT = new BoundBox(0, 0, 1D/6D, 1);
    private static final BoundBox CLOSED_RIGHT = new BoundBox(5D/6D, 0, 1, 1);

    public TileDoor(){
        super(AbstractGame.internalRes("door"));
        this.addProps(OPEN_PROP, RIGHT_PROP);
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        TileState state = world.getState(x, y);
        if(!state.get(OPEN_PROP)){
            return state.get(RIGHT_PROP) ? CLOSED_RIGHT : CLOSED_LEFT;
        }
        else{
            return null;
        }
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer){
        return this.getDefState().prop(RIGHT_PROP, placer.facing == Direction.RIGHT);
    }

    @Override
    protected boolean[][] makeStructure(){
        return new boolean[][]{
                {true},
                {true}
        };
    }

    @Override
    public int getWidth(){
        return 1;
    }

    @Override
    public int getHeight(){
        return 2;
    }

    @Override
    public int getMainX(){
        return 0;
    }

    @Override
    public int getMainY(){
        return 0;
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player){
        if(!world.isClient()){
            Pos2 main = this.getMainPos(x, y, world.getState(x, y));
            for(int addX = 0; addX < this.getWidth(); addX++){
                for(int addY = 0; addY < this.getHeight(); addY++){
                    if(this.isStructurePart(addX, addY)){
                        TileState state = world.getState(main.getX()+addX, main.getY()+addY);
                        world.setState(main.getX()+addX, main.getY()+addY, state.prop(OPEN_PROP, !state.get(OPEN_PROP)));
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new DoorTileRenderer(name, this);
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public float getTranslucentModifier(IWorld world, int x, int y, TileLayer layer, boolean skylight){
        if(!skylight){
            return world.getState(x, y).get(OPEN_PROP) ? 0.9F : 0.8F;
        }
        else{
            return super.getTranslucentModifier(world, x, y, layer, skylight);
        }
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer){
        return super.canPlace(world, x, y, layer) && isValidForPlacement(world, x, y);
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        Pos2 mainPos = this.getMainPos(x, y, world.getState(x, y));
        return isValidForPlacement(world,mainPos.getX(), mainPos.getY());
    }

    private static boolean isValidForPlacement(IWorld world, int x, int y){
        return world.getState(x, y-1).getTile().isFullTile();
    }
}
