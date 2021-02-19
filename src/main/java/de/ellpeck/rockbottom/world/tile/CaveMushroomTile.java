package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.IPotPlantable;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.CaveMushroomTileRenderer;

public class CaveMushroomTile extends TileMeta implements IPotPlantable {

    public CaveMushroomTile() {
        super(ResourceName.intern("cave_mushroom"), false);
        for (int i = 1; i <= 7; i++) {
            this.addSubTile(this.name.addSuffix("." + i));
        }
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new CaveMushroomTileRenderer(name);
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
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return this.canBeHere(world, x, y, layer);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player) {
        return world.isPosLoaded(x, y - 1, false) && this.canBeHere(world, x, y, layer);
    }

    private boolean canBeHere(IWorld world, int x, int y, TileLayer layer) {
        return world.getState(layer, x, y - 1).getTile().hasSolidSurface(world, x, y - 1, layer) && world.getState(TileLayer.LIQUIDS, x, y).getTile().isAir();
    }

    @Override
    public float getXRenderOffset(IWorld world, TileState pot, int x, int y, ItemInstance item) {
        return -1/12f;
    }

    @Override
    public float getRenderYOffset(IWorld world, TileState pot, int x, int y, ItemInstance item) {
        return -4/12f;
    }
}
