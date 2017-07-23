package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.tex.Texture;
import de.ellpeck.rockbottom.api.render.tile.MultiTileRenderer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileDoor;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.HashMap;
import java.util.Map;

public class DoorTileRenderer extends MultiTileRenderer<TileDoor>{

    protected final Map<Pos2, IResourceName> texturesOpen = new HashMap<>();

    public DoorTileRenderer(IResourceName texture, MultiTile tile){
        super(texture, tile);

        for(int x = 0; x < tile.getWidth(); x++){
            for(int y = 0; y < tile.getHeight(); y++){
                if(tile.isStructurePart(x, y)){
                    this.texturesOpen.put(new Pos2(x, y), this.texture.addSuffix(".open."+x+"."+y));
                }
            }
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, TileDoor tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, Color[] light){
        Pos2 subPos = tile.getInnerCoord(state);
        boolean open = state.get(TileDoor.OPEN_PROP);
        boolean right = state.get(TileDoor.RIGHT_PROP);

        IResourceName tex;
        if(open){
            tex = this.texturesOpen.get(subPos);
        }
        else{
            tex = this.textures.get(subPos);
        }

        if(right){
            manager.getTexture(tex).drawWithLight(renderX, renderY, scale, scale, light);
        }
        else{
            Texture texture = manager.getTexture(tex);
            texture.drawWithLight(renderX+scale, renderY, renderX, renderY+scale, 0, 0, texture.getWidth(), texture.getHeight(), light, Color.white);
        }
    }
}