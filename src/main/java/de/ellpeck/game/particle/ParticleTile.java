package de.ellpeck.game.particle;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.render.tile.ITileRenderer;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ParticleTile extends Particle{

    private Color renderPixel;
    private final Tile tile;
    private final int meta;

    public ParticleTile(World world, double x, double y, double motionX, double motionY, Tile tile, int meta){
        super(world, x, y, motionX, motionY, world.rand.nextInt(30)+10);
        this.tile = tile;
        this.meta = meta;
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g, float x, float y, Color filter){
        if(this.renderPixel == null){
            ITileRenderer renderer = this.tile.getRenderer();
            if(renderer != null){
                Image texture = renderer.getParticleTexture(game, manager, g, this.tile, this.meta);
                if(texture != null){
                    int width = texture.getWidth();
                    int height = texture.getHeight();

                    int pixelX = this.world.rand.nextInt(width);
                    int pixelY = this.world.rand.nextInt(height);
                    this.renderPixel = texture.getColor(pixelX, pixelY).multiply(filter);
                }
            }
        }

        if(this.renderPixel != null){
            float scale = 0.4F;

            g.pushTransform();
            g.scale(scale, scale);

            g.setColor(this.renderPixel);
            g.fillRect(x/scale-0.5F*scale, y/scale-0.5F*scale, 1F*scale, 1F*scale);

            g.popTransform();
        }
    }
}
