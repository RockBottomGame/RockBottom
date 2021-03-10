package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.DyeColor;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class BedTileEntity extends TileEntity {

    public AbstractPlayerEntity sleepingPlayer;
    public DyeColor pillowColor = DyeColor.WHITE;
    public DyeColor coverColor = DyeColor.WHITE;

    public BedTileEntity(IWorld world, int x, int y, TileLayer layer) {
        super(world, x, y, layer);
    }

    @Override
    public void save(DataSet set, boolean forSync) {
        set.addEnum("pillow", this.pillowColor);
        set.addEnum("cover", this.coverColor);
    }

    @Override
    public void load(DataSet set, boolean forSync) {
        this.pillowColor = set.getEnum("pillow", DyeColor.class);
        this.coverColor = set.getEnum("cover", DyeColor.class);
    }

    public boolean canSleep() {
        return this.sleepingPlayer == null && this.world.isNighttime();
    }
}