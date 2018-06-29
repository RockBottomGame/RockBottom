package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.TileGrassTuftRenderer;

import java.util.Collections;
import java.util.List;

public class TileGrassTuft extends TileMeta {

    public TileGrassTuft() {
        super(ResourceName.intern("grass_tuft"));
        this.addSubTile(ResourceName.intern("grass_short"));
        this.addSubTile(ResourceName.intern("grass_tall"));
        this.addSubTile(ResourceName.intern("bush"));
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new TileGrassTuftRenderer();
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer) {
        return Util.RANDOM.nextDouble() >= 0.25 ? Collections.singletonList(new ItemInstance(GameContent.ITEM_PLANT_FIBER, Util.RANDOM.nextInt(3) + 1)) : Collections.emptyList();
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return world.getState(layer, x, y - 1).getTile().canKeepPlants(world, x, y, layer);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player) {
        return world.isPosLoaded(x, y - 1, false) && world.getState(layer, x, y - 1).getTile().canKeepPlants(world, x, y, layer);
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
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer) {
        return null;
    }
}
