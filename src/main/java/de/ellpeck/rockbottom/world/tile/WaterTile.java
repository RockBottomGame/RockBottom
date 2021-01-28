package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.tile.LiquidTile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.List;

public class WaterTile extends LiquidTile {

    private final BoundingBox[] bounds;

    public WaterTile() {
        super(ResourceName.intern("water"));

        this.bounds = new BoundingBox[this.getLevels()];
        for (int i = 0; i < this.bounds.length; i++) {
            this.bounds[i] = new BoundingBox(0, 0, 1, (i + 1) / (double) this.getLevels());
        }
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return this.bounds[state.get(this.level)];
    }

    @Override
    public int getLevels() {
        return 12;
    }

    @Override
    public boolean doesFlow() {
        return true;
    }

    @Override
    public int getFlowSpeed() {
        return 5;
    }

    @Override
    public void onIntersectWithEntity(IWorld world, int x, int y, TileLayer layer, TileState state, BoundingBox entityBox, BoundingBox entityBoxMotion, List<BoundingBox> tileBoxes, Entity entity) {
        super.onIntersectWithEntity(world, x, y, layer, state, entityBox, entityBoxMotion, tileBoxes, entity);

        for (BoundingBox box : tileBoxes) {
            if (box.contains(entity.getX(), entity.getOriginY())) {
                if (!(entity instanceof AbstractPlayerEntity) || !((AbstractPlayerEntity) entity).getGameMode().isCreative()) {
                    entity.motionX *= 0.65;
                    if (entity.motionY < 0) {
                        entity.motionY *= 0.65;
                    }
                }
                entity.fallStartY = entity.getY();

                break;
            }
        }
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer) {
        if (Util.RANDOM.nextFloat() >= 0.95F) {
            TileState state = world.getState(layer, x, y);
            int level = state.get(this.level);
            if (level <= 3) {
                for (Direction dir : Direction.ADJACENT) {
                    if (!world.isPosLoaded(x + dir.x, y + dir.y)) {
                        return;
                    } else {
                        TileState other = world.getState(layer, x + dir.x, y + dir.y);
                        if (other.getTile() == this && other.get(this.level) > 3) {
                            return;
                        }
                    }
                }

                if (level <= 0) {
                    world.setState(layer, x, y, GameContent.TILE_AIR.getDefState());
                } else {
                    world.setState(layer, x, y, state.prop(this.level, level - 1));
                }
            }
        }
    }
}
