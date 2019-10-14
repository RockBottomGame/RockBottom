package de.ellpeck.rockbottom.world.gen.ore;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.world.gen.WorldGenOre;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;

import java.util.Collections;
import java.util.Set;

public class WorldGenTin extends WorldGenOre {

    @Override
    protected int getHighestGridPos() {
        return -3;
    }

    @Override
    protected int getLowestGridPos() {
        return -20;
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
        return GameContent.TILE_TIN.getDefState();
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
