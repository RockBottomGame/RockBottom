package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.TileLiquid;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.TilePlatformRenderer;

import java.util.List;

public class TilePlatform extends TileBasic {

    public TilePlatform() {
        super(ResourceName.intern("platform"));
        this.addProps(StaticTileProps.HAS_LADDER);
    }

    @Override
    public BoundBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    public boolean isPlatform() {
        return true;
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
        // Ladders can be placed on platforms
        if (!GameContent.TILE_LADDER.canPlace(world, x, y, layer, player))
            return false;

        if (layer != TileLayer.MAIN)
            return false;

        ItemInstance held = player.getInv().get(player.getSelectedSlot());
        TileState state = world.getState(x, y);

        if (!world.isClient() && held.getItem() == GameContent.TILE_LADDER.getItem() && !state.get(StaticTileProps.HAS_LADDER)) {
            world.setState(x, y, world.getState(x, y).cycleProp(StaticTileProps.HAS_LADDER));
            held.removeAmount(1);
            return true;
        }
        return false;
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer) {
        List<ItemInstance> drops = super.getDrops(world, x, y, layer, destroyer);
        if (world.getState(x, y).get(StaticTileProps.HAS_LADDER))
            drops.addAll(GameContent.TILE_LADDER.getDrops(world, x, y, layer, destroyer));

        return drops;
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player) {
        if (world.getState(TileLayer.BACKGROUND, x, y).getTile().isFullTile())
            return true;

        TileState left = world.getState(x - 1, y);
        if (left.getTile().isFullTile() || left.getTile() instanceof TilePlatform)
            return true;

        TileState right = world.getState(x + 1, y);
        if (right.getTile().isFullTile() || right.getTile() instanceof TilePlatform)
            return true;

        return false;
    }

    @Override
    public boolean canClimb(IWorld world, int x, int y, TileLayer layer, TileState state, BoundBox entityBox, BoundBox entityBoxMotion, List<BoundBox> tileBoxes, Entity entity) {
        return state.get(StaticTileProps.HAS_LADDER) && entityBox.getMinY() < y + 1;
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        if (world.getState(x, y).get(StaticTileProps.HAS_LADDER))
            return GameContent.TILE_LADDER.canStay(world, x, y, layer, changedX, changedY, changedLayer);

        // If tile placed in background
        if (!world.getState(TileLayer.BACKGROUND, x, y).getTile().isAir())
            return true;

        // If tile placed in main on either side of the platform
        if (changedLayer == TileLayer.MAIN && changedY == y && !world.getState(changedX, changedY).getTile().isAir())
            return true;

        // If tile broken from background
        // Then check if the platform is attached anywhere on either side.
        if (changedX == x && changedY == y && changedLayer == TileLayer.BACKGROUND && !world.getState(changedLayer, x, y).getTile().isFullTile())
            return this.canStayFromDirection(world, x, y, Direction.LEFT) || this.canStayFromDirection(world, x, y, Direction.RIGHT);

        // If tile above or below or in any other layer is changed.
        if (changedY != y || changedLayer != TileLayer.MAIN)
            return true;

        Direction dir = Direction.getHorizontal(changedX - x).getOpposite();

        return this.canStayFromDirection(world, x, y, dir);
    }

    // Checks the tiles in the given direction to see if a platform can stay (until unloaded chunks)
    private boolean canStayFromDirection(IWorld world, int x, int y, Direction dir) {
        int checkX = x + dir.x;
        while (world.isPosLoaded(checkX, y)) {
            TileState checkState = world.getState(checkX, y);
            TileState checkBackState = world.getState(TileLayer.BACKGROUND, checkX, y);
            if (checkState.getTile().isFullTile() || checkBackState.getTile().isFullTile())
                return true;
            else if (!(checkState.getTile() instanceof TilePlatform))
                return false;
            checkX += dir.x;
        }
        return true;
    }

    @Override
    public boolean canLiquidSpreadInto(IWorld world, int x, int y, TileLiquid liquid) {
        return true;
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new TilePlatformRenderer(name.addPrefix("wood_"));
    }
}
