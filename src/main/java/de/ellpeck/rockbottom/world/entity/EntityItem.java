package de.ellpeck.rockbottom.world.entity;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.data.set.DataSet;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.render.entity.ItemEntityRenderer;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.world.World;

public class EntityItem extends Entity{

    private final BoundBox boundingBox = new BoundBox(-0.25, -0.25, 0.25, 0.25);
    private final IEntityRenderer renderer;
    public ItemInstance item;

    private int pickupDelay = 10;

    public EntityItem(World world){
        super(world);
        this.renderer = new ItemEntityRenderer();
    }

    public EntityItem(World world, ItemInstance item){
        this(world);
        this.item = item;
    }

    public static void spawn(World world, ItemInstance inst, double x, double y, double motionX, double motionY){
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
    public void update(RockBottom game){
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
