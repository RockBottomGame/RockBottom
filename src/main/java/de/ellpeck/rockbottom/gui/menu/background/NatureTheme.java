package de.ellpeck.rockbottom.gui.menu.background;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.IMainMenuTheme;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.WorldRenderer;
import de.ellpeck.rockbottom.world.gen.biome.BiomeGrassland;

public class NatureTheme implements IMainMenuTheme{

    private final INoiseGen noiseGen = RockBottomAPI.getApiHandler().makeSimplexNoise(Util.RANDOM.nextLong());

    @Override
    public TileState getState(int x, int y, TileState[][] grid){
        TileState state = BiomeGrassland.getState(TileLayer.MAIN, y, BiomeGrassland.getHeight(TileLayer.MAIN, x, this.noiseGen, 2, 5));
        if(state.getTile().isAir()){
            if(Util.RANDOM.nextFloat() >= 0.45F){
                if(grid[x][y-1].getTile().isFullTile()){
                    TileMeta tile = GameContent.TILE_GRASS_TUFT;
                    int type = Util.floor(Util.RANDOM.nextDouble()*(double)tile.metaProp.getVariants());
                    return tile.getDefState().prop(tile.metaProp, type);
                }
            }
        }
        return state;
    }

    @Override
    public int getBackgroundColor(){
        return WorldRenderer.SKY_COLORS[WorldRenderer.SKY_COLORS.length-1];
    }
}
