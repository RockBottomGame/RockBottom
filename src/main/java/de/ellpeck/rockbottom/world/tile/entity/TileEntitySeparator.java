package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.SeparatorRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.entity.TileEntityFueled;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.inventory.TileInventory;

public class TileEntitySeparator extends TileEntityFueled{

    public static final int INPUT = 0;
    public static final int OUTPUT = 1;
    public static final int BYPRODUCT = 2;
    public static final int COAL = 3;

    public final TileInventory inventory = new TileInventory(this, 4);

    protected int smeltTime;
    protected int maxSmeltTime;

    private int lastSmelt;

    public TileEntitySeparator(IWorld world, int x, int y){
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
            SeparatorRecipe recipe = RockBottomAPI.getSeparatorRecipe(input);
            if(recipe != null){
                IUseInfo recipeIn = recipe.getInput();

                if(input.getAmount() >= recipeIn.getAmount()){
                    ItemInstance recipeOut = recipe.getOutput();
                    ItemInstance output = this.inventory.get(OUTPUT);

                    if(output == null || (output.isEffectivelyEqual(recipeOut) && output.fitsAmount(recipeOut.getAmount()))){
                        ItemInstance recipeBy = recipe.getByproduct();
                        ItemInstance byproduct = this.inventory.get(BYPRODUCT);

                        if(recipeBy == null || byproduct == null || (byproduct.isEffectivelyEqual(recipeBy) && byproduct.fitsAmount(recipeBy.getAmount()))){
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

                                    if(recipeBy != null){
                                        if(Util.RANDOM.nextFloat() <= recipe.getByproductChance()){
                                            if(byproduct == null){
                                                this.inventory.set(BYPRODUCT, recipeBy.copy());
                                            }
                                            else{
                                                this.inventory.add(BYPRODUCT, recipeBy.getAmount());
                                            }
                                        }
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
}
