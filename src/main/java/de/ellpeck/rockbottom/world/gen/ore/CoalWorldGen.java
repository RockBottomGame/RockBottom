package de.ellpeck.rockbottom.world.gen.ore;

import com.google.common.collect.Sets;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.gen.WorldGenOre;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;

import java.util.Set;

public class CoalWorldGen extends WorldGenOre {

    public CoalWorldGen() {
        super(ResourceName.intern("coal"));
    }

    @Override
    protected int getHighestGridPos() {
        return 0;
    }

    @Override
    protected int getMaxAmount() {
        return 8;
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
        return GameContent.Tiles.COAL.getDefState();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected Set<Biome> getAllowedBiomes() {
        return Sets.newHashSet(GameContent.Biomes.GRASSLAND, GameContent.Biomes.UNDERGROUND);
    }
}
