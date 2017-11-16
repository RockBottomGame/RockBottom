package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileChest;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityChest;

public class TileChestRenderer extends DefaultTileRenderer<TileChest>{

    private final IResourceName texOpen;

    public TileChestRenderer(IResourceName texture){
        super(texture);
        this.texOpen = this.texture.addSuffix(".open");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g, IWorld world, TileChest tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light){
        TileEntityChest tileEntity = world.getTileEntity(layer, x, y, TileEntityChest.class);
        if(tileEntity != null && tileEntity.getOpenCount() > 0){
            manager.getTexture(this.texOpen).draw(renderX, renderY, scale, scale, light);
        }
        else{
            super.render(game, manager, g, world, tile, state, x, y, layer, renderX, renderY, scale, light);
        }
    }
}
