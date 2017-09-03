package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.CombinerRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.entity.IInventoryHolder;
import de.ellpeck.rockbottom.api.tile.entity.TileEntityFueled;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.inventory.TileInventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TileEntityCombiner extends TileEntityFueled implements IInventoryHolder{

    public static final int INPUT_ONE = 0;
    public static final int INPUT_TWO = 1;
    public static final int OUTPUT = 2;
    public static final int COAL = 3;

    public final TileInventory inventory = new TileInventory(this, 4);

    protected int smeltTime;
    protected int maxSmeltTime;

    private int lastSmelt;

    public TileEntityCombiner(IWorld world, int x, int y){
        super(world, x, y);
    }

    @Override
    protected boolean needsSync(){
        return super.needsSync() || this.lastSmelt != this.smeltTime;
    }

    @Override
    protected void onSync(){
        super.onSync();
        this.lastSmelt = this.smeltTime;
    }

    @Override
    protected boolean tryTickAction(){
        boolean hasRecipeAndSpace = false;

        ItemInstance inputOne = this.inventory.get(INPUT_ONE);
        ItemInstance inputTwo = this.inventory.get(INPUT_TWO);
        if(inputOne != null && inputTwo != null){
            CombinerRecipe recipe = RockBottomAPI.getCombinerRecipe(inputOne, inputTwo);
            if(recipe != null){
                IUseInfo recipeInOne = recipe.getInputOne();
                IUseInfo recipeInTwo = recipe.getInputTwo();

                if(!recipeInOne.containsItem(inputOne)){
                    IUseInfo temp = recipeInOne;
                    recipeInOne = recipeInTwo;
                    recipeInTwo = temp;
                }

                if(inputOne.getAmount() >= recipeInOne.getAmount() && inputTwo.getAmount() >= recipeInTwo.getAmount()){
                    ItemInstance recipeOut = recipe.getOutput();
                    ItemInstance output = this.inventory.get(OUTPUT);

                    if(output == null || (output.isEffectivelyEqual(recipeOut) && output.fitsAmount(recipeOut.getAmount()))){
                        hasRecipeAndSpace = true;

                        if(this.coalTime > 0){
                            if(this.maxSmeltTime <= 0){
                                this.maxSmeltTime = recipe.getTime();
                            }

                            this.smeltTime++;
                            if(this.smeltTime >= this.maxSmeltTime){
                                this.inventory.remove(INPUT_ONE, recipeInOne.getAmount());
                                this.inventory.remove(INPUT_TWO, recipeInTwo.getAmount());

                                if(output == null){
                                    this.inventory.set(OUTPUT, recipeOut.copy());
                                }
                                else{
                                    this.inventory.add(OUTPUT, recipeOut.getAmount());
                                }
                            }
                            else{
                                return hasRecipeAndSpace;
                            }
                        }
                        else if(this.smeltTime > 0){
                            this.smeltTime = Math.max(this.smeltTime-2, 0);
                            return hasRecipeAndSpace;
                        }
                    }
                }
            }
        }

        this.smeltTime = 0;
        this.maxSmeltTime = 0;

        return hasRecipeAndSpace;
    }

    @Override
    protected float getFuelModifier(){
        return 0.75F;
    }

    @Override
    protected ItemInstance getFuel(){
        return this.inventory.get(COAL);
    }

    @Override
    protected void removeFuel(){
        this.inventory.remove(COAL, 1);
    }

    @Override
    protected void onActiveChange(boolean active){
        this.world.causeLightUpdate(this.x, this.y);
    }

    public float getSmeltPercentage(){
        return (float)this.smeltTime/(float)this.maxSmeltTime;
    }

    @Override
    public void save(DataSet set, boolean forSync){
        super.save(set, forSync);

        if(!forSync){
            this.inventory.save(set);
        }

        set.addInt("smelt", this.smeltTime);
        set.addInt("max_smelt", this.maxSmeltTime);
    }

    @Override
    public void load(DataSet set, boolean forSync){
        super.load(set, forSync);

        if(!forSync){
            this.inventory.load(set);
        }

        this.smeltTime = set.getInt("smelt");
        this.maxSmeltTime = set.getInt("max_smelt");
    }

    @Override
    public IInventory getInventory(){
        return this.inventory;
    }

    @Override
    public List<Integer> getInputSlots(ItemInstance instance, Direction dir){
        if(RockBottomAPI.getFuelValue(instance) > 0){
            return Collections.singletonList(COAL);
        }
        else if(RockBottomAPI.isCombinerInput(instance, this.inventory.get(INPUT_TWO))){
            return Collections.singletonList(INPUT_ONE);
        }
        else if(RockBottomAPI.isCombinerInput(instance, this.inventory.get(INPUT_ONE))){
            return Collections.singletonList(INPUT_TWO);
        }
        else{
            return Collections.emptyList();
        }
    }

    @Override
    public List<Integer> getOutputSlots(Direction dir){
        return Collections.singletonList(OUTPUT);
    }

}
