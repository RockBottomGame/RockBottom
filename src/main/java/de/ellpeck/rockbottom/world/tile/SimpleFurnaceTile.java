package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.particle.IParticleManager;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.SimpleFurnaceGui;
import de.ellpeck.rockbottom.gui.container.SimpleFurnaceContainer;
import de.ellpeck.rockbottom.render.tile.SimpleFurnaceTileRenderer;
import de.ellpeck.rockbottom.world.tile.entity.SimpleFurnaceTileEntity;

import java.util.Collections;
import java.util.List;

public class SimpleFurnaceTile extends MultiTile {

    public SimpleFurnaceTile() {
        super(ResourceName.intern("simple_furnace"));
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced) {
        super.describeItem(manager, instance, desc, isAdvanced);
        desc.add(manager.localize(ResourceName.intern("info.simple_furnace")));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        Pos2 main = this.getMainPos(x, y, world.getState(layer, x, y));
        SimpleFurnaceTileEntity tile = world.getTileEntity(layer, main.getX(), main.getY(), SimpleFurnaceTileEntity.class);
        return tile != null && player.openGuiContainer(new SimpleFurnaceGui(player, tile), new SimpleFurnaceContainer(player, tile));
    }

    @Override
    public int getLight(IWorld world, int x, int y, TileLayer layer) {
        Pos2 main = this.getMainPos(x, y, world.getState(layer, x, y));
        SimpleFurnaceTileEntity tile = world.getTileEntity(layer, main.getX(), main.getY(), SimpleFurnaceTileEntity.class);
        return tile != null && tile.isActive() ? 70 : 0;
    }

    @Override
    public boolean isPlatform() {
        return true;
    }

    @Override
    public List<BoundingBox> getPlatformBounds(IWorld world, int x, int y, TileLayer layer, TileState state, MovableWorldObject object, BoundingBox objectBox, BoundingBox objectBoxMotion) {
        if (layer == TileLayer.MAIN && !this.isMainPos(x, y, state))
            return RockBottomAPI.getApiHandler().getDefaultPlatformBounds(world, x, y, layer, 1, 4/12d, state, object, objectBox);
        else
            return Collections.emptyList();
    }

    @Override
    public boolean canProvideTileEntity() {
        return true;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y, TileLayer layer) {
        return this.isMainPos(x, y, world.getState(layer, x, y)) ? new SimpleFurnaceTileEntity(world, x, y, layer) : null;
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new SimpleFurnaceTileRenderer(name, this);
    }

    @Override
    protected boolean[][] makeStructure() {
        return new boolean[][]{
                {true},
                {true}
        };
    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 2;
    }

    @Override
    public int getMainX() {
        return 0;
    }

    @Override
    public int getMainY() {
        return 0;
    }

    @Override
    public void updateRandomlyInPlayerView(IWorld world, int x, int y, TileLayer layer, TileState state, IParticleManager manager) {
        if (Util.RANDOM.nextFloat() >= 0.25F) {
            Pos2 main = this.getMainPos(x, y, world.getState(layer, x, y));
            SimpleFurnaceTileEntity tile = world.getTileEntity(layer, main.getX(), main.getY(), SimpleFurnaceTileEntity.class);
            if (tile.isActive()) {
                if (x == main.getX() && y == main.getY()) {
                    if (Util.RANDOM.nextBoolean()) {
                        manager.addSmokeParticle(world, x + 0.4F + Util.RANDOM.nextFloat() * 0.2F, y + 0.4F + Util.RANDOM.nextFloat() * 0.2F, Util.RANDOM.nextGaussian() * 0.01F, 0F, Util.RANDOM.nextFloat() * 0.1F + 0.1F);
                    }
                } else {
                    manager.addSmokeParticle(world, x + 0.1F, y + 0.87F, Util.RANDOM.nextFloat() * -0.06F, 0F, Util.RANDOM.nextFloat() * 0.25F + 0.15F);
                    if (Util.RANDOM.nextFloat() >= 0.25F) {
                        manager.addSmokeParticle(world, x + 0.75F, y + 0.7F, Util.RANDOM.nextGaussian() * 0.01F, 0F, Util.RANDOM.nextFloat() * 0.15F + 0.15F);
                    }
                }
            }
        }
    }
}
