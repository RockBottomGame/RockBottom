package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.SmelterRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.entity.IInventoryHolder;
import de.ellpeck.rockbottom.api.tile.entity.TileEntityFueled;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.inventory.TileInventory;

import java.util.Collections;
import java.util.List;

public class TileEntitySmelter extends TileEntityFueled implements IInventoryHolder{

    public static final int INPUT = 0;
    public static final int OUTPUT = 1;
    public static final int COAL = 2;

    public final TileInventory inventory = new TileInventory(this, 3);

    protected int smeltTime;
    protected int maxSmeltTime;

    private int lastSmelt;

    public TileEntitySmelter(IWorld world, int x, int y){
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

        ItemInstance input = this.inventory.get(INPUT);
        if(input != null){
            SmelterRecipe recipe = RockBottomAPI.getSmelterRecipe(input);
            if(recipe != null){
                IUseInfo recipeIn = recipe.getInput();

                if(input.getAmount() >= recipeIn.getAmount()){
                    ItemInstance recipeOut = recipe.getOutput();
                    ItemInstance output = this.inventory.get(OUTPUT);

                    if(output == null || (output.isEffectivelyEqual(recipeOut) && output.getAmount()+recipeOut.getAmount() <= output.getMaxAmount())){
                        hasRecipeAndSpace = true;

                        if(this.coalTime > 0){
                            if(this.maxSmeltTime <= 0){
                                this.maxSmeltTime = recipe.getTime();
                            }

                            this.smeltTime++;
                            if(this.smeltTime >= this.maxSmeltTime){
                                this.inventory.remove(INPUT, recipeIn.getAmount());

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
        return 1F;
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
        else if(RockBottomAPI.getSmelterRecipe(instance) != null){
            return Collections.singletonList(INPUT);
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
