package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.StaticTileProps.LogType;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileLog;

import java.util.Locale;

public class TileLogRenderer extends DefaultTileRenderer<TileLog>{

    private final IResourceName[] textures;

    public TileLogRenderer(IResourceName texture){
        super(texture);

        LogType[] types = LogType.values();
        this.textures = new IResourceName[types.length];
        for(int i = 0; i < this.textures.length; i++){
            if(types[i] != LogType.PLACED){
                this.textures[i] = this.texture.addSuffix('.'+types[i].name().toLowerCase(Locale.ROOT));
            }
            else{
                this.textures[i] = this.texture;
            }
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileLog tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light){
        LogType type = state.get(StaticTileProps.LOG_VARIANT);
        IResourceName tex = this.textures[type.ordinal()];
        manager.getTexture(tex).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
    }
}
