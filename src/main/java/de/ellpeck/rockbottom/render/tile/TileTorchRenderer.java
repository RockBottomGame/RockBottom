package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileTorch;

public class TileTorchRenderer extends DefaultTileRenderer<TileTorch>{

    private final IResourceName texLeft;
    private final IResourceName texRight;
    private final IResourceName texBack;

    public TileTorchRenderer(IResourceName texture){
        super(texture);
        this.texLeft = this.texture.addSuffix(".left");
        this.texRight = this.texture.addSuffix(".right");
        this.texBack = this.texture.addSuffix(".back");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g, IWorld world, TileTorch tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light){
        int meta = state.get(StaticTileProps.TORCH_FACING);
        if(meta == 0){
            super.render(game, manager, g, world, tile, state, x, y, layer, renderX, renderY, scale, light);
        }
        else{
            IResourceName tex;

            if(meta == 1){
                tex = this.texRight;
            }
            else if(meta == 2){
                tex = this.texLeft;
            }
            else{
                tex = this.texBack;
            }

            manager.getTexture(tex).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
        }
    }
}
