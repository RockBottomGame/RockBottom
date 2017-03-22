package de.ellpeck.game.world.entity.player;

import de.ellpeck.game.Constants;
import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.item.Item;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.item.ItemTile;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.util.MathUtil;
import de.ellpeck.game.world.Chunk.TileLayer;
import de.ellpeck.game.world.tile.Tile;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Input;

import java.util.Arrays;
import java.util.List;

public class InteractionManager{

    private static final List<Integer> ITEM_SELECTION_KEYS = Arrays.asList(Input.KEY_1, Input.KEY_2, Input.KEY_3, Input.KEY_4, Input.KEY_5, Input.KEY_6, Input.KEY_7, Input.KEY_8);

    public TileLayer breakingLayer;
    public int breakTileX;
    public int breakTileY;

    public int breakProgress;
    public int placeCooldown;

    public int selectedSlot;
    public int mousedTileX;
    public int mousedTileY;

    private final EntityPlayer player;

    public InteractionManager(EntityPlayer player){
        this.player = player;
    }

    public void update(Game game){
        Gui gui = this.player.guiManager.getGui();

        if(gui == null){
            Input input = game.getContainer().getInput();
            double mouseX = input.getMouseX();
            double mouseY = input.getMouseY();

            double worldAtScreenX = this.player.x-game.getWidthInWorld()/2;
            double worldAtScreenY = -this.player.y-game.getHeightInWorld()/2;
            this.mousedTileX = MathUtil.floor(worldAtScreenX+mouseX/(double)Constants.RENDER_SCALE);
            this.mousedTileY = -MathUtil.floor(worldAtScreenY+mouseY/(double)Constants.RENDER_SCALE);

            if(input.isKeyDown(Input.KEY_A)){
                this.player.motionX -= 0.2;
                this.player.facing = Direction.LEFT;
            }
            else if(input.isKeyDown(Input.KEY_D)){
                this.player.motionX += 0.2;
                this.player.facing = Direction.RIGHT;
            }

            if(input.isKeyDown(Input.KEY_SPACE) || input.isKeyDown(Input.KEY_W)){
                this.player.jump(0.28);
            }

            TileLayer layer = input.isKeyDown(Input.KEY_LSHIFT) ? TileLayer.BACKGROUND : TileLayer.MAIN;

            if(input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)){
                if(this.breakTileX != this.mousedTileX || this.breakTileY != this.mousedTileY){
                    this.breakProgress = 0;
                }

                Tile tile = this.player.world.getTile(layer, this.mousedTileX, this.mousedTileY);
                if(tile.canBreak(this.player.world, this.mousedTileX, this.mousedTileY, layer)){
                    if(this.breakProgress >= 8){
                        this.breakProgress = 0;

                        tile.onDestroyed(this.player.world, this.breakTileX, this.breakTileY, this.player, layer);

                        byte meta = this.player.world.getMeta(this.breakTileX, this.breakTileY);
                        game.particleManager.addTileParticles(this.player.world, this.breakTileX, this.breakTileY, tile, meta);

                        this.player.world.setTile(layer, this.breakTileX, this.breakTileY, ContentRegistry.TILE_AIR);
                    }
                    else{
                        this.breakProgress++;

                        this.breakTileX = this.mousedTileX;
                        this.breakTileY = this.mousedTileY;
                        this.breakingLayer = layer;
                    }
                }
                else{
                    this.breakProgress = 0;
                }
            }
            else{
                this.breakProgress = 0;
            }

            if(this.placeCooldown <= 0){
                if(input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)){
                    ItemInstance selected = this.player.playerInventory.get(this.selectedSlot);
                    if(selected != null){
                        Item item = selected.getItem();
                        if(item instanceof ItemTile){
                            if(layer != TileLayer.MAIN || this.player.world.getEntities(new BoundBox(this.mousedTileX, this.mousedTileY, this.mousedTileX+1, this.mousedTileY+1)).isEmpty()){
                                Tile tileThere = this.player.world.getTile(layer, this.mousedTileX, this.mousedTileY);
                                if(tileThere.canReplace(this.player.world, this.mousedTileX, this.mousedTileY, layer)){
                                    Tile tile = ((ItemTile)item).getTile();
                                    if(tile.canPlace(this.player.world, this.mousedTileX, this.mousedTileY, layer)){
                                        this.player.world.setTile(layer, this.mousedTileX, this.mousedTileY, tile);

                                        if(layer == TileLayer.MAIN){
                                            int meta = selected.getMeta();
                                            if(meta != 0){
                                                this.player.world.setMeta(this.mousedTileX, this.mousedTileY, meta);
                                            }
                                        }

                                        selected.remove(1);
                                        if(selected.getAmount() <= 0){
                                            this.player.playerInventory.set(this.selectedSlot, null);
                                        }

                                        this.placeCooldown = 5;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else{
                this.placeCooldown--;
            }

            int scroll = Mouse.getDWheel();
            if(scroll < 0){
                this.selectedSlot++;
                if(this.selectedSlot >= 8){
                    this.selectedSlot = 0;
                }
            }
            else if(scroll > 0){
                this.selectedSlot--;
                if(this.selectedSlot < 0){
                    this.selectedSlot = 7;
                }
            }
        }

    }

    public void onMouseAction(Game game, int button){
        this.player.guiManager.onMouseAction(game, button);
    }

    public void onKeyboardAction(Game game, int button){
        if(!this.player.guiManager.onKeyboardAction(game, button)){
            int index = ITEM_SELECTION_KEYS.indexOf(button);
            if(index >= 0){
                this.selectedSlot = index;
            }
        }
    }
}
