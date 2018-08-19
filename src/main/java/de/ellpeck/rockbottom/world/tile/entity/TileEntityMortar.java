package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.construction.MortarRecipe;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.entity.TileInventory;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Collections;

public class TileEntityMortar extends TileEntity {

    private final TileInventory inventory = new TileInventory(this, 3, Collections.emptyList());
    private MortarRecipe currentRecipe;
    private int progress;

    public TileEntityMortar(IWorld world, int x, int y, TileLayer layer) {
        super(world, x, y, layer);
        this.inventory.addChangeCallback((inv, slot) -> {
            System.out.println("CALLBACK YO");
            this.sendToClients();
        });
    }

    public void doPestleProgress() {
        if (!this.world.isClient()) {
            MortarRecipe recipe = MortarRecipe.forInput(new ItemInstance[]{this.inventory.get(0), this.inventory.get(1), this.inventory.get(2)});

            if (recipe != this.currentRecipe) {
                this.currentRecipe = recipe;
                this.progress = 0;
            }

            if (recipe != null) {
                this.progress++;
                if (this.progress >= recipe.getTime()) {
                    for (int i = 0; i < this.inventory.getSlotAmount(); i++) {
                        ItemInstance toSet = recipe.getOutput()[i];
                        this.inventory.set(i, toSet == null ? null : toSet.copy());
                    }

                    this.currentRecipe = null;
                    this.progress = 0;
                } else {
                    this.sendToClients();
                }
            }
        }
    }

    public float getProgress() {
        if (this.currentRecipe != null) {
            return this.progress / (float) this.currentRecipe.getTime();
        } else {
            return -1F;
        }
    }

    @Override
    public boolean doesTick() {
        return false;
    }

    @Override
    public IFilteredInventory getTileInventory() {
        return this.inventory;
    }

    @Override
    public void save(DataSet set, boolean forSync) {
        this.inventory.save(set);
        set.addInt("progress", this.progress);
        if (this.currentRecipe != null) {
            set.addString("recipe", this.currentRecipe.getName().toString());
        }
    }

    @Override
    public void load(DataSet set, boolean forSync) {
        this.inventory.load(set);
        this.progress = set.getInt("progress");
        if (set.hasKey("recipe")) {
            this.currentRecipe = Registries.MORTAR_REGISTRY.get(new ResourceName(set.getString("recipe")));
        }
    }
}
