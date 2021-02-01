package de.ellpeck.rockbottom.world.tile;


import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.AbstractItemEntity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.SpinningWheelTileRenderer;

public class SpinningWheelTile extends MultiTile {
    public SpinningWheelTile() {
        super(ResourceName.intern("spinning_wheel"));
        this.addProps(StaticTileProps.SPINNING_STAGE);
    }

    @Override
    protected boolean[][] makeStructure() {
        return new boolean[][]{
                {true, true, true},
                {true, true, true},
                {true, true, true}
        };
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractPlayerEntity placer) {
        return getDefState().prop(StaticTileProps.SPINNING_STAGE, 0);
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
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
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
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        Pos2 main = this.getMainPos(x, y, world.getState(layer, x, y));
        TileState state = world.getState(layer, main.getX(), main.getY());

        if (state.getTile() instanceof SpinningWheelTile) {
            int stage = state.get(StaticTileProps.SPINNING_STAGE);

            if (stage == 0) {
                ItemInstance held = player.getInv().get(player.getSelectedSlot());
                if (held != null && held.getItem() == GameContent.Tiles.COTTON.getItem() && held.getAmount() >= 3) {
                    if (!world.isClient()) {
                        player.getInv().remove(player.getSelectedSlot(), 3);
                        world.setState(layer, main.getX(), main.getY(), state.prop(StaticTileProps.SPINNING_STAGE, 1));
                    }
                    return true;
                }
            } else {
                if (!world.isClient()) {
                    if (stage < 7) {
                        world.setState(layer, main.getX(), main.getY(), state.prop(StaticTileProps.SPINNING_STAGE, stage + 1));
                    } else {
                        world.setState(layer, main.getX(), main.getY(), state.prop(StaticTileProps.SPINNING_STAGE, 0));
                        AbstractItemEntity.spawn(world, new ItemInstance(GameContent.Items.YARN), main.getX() + 2.5, main.getY() + 1.5, Util.RANDOM.nextGaussian() * 0.1, Util.RANDOM.nextGaussian() * 0.1);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    protected ITileRenderer<SpinningWheelTile> createRenderer(ResourceName name) {
        return new SpinningWheelTileRenderer(name, this);
    }

}