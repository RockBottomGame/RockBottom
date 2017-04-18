package de.ellpeck.game.gui.menu;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.render.WorldRenderer;
import de.ellpeck.game.render.tile.DefaultTileRenderer;
import de.ellpeck.game.render.tile.ITileRenderer;
import de.ellpeck.game.util.Util;
import de.ellpeck.game.world.tile.Tile;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.util.Random;

public class MainMenuBackground{

    private static final int[] KONAMI_CODE = new int[]{Input.KEY_UP, Input.KEY_UP, Input.KEY_DOWN, Input.KEY_DOWN, Input.KEY_LEFT, Input.KEY_RIGHT, Input.KEY_LEFT, Input.KEY_RIGHT, Input.KEY_B, Input.KEY_A};
    private int konamiAt;

    private final Random rand = new Random();

    private static final int TILE_SIZE = 16;
    private Tile[][] menuTileGrid;

    private int tileAmountX;
    private int tileAmountY;

    private int stackedAmountX;
    private int stackAtY;

    private int timer;

    public void init(Game game){
        this.tileAmountX = Util.ceil(game.getWidthInGui()/TILE_SIZE);
        this.tileAmountY = Util.ceil(game.getHeightInGui()/TILE_SIZE);
        this.menuTileGrid = new Tile[this.tileAmountX][this.tileAmountY];
        this.stackedAmountX = 0;
        this.stackAtY = 0;
    }

    public void update(Game game){
        this.timer++;

        if(this.stackAtY < this.tileAmountY){
            if(this.timer%8 == 0 && this.rand.nextBoolean()){
                int placeY = this.tileAmountY-this.stackAtY-1;

                int placeX;
                do{
                    placeX = this.rand.nextInt(this.tileAmountX);
                }
                while(this.menuTileGrid[placeX][placeY] != null);

                Tile tile;
                if(this.konamiAt >= KONAMI_CODE.length){
                    tile = ContentRegistry.TILE_GRASS;
                }
                else{
                    tile = this.rand.nextFloat() >= 0.75F ? ContentRegistry.TILE_DIRT : ContentRegistry.TILE_ROCK;
                }
                this.menuTileGrid[placeX][placeY] = tile;

                this.stackedAmountX++;
                if(this.stackedAmountX >= this.tileAmountX){
                    this.stackedAmountX = 0;
                    this.stackAtY++;
                }
            }
        }
    }

    public void onKeyInput(int button){
        if(this.konamiAt < KONAMI_CODE.length){
            if(button == KONAMI_CODE[this.konamiAt]){
                this.konamiAt++;
            }
            else{
                this.konamiAt = 0;
            }
        }
    }

    public void render(Game game, AssetManager manager, Graphics g){
        g.setBackground(WorldRenderer.SKY_COLORS[WorldRenderer.SKY_COLORS.length-1]);

        float offsetY = (float)game.getHeightInGui()-this.tileAmountY*TILE_SIZE;
        for(int x = 0; x < this.tileAmountX; x++){
            for(int y = 0; y < this.tileAmountY; y++){
                Tile tile = this.menuTileGrid[x][y];
                if(tile != null){
                    ITileRenderer renderer = tile.getRenderer();
                    if(renderer instanceof DefaultTileRenderer){
                        String tex = ((DefaultTileRenderer)renderer).texture;
                        manager.getImage(tex).draw(x*TILE_SIZE, offsetY+y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        manager.getFont().drawCenteredString((float)game.getWidthInGui()/2F, 20, "&aGame", 4F, false);
    }
}
