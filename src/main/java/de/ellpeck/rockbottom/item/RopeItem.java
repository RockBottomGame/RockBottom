package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.TileItem;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class RopeItem extends TileItem {

    public RopeItem(ResourceName name) {
        super(name);
    }

    @Override
    public Tile getTile() {
        return GameContent.Tiles.PLANT_ROPE;
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player, ItemInstance instance) {
        if (super.onInteractWith(world, x, y, layer, mouseX, mouseY, player, instance)) {
            return true;
        }
        for (int i = 0; i < 12; i++) {
            int yOff = y - i;
            TileState offState = world.getState(layer, x, yOff);
            if (super.onInteractWith(world, x, yOff, layer, mouseX, mouseY, player, instance)) {
                return true;
            }
            if (offState.getTile() != this.getTile()) {
                return false;
            }
        }


        return false;
    }
}
