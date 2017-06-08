package de.ellpeck.rockbottom.game.particle;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.render.WorldRenderer;
import de.ellpeck.rockbottom.game.util.Util;
import de.ellpeck.rockbottom.game.world.World;
import de.ellpeck.rockbottom.api.tile.Tile;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager{

    private final List<Particle> particles = new ArrayList<>();

    public void update(RockBottom game){
        for(int i = 0; i < this.particles.size(); i++){
            Particle particle = this.particles.get(i);
            particle.update(game);

            if(particle.isDead()){
                this.particles.remove(i);
                i--;
            }
        }
    }

    public void render(RockBottom game, AssetManager manager, Graphics g, World world){
        this.particles.forEach(particle -> {
            int light = world.getCombinedLight(Util.floor(particle.x), Util.floor(particle.y));
            particle.render(game, manager, g, (float)particle.x, (float)-particle.y+1F, WorldRenderer.MAIN_COLORS[game.isLightDebug() ? Constants.MAX_LIGHT : light]);
        });
    }

    public void addParticle(Particle particle){
        this.particles.add(particle);
    }

    public void addTileParticles(IWorld world, int x, int y, Tile tile, int meta){
        for(int i = 0; i < 15; i++){
            double motionX = Util.RANDOM.nextGaussian()*0.1F;
            double motionY = Util.RANDOM.nextGaussian()*0.1F;

            Particle particle = new ParticleTile(world, x+0.5, y+0.5, motionX, motionY, tile, meta);
            this.addParticle(particle);
        }
    }

    public int getAmount(){
        return this.particles.size();
    }
}
