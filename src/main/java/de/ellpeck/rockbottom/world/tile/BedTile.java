package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.DyeColor;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.math.Matrix3;
import de.ellpeck.rockbottom.api.util.math.MatrixStack;
import de.ellpeck.rockbottom.api.util.math.Vector2;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.BedTileRenderer;
import de.ellpeck.rockbottom.world.tile.entity.BedTileEntity;
import org.lwjgl.system.CallbackI;

import java.util.Collections;
import java.util.List;

public class BedTile extends MultiTile {

    public BedTile() {
        super(ResourceName.intern("bed"));
        this.addProps(StaticTileProps.FACING_RIGHT);
        this.addProps(StaticTileProps.PILLOW_COLOR);
        this.addProps(StaticTileProps.COVER_COLOR);
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        double hitX = mouseX - x;
        double hitY = mouseY - y;

        TileState thisState = world.getState(x, y);
        boolean isMainPos = this.isMainPos(x, y, thisState);
        boolean isFacingRight = thisState.get(StaticTileProps.FACING_RIGHT);

        ItemInstance held = player.getSelectedItem();
        if (held != null && held.getItem() == GameContent.Tiles.FLOWER.getItem()) {
            DyeColor flowerColor = DyeColor.fromMeta(held.getMeta());
            int otherX = isMainPos ? x + 1 : x - 1;
            TileState otherState = world.getState(otherX, y);
            boolean used = false;
            if (this.isPillowPos(world, x, y)) {
                if (hitY > 4/12f && hitY < 8/12f) {
                    if (isFacingRight && hitX > 0.5f || !isFacingRight && hitX < 0.5f) {
                        world.setState(x, y, thisState.prop(StaticTileProps.PILLOW_COLOR, flowerColor));
                        world.setState(otherX, y, otherState.prop(StaticTileProps.PILLOW_COLOR, flowerColor));
                        used = true;
                    }
                }
            }

            if (!used) {
                if (hitY < 0.5) {
                    world.setState(x, y, thisState.prop(StaticTileProps.COVER_COLOR, flowerColor));
                    world.setState(otherX, y, otherState.prop(StaticTileProps.COVER_COLOR, flowerColor));
                    used = true;
                }
            }

            if (used) {
                player.getInv().set(player.getSelectedSlot(), held.removeAmount(1).nullIfEmpty());
                return true;
            }
        }

        if (!world.isClient()) {
            Pos2 pos = this.getMainPos(x, y, thisState);
            BedTileEntity bed = world.getTileEntity(pos.getX(), pos.getY(), BedTileEntity.class);
            if (bed.canSleep() && player.sleep(pos, true, !isFacingRight)) {
                bed.sleepingPlayer = player;
                return true;
            }
        }

        return super.onInteractWith(world, x, y, layer, mouseX, mouseY, player);
    }

    @Override
    public void doBreak(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity breaker, boolean isRightTool, boolean allowDrop) {
        if (!world.isClient() && layer == TileLayer.MAIN) {
            Pos2 pos = this.getMainPos(x, y, world.getState(x, y));
            BedTileEntity bed = world.getTileEntity(pos.getX(), pos.getY(), BedTileEntity.class);
            if (bed.sleepingPlayer != null) {
                bed.sleepingPlayer.removeBedSpawn();
            }
        }
        super.doBreak(world, x, y, layer, breaker, isRightTool, allowDrop);
    }

    public boolean isPillowPos(IWorld world, int x, int y) {
        return !world.getState(x, y).get(StaticTileProps.FACING_RIGHT) == this.isMainPos(x, y, world.getState(x, y));
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    public List<BoundingBox> getPlatformBounds(IWorld world, int x, int y, TileLayer layer, TileState state, MovableWorldObject object, BoundingBox objectBox, BoundingBox objectBoxMotion) {
        if (layer == TileLayer.MAIN)
            return RockBottomAPI.getApiHandler().getDefaultPlatformBounds(world, x, y, layer, 1, 5 / 12d, state, object, objectBox);
        else
            return Collections.emptyList();
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        if (this.isMainPos(x, y, world.getState(x, y))) {
            if (world.isPosLoaded(x, y - 1) && world.isPosLoaded(x + 1, y - 1)) {
                return world.getState(x, y - 1).getTile().hasSolidSurface(world, x, y - 1, layer) ||
                        world.getState(x + 1, y - 1).getTile().hasSolidSurface(world, x + 1, y - 1, layer);
            }
        } else {
            if (world.isPosLoaded(x, y - 1) && world.isPosLoaded(x - 1, y - 1)) {
                return world.getState(x, y - 1).getTile().hasSolidSurface(world, x, y - 1, layer) ||
                        world.getState(x - 1, y - 1).getTile().hasSolidSurface(world, x - 1, y - 1, layer);
            }
        }

        return true;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player) {
        if (super.canPlace(world, x, y, layer, player)) {
            return world.getState(x, y - 1).getTile().hasSolidSurface(world, x, y - 1, layer) &&
                    world.getState(x + 1, y - 1).getTile().hasSolidSurface(world, x + 1, y - 1, layer);
        }

        return false;
    }

    @Override
    public boolean canProvideTileEntity() {
        return true;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y, TileLayer layer) {
        if (this.isMainPos(x, y, world.getState(x, y))) {
            return new BedTileEntity(world, x, y, layer);
        }
        return null;
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean isPlatform() {
        return true;
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractPlayerEntity placer) {
        return this.getDefState().prop(StaticTileProps.FACING_RIGHT, placer.facing == Direction.RIGHT);
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new BedTileRenderer(name, this);
    }

    @Override
    protected boolean[][] makeStructure() {
        return new boolean[][]{
                {true, true}
        };
    }

    @Override
    public int getWidth() {
        return 2;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    @Override
    public int getMainX() {
        return 0;
    }

    @Override
    public int getMainY() {
        return 0;
    }
}
