package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.render.item.IItemRenderer;
import de.ellpeck.rockbottom.render.item.ToolItemRenderer;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.tile.Tile;

import java.util.HashMap;
import java.util.Map;

public class ItemTool extends ItemBasic{

    private final float miningSpeed;
    private final Map<ToolType, Integer> toolTypes = new HashMap<>();

    public ItemTool(String name, float miningSpeed){
        super(name);
        this.miningSpeed = miningSpeed;
        this.maxAmount = 1;
    }

    public ItemTool addToolType(ToolType type, int level){
        this.toolTypes.put(type, level);
        return this;
    }

    @Override
    protected IItemRenderer createRenderer(String name){
        return new ToolItemRenderer(name);
    }

    @Override
    public Map<ToolType, Integer> getToolTypes(ItemInstance instance){
        return this.toolTypes;
    }

    @Override
    public float getMiningSpeed(World world, int x, int y, TileLayer layer, Tile tile, boolean isRightTool){
        return isRightTool ? this.miningSpeed : super.getMiningSpeed(world, x, y, layer, tile, isRightTool);
    }
}
