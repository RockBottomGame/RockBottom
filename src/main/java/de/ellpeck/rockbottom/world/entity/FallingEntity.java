package de.ellpeck.rockbottom.world.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.AbstractItemEntity;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.entity.FallingEntityRenderer;

public class FallingEntity extends Entity {

    public static final ResourceName ID = ResourceName.intern("falling");

    private final IEntityRenderer renderer = new FallingEntityRenderer();
    public TileState state;
    public ItemInstance stateInstance;

    public FallingEntity(IWorld world, TileState state) {
        super(world);
        this.state = state;
        this.stateInstance = new ItemInstance(state.getTile());
    }

    public FallingEntity(IWorld world) {
        super(world);
    }

    @Override
    public ResourceName getRegistryName() {
        return ID;
    }

    @Override
    public IEntityRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public void update(IGameInstance game) {
        super.update(game);

        if (this.onGround) {
            if (!this.world.isClient()) {
                if (this.state != null) {
                    double x = this.getX();
                    double y = this.getY();

                    int tileX = Util.floor(x);
                    int tileY = Util.floor(y);
                    TileState state = this.world.getState(tileX, tileY);

                    if (state.getTile().canReplace(this.world, tileX, tileY, TileLayer.MAIN)) {
                        this.world.setState(tileX, tileY, this.state);
                    } else if (this.stateInstance != null) {
                        AbstractItemEntity.spawn(this.world, this.stateInstance.copy(), x, y, Util.RANDOM.nextGaussian() * 0.1, Util.RANDOM.nextGaussian() * 0.1);
                    }
                }

                this.setReadyToRemove();
            }
        }
    }

    @Override
    public void save(DataSet set, boolean forFullSync) {
        super.save(set, forFullSync);
        set.addInt("state", this.world.getIdForState(this.state));
    }

    @Override
    public void load(DataSet set, boolean forFullSync) {
        super.load(set, forFullSync);
        this.state = this.world.getStateForId(set.getInt("state"));
        if (this.state != null) {
            this.stateInstance = new ItemInstance(this.state.getTile());
        }
    }

    @Override
    public boolean canCollideWith(MovableWorldObject object, BoundingBox entityBox, BoundingBox entityBoxMotion) {
        return object instanceof AbstractPlayerEntity || object instanceof FallingEntity;
    }
}
