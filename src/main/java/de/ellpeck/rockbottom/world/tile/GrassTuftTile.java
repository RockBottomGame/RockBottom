package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.GrassTuftTileRenderer;

import java.util.Collections;
import java.util.List;

public class GrassTuftTile extends TileMeta {

    public GrassTuftTile() {
        super(ResourceName.intern("grass_tuft"));
        this.addSubTile(ResourceName.intern("grass_short"));
        this.addSubTile(ResourceName.intern("grass_tall"));
        this.addSubTile(ResourceName.intern("bush"));
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new GrassTuftTileRenderer(name);
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer) {
        return Util.RANDOM.nextDouble() >= 0.25 ? Collections.singletonList(new ItemInstance(GameContent.ITEM_PLANT_FIBER, Util.RANDOM.nextInt(3) + 1)) : Collections.emptyList();
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
        return world.getState(layer, x, y - 1).getTile().canKeepPlants(world, x, y, layer) && world.getState(TileLayer.LIQUIDS, x, y).getTile().isAir();
    }

    @Override
    public boolean canReplace(IWorld world, int x, int y, TileLayer layer) {
        return true;
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }
}
