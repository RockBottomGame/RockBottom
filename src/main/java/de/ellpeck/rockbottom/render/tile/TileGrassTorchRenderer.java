package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileTorch;

public class TileGrassTorchRenderer extends DefaultTileRenderer<TileTorch>{

    private final IResourceName[] texNormal = new IResourceName[2];
    private final IResourceName[] texLeft = new IResourceName[2];
    private final IResourceName[] texRight = new IResourceName[2];
    private final IResourceName[] texBack = new IResourceName[2];

    public TileGrassTorchRenderer(IResourceName texture){
        super(texture.addSuffix(".off"));

        IResourceName tileTexture = texture.addPrefix("tiles.");
        for(int i = 0; i < 2; i++){
            String suffix = i == 0 ? ".on" : ".off";
            this.texNormal[i] = tileTexture.addSuffix(suffix);
            this.texLeft[i] = tileTexture.addSuffix(".left"+suffix);
            this.texRight[i] = tileTexture.addSuffix(".right"+suffix);
            this.texBack[i] = tileTexture.addSuffix(".back"+suffix);
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileTorch tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light){
        int meta = state.get(StaticTileProps.TORCH_FACING);

        IResourceName[] tex;
        if(meta == 0){
            tex = this.texNormal;
        }
        else if(meta == 1){
            tex = this.texRight;
        }
        else if(meta == 2){
            tex = this.texLeft;
        }
        else{
            tex = this.texBack;
        }

        IResourceName name = tex[state.get(StaticTileProps.TORCH_TIMER) < 4 ? 0 : 1];
        manager.getTexture(name).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
    }
}
