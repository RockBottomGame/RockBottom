package de.ellpeck.rockbottom.world.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.AbstractEntityItem;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.entity.FallingEntityRenderer;

public class EntityFalling extends Entity{

    private final IEntityRenderer renderer = new FallingEntityRenderer();
    public TileState state;
    public ItemInstance stateInstance;

    public EntityFalling(IWorld world, TileState state){
        super(world);
        this.state = state;
        this.stateInstance = new ItemInstance(state.getTile());
    }

    public EntityFalling(IWorld world){
        super(world);
    }

    @Override
    public IEntityRenderer getRenderer(){
        return this.renderer;
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        if(this.onGround){
            if(!this.world.isClient()){
                if(this.state != null){
                    int x = Util.floor(this.x);
                    int y = Util.floor(this.y);
                    TileState state = this.world.getState(x, y);

                    if(state.getTile().canReplace(this.world, x, y, TileLayer.MAIN)){
                        this.world.setState(x, y, this.state);
                    }
                    else if(this.stateInstance != null){
                        AbstractEntityItem.spawn(this.world, this.stateInstance.copy(), this.x, this.y, Util.RANDOM.nextGaussian()*0.1, Util.RANDOM.nextGaussian()*0.1);
                    }
                }

                this.kill();
            }
        }
    }

    @Override
    public void save(DataSet set){
        super.save(set);
        set.addInt("state", this.world.getIdForState(this.state));
    }

    @Override
    public void load(DataSet set){
        super.load(set);
        this.state = this.world.getStateForId(set.getInt("state"));
        if(this.state != null){
            this.stateInstance = new ItemInstance(this.state.getTile());
        }
    }

    @Override
    public boolean canCollideWith(MovableWorldObject object, BoundBox entityBox, BoundBox entityBoxMotion){
        return object instanceof AbstractEntityPlayer;
    }
}
