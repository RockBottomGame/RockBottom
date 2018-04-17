package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileGrass;

public class TileGrassRenderer extends DefaultTileRenderer<TileGrass>{

    private final ResourceName texSnowy;

    public TileGrassRenderer(ResourceName texture){
        super(texture);
        this.texSnowy = this.texture.addSuffix("_snowy");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileGrass tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light){
        if(world.getState(x, y+1).getTile().makesGrassSnowy(world, x, y+1, layer)){
            manager.getTexture(this.texSnowy).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
        }
        else{
            super.render(game, manager, g, world, tile, state, x, y, layer, renderX, renderY, scale, light);
        }
    }
}
