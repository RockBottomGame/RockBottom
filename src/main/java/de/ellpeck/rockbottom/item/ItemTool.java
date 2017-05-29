package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.render.item.IItemRenderer;
import de.ellpeck.rockbottom.render.item.ToolItemRenderer;

import java.util.HashMap;
import java.util.Map;

public class ItemTool extends ItemBasic{

    private final Map<ToolType, Integer> toolTypes = new HashMap<>();

    public ItemTool(String name){
        super(name);
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
}
