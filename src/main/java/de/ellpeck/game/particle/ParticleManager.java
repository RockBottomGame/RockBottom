package de.ellpeck.game.particle;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.tile.Tile;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager{

    private final List<Particle> particles = new ArrayList<>();

    public void update(Game game){
        for(int i = 0; i < this.particles.size(); i++){
            Particle particle = this.particles.get(i);
            particle.update(game);

            if(particle.isDead()){
                this.particles.remove(i);
                i--;
            }
        }
    }

    public void render(Game game, AssetManager manager, Graphics g){
        this.particles.forEach(particle -> particle.render(game, manager, g, (float)particle.x, (float)-particle.y+1F));
    }

    public void addParticle(Particle particle){
        this.particles.add(particle);
    }

    public void addTileParticles(World world, int x, int y, Tile tile, byte meta){
        for(int i = 0; i < 15; i++){
            double motionX = world.rand.nextGaussian()*0.1F;
            double motionY = world.rand.nextGaussian()*0.1F;

            Particle particle = new ParticleTile(world, x+0.5, y+0.5, motionX, motionY, tile, meta);
            this.addParticle(particle);
        }
    }

    public int getAmount(){
        return this.particles.size();
    }
}
