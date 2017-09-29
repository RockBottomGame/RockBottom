package de.ellpeck.rockbottom.gui.menu.background;

import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.anim.IAnimation;
import de.ellpeck.rockbottom.api.gui.IMainMenuTheme;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.menu.GuiMainMenu;
import de.ellpeck.rockbottom.init.RockBottom;

import java.util.List;

public class MainMenuBackground{

    private static final IResourceName RES_LOGO = RockBottomAPI.createInternalRes("logo");

    static{
        RockBottomAPI.MAIN_MENU_THEMES.add(new BlankTheme());
    }

    private final IMainMenuTheme theme;
    private final TileState[][] tiles = new TileState[IMainMenuTheme.TILE_AMOUNT][IMainMenuTheme.TILE_AMOUNT];

    private int currentY;
    private int layerCounter;

    private long hoverStartTime;
    private long hoverPauseTime;

    public MainMenuBackground(){
        List<IMainMenuTheme> themes = RockBottomAPI.MAIN_MENU_THEMES;
        this.theme = themes.get(Util.RANDOM.nextInt(themes.size()));
    }

    public void update(RockBottom game){
        if(this.currentY < IMainMenuTheme.TILE_AMOUNT){
            if(game.getTotalTicks()%2 == 0 && Util.RANDOM.nextFloat() >= 0.75F){
                int placeX;
                do{
                    placeX = Util.RANDOM.nextInt(IMainMenuTheme.TILE_AMOUNT);
                }
                while(this.tiles[placeX][this.currentY] != null);

                this.tiles[placeX][this.currentY] = this.theme.getState(placeX, this.currentY);
                this.layerCounter++;

                if(this.layerCounter >= IMainMenuTheme.TILE_AMOUNT){
                    this.currentY++;
                    this.layerCounter = 0;
                }
            }
        }
    }

    public void render(RockBottom game, IAssetManager manager, IGraphics g){
        int color = this.theme.getBackgroundColor();
        g.backgroundColor(color);

        float tileSize = Math.max(game.getWidthInGui(), game.getHeightInGui())/(float)IMainMenuTheme.TILE_AMOUNT;

        for(int x = 0; x < IMainMenuTheme.TILE_AMOUNT; x++){
            for(int y = 0; y < IMainMenuTheme.TILE_AMOUNT; y++){
                TileState state = this.tiles[x][y];
                if(state != null){
                    Tile tile = state.getTile();
                    ITileRenderer renderer = tile.getRenderer();
                    if(renderer != null){
                        renderer.renderInMainMenuBackground(game, manager, g, tile, state, x*tileSize, game.getHeightInGui()-(y+1)*tileSize, tileSize);
                    }
                }
            }
        }

        IAnimation logo = manager.getAnimation(RES_LOGO);

        float scale = 0.75F;
        float width = logo.getFrameWidth()*scale;
        float height = logo.getFrameHeight()*scale;
        float x = game.getWidthInGui()/2F-width/2F;

        float mouseX = game.getMouseInGuiX();
        float mouseY = game.getMouseInGuiY();

        if(game.getGuiManager().getGui() instanceof GuiMainMenu && mouseX >= x+72*scale && mouseY >= 28*scale && mouseX <= x+width-72*scale && mouseY <= height-32*scale){
            if(this.hoverStartTime <= 0){
                this.hoverStartTime = Util.getTimeMillis()-this.hoverPauseTime;
                this.hoverPauseTime = 0;
            }

            logo.drawRow(this.hoverStartTime, 0, x, 0, width, height, Colors.WHITE);
        }
        else{
            if(this.hoverPauseTime <= 0){
                this.hoverPauseTime = Util.getTimeMillis()-this.hoverStartTime;
                this.hoverStartTime = 0;
            }

            logo.drawFrame(0, logo.getFrameByTime(0, this.hoverPauseTime), x, 0, width, height, Colors.WHITE);
        }
    }
}
