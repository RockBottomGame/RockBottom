package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.render.tile.TileMetaRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileGrassTuft;

public class TileGrassTuftRenderer extends TileMetaRenderer<TileGrassTuft>{

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileGrassTuft tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light){
        float topX = renderX+(float)Math.sin((world.getTotalTime()+(x*50))/25D%(2*Math.PI))*scale/15F;
        ITexture texture = this.getTexture(manager, tile, state.get(tile.metaProp)).getPositionalVariation(x, y);
        texture.draw(topX, renderY, renderX, renderY+scale, renderX+scale, renderY+scale, topX+scale, renderY, 0, 0, texture.getRenderWidth(), texture.getRenderHeight(), light, Colors.WHITE);
    }
}
