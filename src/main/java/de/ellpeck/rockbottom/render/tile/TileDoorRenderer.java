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
import de.ellpeck.rockbottom.world.tile.TileWoodDoor;

public class TileDoorRenderer extends DefaultTileRenderer<TileWoodDoor>{

    private final IResourceName[] texClosed;
    private final IResourceName[] texOpen;

    public TileDoorRenderer(IResourceName texture){
        super(texture.addSuffix(".item"));
        texture = texture.addPrefix("tiles.");

        this.texClosed = new IResourceName[2];
        this.texOpen = new IResourceName[2];
        for(int i = 0; i < 2; i++){
            this.texClosed[i] = texture.addSuffix(".closed."+i);
            this.texOpen[i] = texture.addSuffix(".open."+i);
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileWoodDoor tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light){
        int half = state.get(StaticTileProps.TOP_HALF) ? 0 : 1;

        IResourceName tex;
        if(state.get(StaticTileProps.OPEN)){
            tex = this.texOpen[half];
        }
        else{
            tex = this.texClosed[half];
        }

        if(state.get(StaticTileProps.FACING_RIGHT)){
            manager.getTexture(tex).draw(renderX, renderY, scale, scale, light);
        }
        else{
            manager.getTexture(tex).draw(renderX+scale, renderY, -scale, scale, light);
        }
    }
}
