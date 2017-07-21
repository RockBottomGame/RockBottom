package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.IntProp;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.world.tile.TileTorch;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class TorchTileRenderer extends DefaultTileRenderer{

    private final IResourceName texLeft;
    private final IResourceName texRight;
    private final IResourceName texBack;

    public TorchTileRenderer(){
        super(AbstractGame.internalRes("torch"));
        this.texLeft = this.texture.addSuffix(".left");
        this.texRight = this.texture.addSuffix(".right");
        this.texBack = this.texture.addSuffix(".back");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, Tile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, Color[] light){
        int meta = world.getState(x, y).getProperty(TileTorch.PROP_FACING);

        IResourceName tex;
        if(meta == 1){
            tex = this.texLeft;
        }
        else if(meta == 2){
            tex = this.texRight;
        }
        else if(meta == 3){
            tex = this.texBack;
        }
        else{
            tex = this.texture;
        }

        manager.getTexture(tex).drawWithLight(renderX, renderY, scale, scale, light);
    }
}
