package de.ellpeck.rockbottom.world.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.AbstractEntityBoomerang;
import de.ellpeck.rockbottom.api.entity.AbstractEntityItem;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolProperty;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.render.entity.BoomerangEntityRenderer;

public class EntityBoomerang extends AbstractEntityBoomerang {

    private final IEntityRenderer renderer = new BoomerangEntityRenderer();
    private double startX;
    private double startY;
    private double maxDistanceSq;
    private boolean turnedAround;
    private ItemInstance item;

    public EntityBoomerang(IWorld world) {
        super(world);
    }

    @Override
    public void update(IGameInstance game) {
        double lastMotionX = this.motionX;
        double lastMotionY = this.motionY;

        super.update(game);

        if (!this.world.isClient()) {
            double x = this.getX();
            double y = this.getY();

            double distSq = Util.distanceSq(this.startX, this.startY, x, y);
            boolean coll = this.collidedHor || this.collidedVert;

            if (coll || distSq >= this.maxDistanceSq) {
                if (this.turnedAround) {
                    if (this.item != null) {
                        ItemInstance remain = this.item.getItem().takeDamage(this.item, 1);
                        if (remain != null) {
                            AbstractEntityItem.spawn(this.world, remain, x, y, this.motionX, this.motionY);
                        }
                    }
                    this.setDead(true);
                } else {
                    this.turnedAround = true;
                    this.startX = x;
                    this.startY = y;

                    if (coll) {
                        this.maxDistanceSq = distSq;
                    }

                    this.motionX = -lastMotionX;
                    this.motionY = -lastMotionY;
                }
            }
        }
    }

    @Override
    public void onIntersectWithEntity(Entity otherEntity, BoundBox thisBox, BoundBox thisBoxMotion, BoundBox otherBox, BoundBox otherBoxMotion) {
        if (!this.world.isClient() && this.item != null && !this.isDead()) {
            if (otherEntity instanceof AbstractEntityPlayer) {
                if (this.turnedAround) {
                    ItemInstance remain = this.item.getItem().takeDamage(this.item, 1);
                    if (remain != null) {
                        remain = ((AbstractEntityPlayer) otherEntity).getInv().add(remain, false);
                        if (remain == null) {
                            this.setDead(true);
                        }
                    } else {
                        this.setDead(true);
                    }
                }
            } else {
                int damage = this.item.getItem().getToolProperties(this.item).get(ToolProperty.BOOMERANG);
                if (damage > 0) {
                    otherEntity.onAttack(null, 0D, 0D, damage);
                }
            }
        }
    }

    @Override
    public boolean doesInterpolate() {
        return true;
    }

    @Override
    public int getSyncFrequency() {
        return 3;
    }

    @Override
    public void applyMotion() {

    }

    @Override
    public void setStart(double x, double y) {
        this.startX = x;
        this.startY = y;
    }

    @Override
    public double getStartX() {
        return this.startX;
    }

    @Override
    public double getStartY() {
        return this.startY;
    }

    @Override
    public double getMaxDistanceSq() {
        return this.maxDistanceSq;
    }

    @Override
    public void setMaxDistance(double maxDistance) {
        this.maxDistanceSq = maxDistance * maxDistance;
    }

    @Override
    public ItemInstance getItem() {
        return this.item;
    }

    @Override
    public void setItem(ItemInstance item) {
        this.item = item;
    }

    @Override
    public IEntityRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public void save(DataSet set, boolean forFullSync) {
        super.save(set, forFullSync);

        if (!forFullSync) {
            set.addDouble("start_x", this.startX);
            set.addDouble("start_y", this.startY);
            set.addDouble("max_dist", this.maxDistanceSq);
            set.addBoolean("turned_around", this.turnedAround);
        }

        DataSet sub = new DataSet();
        this.item.save(sub);
        set.addDataSet("item", sub);
    }

    @Override
    public void load(DataSet set, boolean forFullSync) {
        super.load(set, forFullSync);

        if (!forFullSync) {
            this.startX = set.getDouble("start_x");
            this.startY = set.getDouble("start_y");
            this.maxDistanceSq = set.getDouble("max_dist");
            this.turnedAround = set.getBoolean("turned_around");
        }

        DataSet sub = set.getDataSet("item");
        this.item = ItemInstance.load(sub);
    }
}
