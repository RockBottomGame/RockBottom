package de.ellpeck.rockbottom.api.particle;

import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.IWorld;

public interface IParticleManager{

    void addParticle(Particle particle);

    void addTileParticles(IWorld world, int x, int y, Tile tile, int meta);

    int getAmount();
}
