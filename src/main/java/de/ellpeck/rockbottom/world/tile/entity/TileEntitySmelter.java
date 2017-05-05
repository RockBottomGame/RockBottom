package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.construction.SmelterRecipe;
import de.ellpeck.rockbottom.data.set.DataSet;
import de.ellpeck.rockbottom.inventory.TileInventory;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.world.World;
import io.netty.buffer.ByteBuf;

public class TileEntitySmelter extends TileEntity{

    public static final int INPUT = 0;
    public static final int OUTPUT = 1;
    public static final int COAL = 2;

    public final TileInventory inventory = new TileInventory(this, 3);

    public int smeltTime;
    public int maxSmeltTime;

    public int coalTime;
    public int maxCoalTime;

    private int lastSmelt;
    private int lastCoal;
    private boolean lastActive;

    public TileEntitySmelter(World world, int x, int y){
        super(world, x, y);
    }

    @Override
    public void update(RockBottom game){
        if(!NetHandler.isClient()){
            boolean smelted = this.doSmelting();

            if(this.coalTime > 0){
                this.coalTime--;
            }

            if(smelted){
                if(this.coalTime <= 0){
                    ItemInstance inst = this.inventory.get(COAL);
                    if(inst != null){
                        int amount = ConstructionRegistry.getFuelValue(inst);
                        if(amount > 0){
                            this.maxCoalTime = amount;
                            this.coalTime = amount;

                            this.inventory.remove(COAL, 1);
                        }
                    }
                }
            }

            if((this.lastCoal != this.coalTime || this.lastSmelt != this.smeltTime) && this.world.info.totalTimeInWorld%10 == 0){
                this.lastCoal = this.coalTime;
                this.lastSmelt = this.smeltTime;

                this.sendToClients();
            }

            boolean active = this.isActive();
            if(this.lastActive != active){
                this.lastActive = active;

                this.world.causeLightUpdate(this.x, this.y);
            }
        }
    }

    private boolean doSmelting(){
        boolean hasRecipeAndSpace = false;

        ItemInstance input = this.inventory.get(INPUT);
        if(input != null){
            SmelterRecipe recipe = ConstructionRegistry.getSmelterRecipe(input);
            if(recipe != null){
                ItemInstance recipeIn = recipe.getInput();

                if(input.getAmount() >= recipeIn.getAmount()){
                    ItemInstance recipeOut = recipe.getOutput();
                    ItemInstance output = this.inventory.get(OUTPUT);

                    if(output == null || (output.isItemEqual(recipeOut) && output.getAmount()+recipeOut.getAmount() <= output.getMaxAmount())){
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

    public boolean isActive(){
        return this.coalTime > 0;
    }

    @Override
    public void save(DataSet set){
        this.inventory.save(set);

        set.addInt("smelt", this.smeltTime);
        set.addInt("max_smelt", this.maxSmeltTime);
        set.addInt("coal", this.coalTime);
        set.addInt("max_coal", this.maxCoalTime);
    }

    @Override
    public void load(DataSet set){
        this.inventory.load(set);

        this.smeltTime = set.getInt("smelt");
        this.maxSmeltTime = set.getInt("max_smelt");
        this.coalTime = set.getInt("coal");
        this.maxCoalTime = set.getInt("max_coal");
    }

    @Override
    public void toBuf(ByteBuf buf){
        buf.writeInt(this.smeltTime);
        buf.writeInt(this.maxSmeltTime);
        buf.writeInt(this.coalTime);
        buf.writeInt(this.maxCoalTime);
    }

    @Override
    public void fromBuf(ByteBuf buf){
        this.smeltTime = buf.readInt();
        this.maxSmeltTime = buf.readInt();
        this.coalTime = buf.readInt();
        this.maxCoalTime = buf.readInt();
    }
}
