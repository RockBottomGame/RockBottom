package de.ellpeck.rockbottom.game.item;

import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.render.item.IItemRenderer;
import de.ellpeck.rockbottom.game.world.World;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.game.render.item.ToolItemRenderer;
import de.ellpeck.rockbottom.api.world.TileLayer;

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
    public float getMiningSpeed(IWorld world, int x, int y, TileLayer layer, Tile tile, boolean isRightTool){
        return isRightTool ? this.miningSpeed : super.getMiningSpeed(world, x, y, layer, tile, isRightTool);
    }
}
