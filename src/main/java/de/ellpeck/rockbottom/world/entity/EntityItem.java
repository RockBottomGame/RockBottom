package de.ellpeck.rockbottom.world.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.AbstractEntityItem;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.ApiInternal;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.render.entity.ItemEntityRenderer;

@ApiInternal
public class EntityItem extends AbstractEntityItem {

    private final IEntityRenderer renderer;
    private ItemInstance item;

    private int pickupDelay = 10;

    public EntityItem(IWorld world) {
        super(world);
        this.renderer = new ItemEntityRenderer();
    }

    public EntityItem(IWorld world, ItemInstance item) {
        this(world);
        this.item = item;
    }

    @Override
    public IEntityRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public void update(IGameInstance game) {
        super.update(game);

        if (this.pickupDelay > 0) {
            this.pickupDelay--;
        }

        if (!this.world.isClient()) {
            if (this.item != null) {
                if (this.ticksExisted >= this.item.getItem().getDespawnTime(this.item)) {
                    this.setReadyToRemove();
                }
            } else {
                this.setReadyToRemove();
            }
        }
    }

    @Override
    public int getSyncFrequency() {
        return 5;
    }

    @Override
    public boolean doesInterpolate() {
        return true;
    }

    @Override
    public int getRenderPriority() {
        return 100;
    }

    @Override
    public boolean canPickUp() {
        return this.pickupDelay <= 0;
    }

    @Override
    public void applyMotion() {
        this.motionY -= 0.015;

        this.motionX *= this.onGround ? 0.8 : 0.98;
        this.motionY *= 0.98;
    }

    @Override
    public void save(DataSet set, boolean forFullSync) {
        super.save(set, forFullSync);

        if (this.item != null) {
            DataSet itemSet = new DataSet();
            this.item.save(itemSet);
            set.addDataSet("item", itemSet);
        }

        set.addInt("pickup_delay", this.pickupDelay);
    }

    @Override
    public void load(DataSet set, boolean forFullSync) {
        super.load(set, forFullSync);

        DataSet itemSet = set.getDataSet("item");
        this.item = ItemInstance.load(itemSet);
        this.pickupDelay = set.getInt("pickup_delay");
    }

    @Override
    public ItemInstance getItem() {
        return this.item;
    }

    @Override
    public void setItem(ItemInstance instance) {
        this.item = instance;
    }

    @Override
    public float getWidth() {
        return 0.5F;
    }

    @Override
    public float getHeight() {
        return 0.5F;
    }
}
