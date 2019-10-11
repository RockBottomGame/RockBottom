package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ItemTool;
import de.ellpeck.rockbottom.api.item.ToolProperty;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.entity.EntityBoomerang;

public class ItemBoomerang extends ItemTool {

    private final double speed;
    private final double maxDistance;

    public ItemBoomerang(ResourceName name, int durability, int level, double speed, double maxDistance) {
        super(name, 1F, durability, ToolProperty.BOOMERANG, level);
        this.speed = speed;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance) {
        if (!world.isClient()) {
            EntityBoomerang boomerang = new EntityBoomerang(world);
            boomerang.setPos(player.getX(), player.getOriginY() + player.getEyeHeight());
            boomerang.setStart(boomerang.getX(), boomerang.getY());
            boomerang.setMaxDistance(this.maxDistance);
            boomerang.setItem(instance.copy());

            double diffX = mouseX - player.getX();
            double diffY = mouseY - player.getY();
            double length = Util.distance(0, 0, diffX, diffY);

            boomerang.motionX = this.speed * (diffX / length);
            boomerang.motionY = this.speed * (diffY / length);

            world.addEntity(boomerang);

            player.getInv().remove(player.getSelectedSlot(), 1);
        }
        return true;
    }

    @Override
    public double getMaxInteractionDistance(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance) {
        return Double.MAX_VALUE;
    }
}
