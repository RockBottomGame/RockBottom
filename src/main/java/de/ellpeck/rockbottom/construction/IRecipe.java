package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.item.ItemInstance;

import java.util.List;

public interface IRecipe{

    List<ItemInstance> getInputs();

    List<ItemInstance> getOutputs();

}
