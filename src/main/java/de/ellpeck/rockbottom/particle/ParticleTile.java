/*
 * This file ("ParticleTile.java") is part of the RockBottomAPI by Ellpeck.
 * View the source code at <https://github.com/Ellpeck/RockBottomAPI>.
 *
 * The RockBottomAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The RockBottomAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the RockBottomAPI. If not, see <http://www.gnu.org/licenses/>.
 */

package de.ellpeck.rockbottom.particle;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.tex.Texture;
import de.ellpeck.rockbottom.api.particle.Particle;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ParticleTile extends Particle{

    private final TileState state;
    private Color renderPixel;

    public ParticleTile(IWorld world, double x, double y, double motionX, double motionY, TileState state){
        super(world, x, y, motionX, motionY, Util.RANDOM.nextInt(30)+10);
        this.state = state;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, float x, float y, Color filter){
        if(this.renderPixel == null){
            ITileRenderer renderer = this.state.getTile().getRenderer();
            if(renderer != null){
                Texture texture = renderer.getParticleTexture(game, manager, g, this.state.getTile(), this.state);
                if(texture != null){
                    int width = texture.getWidth();
                    int height = texture.getHeight();

                    int pixelX = Util.RANDOM.nextInt(width);
                    int pixelY = Util.RANDOM.nextInt(height);
                    this.renderPixel = texture.getColor(pixelX, pixelY).multiply(filter);
                }
            }
        }

        if(this.renderPixel != null){
            g.setColor(this.renderPixel);
            g.fillRect(x, y, 0.12F, 0.12F);
        }
    }
}
