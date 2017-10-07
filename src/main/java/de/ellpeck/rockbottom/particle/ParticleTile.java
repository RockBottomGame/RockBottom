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
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.particle.Particle;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;

public class ParticleTile extends Particle{

    private final TileState state;
    private int renderPixel = -1;

    public ParticleTile(IWorld world, double x, double y, double motionX, double motionY, TileState state){
        super(world, x, y, motionX, motionY, Util.RANDOM.nextInt(30)+10);
        this.state = state;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g, float x, float y, int filter){
        if(this.renderPixel == -1){
            ITileRenderer renderer = this.state.getTile().getRenderer();
            if(renderer != null){
                ITexture texture = renderer.getParticleTexture(game, manager, g, this.state.getTile(), this.state);
                if(texture != null){
                    int width = texture.getWidth();
                    int height = texture.getHeight();

                    int pixelX = Util.RANDOM.nextInt(width);
                    int pixelY = Util.RANDOM.nextInt(height);
                    this.renderPixel = Colors.multiply(texture.getTextureColor(pixelX, pixelY), filter);
                }
            }
        }

        if(this.renderPixel != -1){
            g.fillRect(x, y, 0.12F, 0.12F, this.renderPixel);
        }
    }
}
