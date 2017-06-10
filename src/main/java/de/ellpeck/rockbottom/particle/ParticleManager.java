package de.ellpeck.rockbottom.particle;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.particle.IParticleManager;
import de.ellpeck.rockbottom.api.particle.Particle;
import de.ellpeck.rockbottom.api.particle.ParticleTile;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.render.WorldRenderer;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.World;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager implements IParticleManager{

    private final List<Particle> particles = new ArrayList<>();

    public void update(IGameInstance game){
        for(int i = 0; i < this.particles.size(); i++){
            Particle particle = this.particles.get(i);
            particle.update(game);

            if(particle.isDead()){
                this.particles.remove(i);
                i--;
            }
        }
    }

    public void render(IGameInstance game, IAssetManager manager, Graphics g, World world, float transX, float transY){
        this.particles.forEach(particle -> {
            int light = world.getCombinedLight(Util.floor(particle.x), Util.floor(particle.y));
            particle.render(game, manager, g, (float)particle.x-transX, (float)-particle.y-transY+1F, WorldRenderer.MAIN_COLORS[game.isLightDebug() ? Constants.MAX_LIGHT : light]);
        });
    }

    @Override
    public void addParticle(Particle particle){
        this.particles.add(particle);
    }

    @Override
    public void addTileParticles(IWorld world, int x, int y, Tile tile, int meta){
        for(int i = 0; i < 15; i++){
            double motionX = Util.RANDOM.nextGaussian()*0.1F;
            double motionY = Util.RANDOM.nextGaussian()*0.1F;

            Particle particle = new ParticleTile(world, x+0.5, y+0.5, motionX, motionY, tile, meta);
            this.addParticle(particle);
        }
    }

    @Override
    public int getAmount(){
        return this.particles.size();
    }
}
