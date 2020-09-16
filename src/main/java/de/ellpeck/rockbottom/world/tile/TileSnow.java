package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.TileSnowRenderer;

import java.util.List;

public class TileSnow extends TileFalling {

    private final BoundingBox boundingBox = new BoundingBox(0D, 0D, 1D, 0.75D);

    public TileSnow() {
        super(ResourceName.intern("snow"));
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new TileSnowRenderer(name);
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return this.boundingBox;
    }

    @Override
    public boolean makesGrassSnowy(IWorld world, int x, int y, TileLayer layer) {
        return true;
    }

    @Override
    public void onIntersectWithEntity(IWorld world, int x, int y, TileLayer layer, TileState state, BoundingBox entityBox, BoundingBox entityBoxMotion, List<BoundingBox> tileBoxes, Entity entity) {
        if (layer == TileLayer.MAIN && entity instanceof AbstractEntityPlayer) {
            entity.motionX *= 0.9D;

            if (!world.isDedicatedServer() && Math.abs(entity.motionX) > 0.01D && world.getTotalTime() % 15 == 0) {
                if (entityBox.intersects(new BoundingBox(0D, 0D, 1D, 1D).add(x, y))) {
                    for (int i = Util.RANDOM.nextInt(5); i >= 0; i--) {
                        RockBottomAPI.getGame().getParticleManager().addSnowParticle(world, entity.getX() + Util.RANDOM.nextGaussian() * 0.1D, y + 0.9D + Util.RANDOM.nextFloat() * 0.2D, -entity.motionX * Util.RANDOM.nextFloat() * 0.2D, Util.RANDOM.nextFloat() * 0.2D, 25);
                    }
                }
            }
        }
    }
}
