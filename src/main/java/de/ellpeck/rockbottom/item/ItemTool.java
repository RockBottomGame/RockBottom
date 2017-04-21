package de.ellpeck.rockbottom.item;

import java.util.HashMap;
import java.util.Map;

public class ItemTool extends ItemBasic{

    private final Map<ToolType, Integer> toolTypes = new HashMap<>();

    public ItemTool(int id, String name){
        super(id, name);
        this.maxAmount = 1;
    }

    public ItemTool addToolType(ToolType type, int level){
        this.toolTypes.put(type, level);
        return this;
    }

    @Override
    public Map<ToolType, Integer> getToolTypes(ItemInstance instance){
        return this.toolTypes;
    }
}
