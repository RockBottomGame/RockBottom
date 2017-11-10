package de.ellpeck.rockbottom.world.entity;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.entity.SandEntityRenderer;

public class EntitySand extends Entity{

    private final IEntityRenderer renderer = new SandEntityRenderer();

    public EntitySand(IWorld world){
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
                int x = Util.floor(this.x);
                int y = Util.floor(this.y);
                TileState state = this.world.getState(x, y);

                if(state.getTile().canReplace(this.world, x, y, TileLayer.MAIN)){
                    this.world.setState(x, y, GameContent.TILE_SAND.getDefState());
                }
                else{
                    EntityItem.spawn(this.world, new ItemInstance(GameContent.TILE_SAND), this.x, this.y, Util.RANDOM.nextGaussian()*0.1, Util.RANDOM.nextGaussian()*0.1);
                }

                this.kill();
            }
        }
    }

    @Override
    public boolean canCollideWith(MovableWorldObject object, BoundBox entityBox, BoundBox entityBoxMotion){
        return object instanceof EntitySand || object instanceof AbstractEntityPlayer;
    }
}
