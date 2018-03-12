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
import de.ellpeck.rockbottom.world.tile.TileCopper;

public class TileCopperRenderer extends DefaultTileRenderer<TileCopper>{

    private final IResourceName canister;

    public TileCopperRenderer(IResourceName texture){
        super(texture);
        this.canister = this.texture.addSuffix(".canister");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileCopper tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light){
        if(state.get(StaticTileProps.HAS_CANISTER)){
            manager.getTexture(this.canister).draw(renderX, renderY, scale, scale, light);
        }
        else{
            super.render(game, manager, g, world, tile, state, x, y, layer, renderX, renderY, scale, light);
        }
    }
}
