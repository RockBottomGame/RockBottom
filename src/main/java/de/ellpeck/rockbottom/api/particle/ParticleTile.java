package de.ellpeck.rockbottom.api.particle;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.IWorld;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ParticleTile extends Particle{

    private final Tile tile;
    private final int meta;
    private Color renderPixel;

    public ParticleTile(IWorld world, double x, double y, double motionX, double motionY, Tile tile, int meta){
        super(world, x, y, motionX, motionY, RockBottomAPI.RANDOM.nextInt(30)+10);
        this.tile = tile;
        this.meta = meta;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, float x, float y, Color filter){
        if(this.renderPixel == null){
            ITileRenderer renderer = this.tile.getRenderer();
            if(renderer != null){
                Image texture = renderer.getParticleTexture(game, manager, g, this.tile, this.meta);
                if(texture != null){
                    int width = texture.getWidth();
                    int height = texture.getHeight();

                    int pixelX = RockBottomAPI.RANDOM.nextInt(width);
                    int pixelY = RockBottomAPI.RANDOM.nextInt(height);
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
