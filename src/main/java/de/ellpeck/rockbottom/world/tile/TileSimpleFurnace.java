package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.particle.IParticleManager;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.GuiSimpleFurnace;
import de.ellpeck.rockbottom.gui.container.ContainerSimpleFurnace;
import de.ellpeck.rockbottom.render.tile.TileSimpleFurnaceRenderer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySimpleFurnace;

import java.util.Collections;
import java.util.List;

public class TileSimpleFurnace extends MultiTile {

    public TileSimpleFurnace() {
        super(ResourceName.intern("simple_furnace"));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
        Pos2 main = this.getMainPos(x, y, world.getState(layer, x, y));
        TileEntitySimpleFurnace tile = world.getTileEntity(layer, main.getX(), main.getY(), TileEntitySimpleFurnace.class);
        return tile != null && player.openGuiContainer(new GuiSimpleFurnace(player, tile), new ContainerSimpleFurnace(player, tile));
    }

    @Override
    public int getLight(IWorld world, int x, int y, TileLayer layer) {
        Pos2 main = this.getMainPos(x, y, world.getState(layer, x, y));
        TileEntitySimpleFurnace tile = world.getTileEntity(layer, main.getX(), main.getY(), TileEntitySimpleFurnace.class);
        return tile != null && tile.isActive() ? 70 : 0;
    }

    @Override
    public boolean isPlatform() {
        return true;
    }

    @Override
    public List<BoundBox> getPlatformBounds(IWorld world, int x, int y, TileLayer layer, TileState state, MovableWorldObject object, BoundBox objectBox, BoundBox objectBoxMotion) {
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
        return this.isMainPos(x, y, world.getState(layer, x, y)) ? new TileEntitySimpleFurnace(world, x, y, layer) : null;
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
    public BoundBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new TileSimpleFurnaceRenderer(name, this);
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
            TileEntitySimpleFurnace tile = world.getTileEntity(layer, main.getX(), main.getY(), TileEntitySimpleFurnace.class);
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
