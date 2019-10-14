package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.construction.smelting.CombinerRecipe;
import de.ellpeck.rockbottom.api.construction.smelting.FuelInput;
import de.ellpeck.rockbottom.api.construction.smelting.SmeltingRecipe;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.CombinedInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.tile.entity.SyncedInt;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.entity.TileInventory;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileEntityCombiner extends TileEntity {

    private final TileInventory inputInv1 = new TileInventory(this, inst -> {
        ItemInstance other = this.getTileInventory().get(1);
        if (other == null) return CombinerRecipe.forInput(inst) != null;
        else               return CombinerRecipe.forInputs(inst, other) != null;

    });
    private final TileInventory inputInv2 = new TileInventory(this, inst -> {
        ItemInstance other = this.getTileInventory().get(0);
        if (other == null) return CombinerRecipe.forInput(inst) != null;
        else               return CombinerRecipe.forInputs(inst, other) != null;
    });
    private final TileInventory fuelInv = new TileInventory(this, inst -> FuelInput.getFuelTime(inst) > 0);
    private final TileInventory outputInv = new TileInventory(this);
    private final CombinedInventory inventory = new CombinedInventory(this.inputInv1, this.inputInv2, this.fuelInv, this.outputInv);

    private final SyncedInt smeltTime = new SyncedInt("smelt_time");
    private final SyncedInt maxSmeltTime = new SyncedInt("max_smelt_time");
    private final SyncedInt fuelTime = new SyncedInt("fuel_time");
    private final SyncedInt maxFuelTime = new SyncedInt("max_fuel_time");
    private ItemInstance scheduledOutput;
    private boolean lastActive;

    public TileEntityCombiner(IWorld world, int x, int y, TileLayer layer) {
        super(world, x, y, layer);
    }

    @Override
    public IFilteredInventory getTileInventory() {
        return this.inventory;
    }

    @Override
    public boolean doesTick() {
        return true;
    }

    @Override
    public void update(IGameInstance game) {
        super.update(game);

        if (!this.world.isClient()) {
            if (this.maxSmeltTime.get() <= 0) {
                ItemInstance input1 = this.inputInv1.get(0);
                ItemInstance input2 = this.inputInv2.get(0);
                if (input1 != null && input2 != null) {
                    CombinerRecipe recipe = CombinerRecipe.forInputs(input1, input2);
                    if (recipe != null) {
                        IUseInfo r1 = recipe.getInput1();
                        IUseInfo r2 = recipe.getInput2();
                        IUseInfo recipeInput1;
                        IUseInfo recipeInput2;
                        if (r1.containsItem(input1)) {
                            recipeInput1 = r1;
                            recipeInput2 = r2;
                        } else {
                            recipeInput1 = r2;
                            recipeInput2 = r1;
                        }
                        if (input1.getAmount() >= recipeInput1.getAmount() && input2.getAmount() >= recipeInput2.getAmount()) {
                            ItemInstance output = recipe.getOutput();
                            ItemInstance currentOutput = this.outputInv.get(0);

                            if (currentOutput == null || (currentOutput.isEffectivelyEqual(output) && currentOutput.fitsAmount(output.getAmount()))) {
                                this.maxSmeltTime.set(recipe.getTime());
                                this.scheduledOutput = output.copy();
                                this.inputInv1.remove(0, recipeInput1.getAmount());
                                this.inputInv2.remove(0, recipeInput2.getAmount());
                            }
                        }
                    }
                }

                if (this.maxSmeltTime.get() <= 0) {
                    this.scheduledOutput = null;
                }
            } else {
                if (this.fuelTime.get() <= 0) {
                    ItemInstance fuel = this.fuelInv.get(0);
                    if (fuel != null) {
                        int time = FuelInput.getFuelTime(fuel);
                        if (time > 0) {
                            this.fuelTime.set(time);
                            this.maxFuelTime.set(time);
                            this.fuelInv.remove(0, 1);
                        }
                    }

                    if (this.fuelTime.get() <= 0 && this.smeltTime.get() > 0) {
                        this.smeltTime.remove(1);
                    }
                } else {
                    if (Util.RANDOM.nextFloat() >= 0.45F) {
                        this.smeltTime.add(1);
                    }

                    if (this.smeltTime.get() >= this.maxSmeltTime.get()) {
                        ItemInstance currentOutput = this.outputInv.get(0);
                        if (currentOutput != null && currentOutput.isEffectivelyEqual(this.scheduledOutput)) {
                            this.outputInv.add(0, this.scheduledOutput.getAmount());
                        } else {
                            this.outputInv.set(0, this.scheduledOutput);
                        }

                        this.scheduledOutput = null;
                        this.smeltTime.set(0);
                        this.maxSmeltTime.set(0);
                    }
                }
            }

            if (this.fuelTime.get() > 0) {
                this.fuelTime.remove(1);
            }
        }

        boolean active = this.isActive();
        if (active != this.lastActive) {
            this.lastActive = active;
            this.world.causeLightUpdate(this.x, this.y);
        }
    }

    @Override
    protected boolean needsSync() {
        return this.smeltTime.needsSync() || this.maxSmeltTime.needsSync() || this.fuelTime.needsSync() || this.maxFuelTime.needsSync();
    }

    @Override
    protected void onSync() {
        this.smeltTime.onSync();
        this.maxSmeltTime.onSync();
        this.fuelTime.onSync();
        this.maxFuelTime.onSync();
    }

    public float getFuelPercentage() {
        return this.maxFuelTime.get() > 0 ? this.fuelTime.get() / (float) this.maxFuelTime.get() : 0;
    }

    public float getSmeltPercentage() {
        return this.maxSmeltTime.get() > 0 ? this.smeltTime.get() / (float) this.maxSmeltTime.get() : 0;
    }

    public boolean isActive() {
        return this.smeltTime.get() > 0 || this.fuelTime.get() > 0;
    }

    @Override
    public void save(DataSet set, boolean forSync) {
        if (!forSync) {
            this.inventory.save(set);

            if (this.scheduledOutput != null) {
                DataSet sub = new DataSet();
                this.scheduledOutput.save(sub);
                set.addDataSet("output", sub);
            }
        }

        this.smeltTime.save(set);
        this.maxSmeltTime.save(set);
        this.fuelTime.save(set);
        this.maxFuelTime.save(set);
    }

    @Override
    public void load(DataSet set, boolean forSync) {
        if (!forSync) {
            this.inventory.load(set);

            if (set.hasKey("output")) {
                DataSet sub = set.getDataSet("output");
                this.scheduledOutput = ItemInstance.load(sub);
            }
        }

        this.smeltTime.load(set);
        this.maxSmeltTime.load(set);
        this.fuelTime.load(set);
        this.maxFuelTime.load(set);
    }
}
