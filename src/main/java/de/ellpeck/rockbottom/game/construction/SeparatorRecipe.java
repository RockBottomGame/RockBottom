package de.ellpeck.rockbottom.game.construction;

import de.ellpeck.rockbottom.api.item.ItemInstance;

public class SeparatorRecipe{

    private final ItemInstance input;
    private final ItemInstance output;
    private final ItemInstance byproduct;
    private final int time;
    private final float byproductChance;

    public SeparatorRecipe(ItemInstance output, ItemInstance input, int time, ItemInstance byproduct, float byproductChance){
        this.input = input;
        this.output = output;
        this.byproduct = byproduct;
        this.time = time;
        this.byproductChance = byproductChance;

        if(this.byproductChance <= 0F || this.byproductChance > 1F){
            throw new IllegalArgumentException("Byproduct chance of separator recipe "+this+" is out of bounds: "+this.byproductChance+", should be between 0 and 1");
        }
    }

    public ItemInstance getInput(){
        return this.input;
    }

    public ItemInstance getOutput(){
        return this.output;
    }

    public ItemInstance getByproduct(){
        return this.byproduct;
    }

    public int getTime(){
        return this.time;
    }

    public float getByproductChance(){
        return this.byproductChance;
    }
}
