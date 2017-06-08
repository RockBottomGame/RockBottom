package de.ellpeck.rockbottom.game.world.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.game.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.game.render.entity.ItemEntityRenderer;

public class EntityItem extends Entity{

    private final BoundBox boundingBox = new BoundBox(-0.25, -0.25, 0.25, 0.25);
    private final IEntityRenderer renderer;
    public ItemInstance item;

    private int pickupDelay = 10;

    public EntityItem(IWorld world){
        super(world);
        this.renderer = new ItemEntityRenderer();
    }

    public EntityItem(IWorld world, ItemInstance item){
        this(world);
        this.item = item;
    }

    public static void spawn(IWorld world, ItemInstance inst, double x, double y, double motionX, double motionY){
        EntityItem item = new EntityItem(world, inst);
        item.setPos(x, y);
        item.motionX = motionX;
        item.motionY = motionY;
        world.addEntity(item);
    }

    @Override
    public IEntityRenderer getRenderer(){
        return this.renderer;
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        if(this.pickupDelay > 0){
            this.pickupDelay--;
        }

        if(this.ticksExisted >= this.item.getItem().getDespawnTime(this.item)){
            this.kill();
        }
    }

    @Override
    public int getRenderPriority(){
        return 1;
    }

    public boolean canPickUp(){
        return this.pickupDelay <= 0;
    }

    @Override
    public BoundBox getBoundingBox(){
        return this.boundingBox;
    }

    @Override
    protected void applyMotion(){
        this.motionY -= 0.015;

        this.motionX *= this.onGround ? 0.8 : 0.98;
        this.motionY *= 0.98;
    }

    @Override
    public void save(DataSet set){
        super.save(set);

        DataSet itemSet = new DataSet();
        this.item.save(itemSet);
        set.addDataSet("item", itemSet);

        set.addInt("pickup_delay", this.pickupDelay);
    }

    @Override
    public void load(DataSet set){
        super.load(set);

        DataSet itemSet = set.getDataSet("item");
        this.item = ItemInstance.load(itemSet);
        if(this.item == null){
            this.kill();
            return;
        }

        this.pickupDelay = set.getInt("pickup_delay");
    }
}
