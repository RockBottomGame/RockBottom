package de.ellpeck.rockbottom.world.gen.ore;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.gen.WorldGenOre;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;

import java.util.Collections;
import java.util.Set;

public class CopperWorldGen extends WorldGenOre {

    public CopperWorldGen() {
        super(ResourceName.intern("copper"));
    }

    @Override
    protected int getHighestGridPos() {
        return -1;
    }

    @Override
    protected int getLowestGridPos() {
        return -50;
    }

    @Override
    protected int getMaxAmount() {
        return 3;
    }

    @Override
    protected int getClusterRadiusX() {
        return 5;
    }

    @Override
    protected int getClusterRadiusY() {
        return 3;
    }

    @Override
    protected TileState getOreState() {
        TileState state = GameContent.TILE_COPPER.getDefState();
        return this.oreRandom.nextInt(30) <= 0 ? state.prop(StaticTileProps.HAS_CANISTER, true) : state;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected Set<Biome> getAllowedBiomes() {
        return Collections.singleton(GameContent.BIOME_UNDERGROUND);
    }
}
