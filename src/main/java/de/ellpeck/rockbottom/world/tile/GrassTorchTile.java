package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class GrassTorchTile extends TorchTile {

    public GrassTorchTile() {
        super(ResourceName.intern("torch_grass"));
    }

    @Override
    public double getTurnOffChance() {
        return 0.8;
    }

    @Override
    public int getMaxLight() {
        return 50;
    }
}
