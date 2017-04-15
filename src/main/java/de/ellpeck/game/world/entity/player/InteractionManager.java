package de.ellpeck.game.world.entity.player;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.item.Item;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.item.ItemTile;
import de.ellpeck.game.item.ToolType;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.util.Util;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.EntityItem;
import de.ellpeck.game.world.tile.Tile;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Input;

import java.util.Map;
import java.util.function.Predicate;

public class InteractionManager{

    private static final Predicate<Entity> PLACEMENT_TEST = entity -> !(entity instanceof EntityItem);

    public TileLayer breakingLayer;
    public int breakTileX;
    public int breakTileY;

    public float breakProgress;
    public int placeCooldown;

    public int mousedTileX;
    public int mousedTileY;

    public void update(Game game){
        if(game.isInWorld()){
            EntityPlayer player = game.player;
            Gui gui = game.guiManager.getGui();

            if(gui == null && !player.isDead()){
                Input input = game.getContainer().getInput();
                double mouseX = input.getMouseX();
                double mouseY = input.getMouseY();

                double worldAtScreenX = player.x-game.getWidthInWorld()/2;
                double worldAtScreenY = -player.y-game.getHeightInWorld()/2;
                this.mousedTileX = Util.floor(worldAtScreenX+mouseX/(double)game.settings.renderScale);
                this.mousedTileY = -Util.floor(worldAtScreenY+mouseY/(double)game.settings.renderScale);

                if(input.isKeyDown(game.settings.keyLeft.key)){
                    player.motionX -= 0.2;
                    player.facing = Direction.LEFT;
                }
                else if(input.isKeyDown(game.settings.keyRight.key)){
                    player.motionX += 0.2;
                    player.facing = Direction.RIGHT;
                }

                if(input.isKeyDown(game.settings.keyJump.key)){
                    player.jump(0.28);
                }

                if(input.isKeyPressed(Input.KEY_K)){
                    player.kill();
                }

                if(player.world.isPosLoaded(this.mousedTileX, this.mousedTileY)){
                    TileLayer layer = input.isKeyDown(game.settings.keyBackground.key) ? TileLayer.BACKGROUND : TileLayer.MAIN;
                    ItemInstance selected = player.inv.get(player.inv.selectedSlot);

                    if(input.isMouseButtonDown(game.settings.buttonDestroy)){
                        if(this.breakTileX != this.mousedTileX || this.breakTileY != this.mousedTileY){
                            this.breakProgress = 0;
                        }

                        Tile tile = player.world.getTile(layer, this.mousedTileX, this.mousedTileY);
                        if(tile.canBreak(player.world, this.mousedTileX, this.mousedTileY, layer)){
                            float hardness = tile.getHardness(player.world, this.mousedTileX, this.mousedTileY, layer);
                            float progressAmount = 0.05F/hardness;
                            boolean isRightTool = false;

                            if(selected != null){
                                Map<ToolType, Integer> tools = selected.getItem().getToolTypes(selected);
                                if(!tools.isEmpty()){
                                    for(Map.Entry<ToolType, Integer> entry : tools.entrySet()){
                                        int level = entry.getValue();

                                        if(tile.isToolEffective(player.world, this.mousedTileX, this.mousedTileY, layer, entry.getKey(), level)){
                                            progressAmount += level/200F;
                                            isRightTool = true;
                                            break;
                                        }
                                    }
                                }
                            }

                            this.breakProgress += progressAmount;

                            if(this.breakProgress >= 1){
                                this.breakProgress = 0;

                                player.world.destroyTile(this.mousedTileX, this.mousedTileY, layer, player, isRightTool);
                            }
                            else{
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
                        if(input.isMouseButtonDown(game.settings.buttonPlace)){
                            Tile tileThere = player.world.getTile(layer, this.mousedTileX, this.mousedTileY);
                            if(layer != TileLayer.MAIN || !tileThere.onInteractWith(player.world, this.mousedTileX, this.mousedTileY, player)){
                                if(selected != null){
                                    Item item = selected.getItem();
                                    if(item instanceof ItemTile){
                                        if(layer != TileLayer.MAIN || player.world.getEntities(new BoundBox(this.mousedTileX, this.mousedTileY, this.mousedTileX+1, this.mousedTileY+1), PLACEMENT_TEST).isEmpty()){
                                            Tile tile = ((ItemTile)item).getTile();
                                            if(tileThere.canReplace(player.world, this.mousedTileX, this.mousedTileY, layer, tile)){
                                                if(tile.canPlace(player.world, this.mousedTileX, this.mousedTileY, layer)){

                                                    tile.doPlace(player.world, this.mousedTileX, this.mousedTileY, layer, selected, player);

                                                    selected.remove(1);
                                                    if(selected.getAmount() <= 0){
                                                        player.inv.set(player.inv.selectedSlot, null);
                                                    }

                                                    this.placeCooldown = 5;
                                                }
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
                }

                int scroll = Mouse.getDWheel();
                if(scroll < 0){
                    player.inv.selectedSlot++;
                    if(player.inv.selectedSlot >= 8){
                        player.inv.selectedSlot = 0;
                    }
                }
                else if(scroll > 0){
                    player.inv.selectedSlot--;
                    if(player.inv.selectedSlot < 0){
                        player.inv.selectedSlot = 7;
                    }
                }
            }
            else{
                this.breakProgress = 0;
            }
        }
    }

    public void onMouseAction(Game game, int button){
        game.guiManager.onMouseAction(game, button, game.getMouseInGuiX(), game.getMouseInGuiY());
    }

    public void onKeyboardAction(Game game, int button, char character){
        if(!game.guiManager.onKeyboardAction(game, button, character)){
            if(game.isInWorld()){
                for(int i = 0; i < game.settings.keysItemSelection.length; i++){
                    if(button == game.settings.keysItemSelection[i]){
                        game.player.inv.selectedSlot = i;
                        break;
                    }
                }
            }
        }
    }
}
