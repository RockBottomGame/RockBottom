package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolProperty;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Collections;
import java.util.List;

public class TileTilledSoil extends TileBasic {

    public TileTilledSoil() {
        super(ResourceName.intern("soil_tilled"));
    }

    @Override
    public boolean canKeepFarmablePlants(IWorld world, int x, int y, TileLayer layer) {
        return true;
    }

    @Override
    protected boolean hasItem() {
        return false;
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer) {
        return Collections.singletonList(new ItemInstance(GameContent.TILE_SOIL));
    }

    @Override
    public boolean onInteractWithBreakKey(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
        ItemInstance selected = player.getInv().get(player.getSelectedSlot());
        if (selected != null && selected.getItem().getToolProperties(selected).containsKey(ToolProperty.HOE)) {
            if (!world.isClient()) {
                world.setState(layer, x, y, GameContent.TILE_SOIL.getDefState());
                selected.getItem().takeDamage(selected, player, 1);
            }
            return true;
        }
        return false;
    }


    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        super.onChangeAround(world, x, y, layer, changedX, changedY, changedLayer);
        if (this.shouldDecay(world, x, y, layer)) {
            world.setState(layer, x, y, GameContent.TILE_SOIL.getDefState());
        }
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer) {
        if (this.shouldDecay(world, x, y, layer)) {
            world.setState(layer, x, y, GameContent.TILE_SOIL.getDefState());
        }
    }

    private boolean shouldDecay(IWorld world, int x, int y, TileLayer layer) {
        if (world.isPosLoaded(x, y + 1)) {
            Tile tile = world.getState(layer, x, y + 1).getTile();
            return tile.hasSolidSurface(world, x, y + 1, layer);
        } else {
            return false;
        }
    }
}
