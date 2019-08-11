package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolProperty;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileSoil extends TileBasic {

    public TileSoil() {
        super(ResourceName.intern("soil"));
    }

    @Override
    public boolean canGrassSpreadTo(IWorld world, int x, int y, TileLayer layer) {
        return Util.RANDOM.nextInt(30) <= 0 && !world.getState(layer, x, y + 1).getTile().isFullTile();
    }

    @Override
    public boolean canKeepPlants(IWorld world, int x, int y, TileLayer layer) {
        return true;
    }

    @Override
    public float getTranslucentModifier(IWorld world, int x, int y, TileLayer layer, boolean skylight) {
        return layer == TileLayer.BACKGROUND ? 0.9F : 0.7F;
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
        ItemInstance selected = player.getInv().get(player.getSelectedSlot());
        if (selected != null && selected.getItem().getToolProperties(selected).containsKey(ToolProperty.HOE)) {
            if (!world.getState(layer, x, y + 1).getTile().hasSolidSurface(world, x, y + 1, layer)) {
                if (!world.isClient()) {
                    world.setState(layer, x, y, GameContent.TILE_SOIL_TILLED.getDefState());
                    selected.getItem().takeDamage(selected, player, 1);
                }
                return true;
            }
        }
        return false;
    }
}
