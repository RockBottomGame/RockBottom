package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.item.ItemInstance;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BasicRecipe implements IRecipe{

    private final List<ItemInstance> inputs;
    private final List<ItemInstance> outputs;

    public BasicRecipe(ItemInstance output, ItemInstance... inputs){
        this.inputs = Arrays.asList(inputs);
        this.outputs = Collections.singletonList(output);
    }

    @Override
    public List<ItemInstance> getInputs(){
        return this.inputs;
    }

    @Override
    public List<ItemInstance> getOutputs(){
        return this.outputs;
    }
}
