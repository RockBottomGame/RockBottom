package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.particle.IParticleManager;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.TileTorchRenderer;

public class TileTorch extends TileLamp {

    public TileTorch(ResourceName name) {
        super(name);
        this.addProps(StaticTileProps.TORCH_TIMER);
    }

    public double getTurnOffChance() {
        return 0.95;
    }

    public int getMaxLight() {
        return 80;
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer) {
        if (Util.RANDOM.nextDouble() >= this.getTurnOffChance()) {
            TileState state = world.getState(layer, x, y);
            if (state.get(StaticTileProps.TORCH_TIMER) < 9) {
                world.setState(layer, x, y, state.cycleProp(StaticTileProps.TORCH_TIMER));
            }
        }
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
        TileState state = world.getState(layer, x, y);
        if (state.get(StaticTileProps.TORCH_TIMER) > 0) {
            if (!world.isClient()) {
                world.setState(layer, x, y, state.prop(StaticTileProps.TORCH_TIMER, 0));
            }
            return true;
        }
        return false;
    }

    @Override
    public int getLight(IWorld world, int x, int y, TileLayer layer) {
        int timer = world.getState(layer, x, y).get(StaticTileProps.TORCH_TIMER);
        float onPercentage = 1F - timer / 10F;
        return Util.ceil(this.getMaxLight() * onPercentage);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player) {
        return this.getTorchState(world, x, y, 0) != null;
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer) {
        return this.getTorchState(world, x, y, 0);
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        if (!world.isClient()) {
            TileState state = this.getTorchState(world, x, y, world.getState(layer, x, y).get(StaticTileProps.TORCH_TIMER));

            if (state == null) {
                world.destroyTile(x, y, layer, null, this.forceDrop);
            } else if (state != world.getState(x, y)) {
                world.setState(x, y, state);
            }
        }
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return this.getTorchState(world, x, y, 0) != null;
    }

    private TileState getTorchState(IWorld world, int x, int y, int timer) {
        int meta = this.getFacingMeta(world, x, y);
        if (meta >= 0) {
            return this.getDefState().prop(StaticTileProps.TORCH_FACING, meta).prop(StaticTileProps.TORCH_TIMER, timer);
        } else {
            return null;
        }
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new TileTorchRenderer(name);
    }

    @Override
    public void updateRandomlyInPlayerView(IWorld world, int x, int y, TileLayer layer, TileState state, IParticleManager manager) {
        float percentage = 1F - state.get(StaticTileProps.TORCH_TIMER) / 10F;
        if (Util.RANDOM.nextFloat() <= 0.25F * percentage) {
            manager.addSmokeParticle(world, x + 0.5F, y + 0.8F, Util.RANDOM.nextGaussian() * 0.025F, 0F, Util.RANDOM.nextFloat() * 0.15F + 0.15F);
        }
    }
}
