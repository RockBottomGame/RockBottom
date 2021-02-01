package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.ArrayList;
import java.util.List;

public class LeavesTile extends BasicTile {

    public LeavesTile() {
        super(ResourceName.intern("leaves"));
        this.addProps(StaticTileProps.NATURAL);
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return state.get(StaticTileProps.NATURAL) ? null : super.getBoundBox(world, state, x, y, layer);
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractPlayerEntity placer) {
        return super.getPlacementState(world, x, y, layer, instance, placer).prop(StaticTileProps.NATURAL, false);
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer) {
        List<ItemInstance> drops = new ArrayList<>();

        if (Util.RANDOM.nextDouble() >= 0.85) {
            drops.add(new ItemInstance(GameContent.Tiles.SAPLING));
        }

        if (destroyer != null && Util.RANDOM.nextDouble() >= 0.65) {
            drops.add(new ItemInstance(GameContent.Items.TWIG));
        }

        return drops;
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        if (!world.isClient()) {
            if (world.getState(layer, x, y).get(StaticTileProps.NATURAL)) {
                if (!this.recursiveLeavesCheck(world, x, y, layer, new ArrayList<>())) {
                    world.scheduleUpdate(x, y, layer, Util.RANDOM.nextInt(25) + 5);
                }
            }
        }
    }

    private boolean recursiveLeavesCheck(IWorld world, int x, int y, TileLayer layer, List<Pos2> alreadyChecked) {
        for (Direction direction : Direction.ADJACENT) {
            Pos2 pos = new Pos2(x + direction.x, y + direction.y);
            if (!alreadyChecked.contains(pos)) {
                alreadyChecked.add(pos);

                TileState state = world.getState(layer, pos.getX(), pos.getY());
                if (state.getTile() == this && state.get(StaticTileProps.NATURAL)) {
                    if (this.recursiveLeavesCheck(world, pos.getX(), pos.getY(), layer, alreadyChecked)) {
                        return true;
                    }
                } else if (state.getTile().doesSustainLeaves(world, pos.getX(), pos.getY(), layer)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onScheduledUpdate(IWorld world, int x, int y, TileLayer layer, int scheduledMeta) {
        if (!world.isClient()) {
            if (world.getState(layer, x, y).get(StaticTileProps.NATURAL)) {
                world.destroyTile(x, y, layer, null, true);
            }
        }
    }
}
