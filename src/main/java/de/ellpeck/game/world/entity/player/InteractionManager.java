package de.ellpeck.game.world.entity.player;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.item.Item;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.item.ItemTile;
import de.ellpeck.game.item.ToolType;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.util.MathUtil;
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

    private final EntityPlayer player;

    public InteractionManager(EntityPlayer player){
        this.player = player;
    }

    public void update(Game game){
        Gui gui = game.guiManager.getGui();

        if(gui == null && !this.player.isDead()){
            Input input = game.getContainer().getInput();
            double mouseX = input.getMouseX();
            double mouseY = input.getMouseY();

            double worldAtScreenX = this.player.x-game.getWidthInWorld()/2;
            double worldAtScreenY = -this.player.y-game.getHeightInWorld()/2;
            this.mousedTileX = MathUtil.floor(worldAtScreenX+mouseX/(double)game.settings.renderScale);
            this.mousedTileY = -MathUtil.floor(worldAtScreenY+mouseY/(double)game.settings.renderScale);

            if(input.isKeyDown(game.settings.keyLeft.key)){
                this.player.motionX -= 0.2;
                this.player.facing = Direction.LEFT;
            }
            else if(input.isKeyDown(game.settings.keyRight.key)){
                this.player.motionX += 0.2;
                this.player.facing = Direction.RIGHT;
            }

            if(input.isKeyDown(game.settings.keyJump.key)){
                this.player.jump(0.28);
            }

            if(input.isKeyPressed(Input.KEY_K)){
                this.player.kill();
            }

            if(this.player.world.isPosLoaded(this.mousedTileX, this.mousedTileY)){
                TileLayer layer = input.isKeyDown(game.settings.keyBackground.key) ? TileLayer.BACKGROUND : TileLayer.MAIN;
                ItemInstance selected = this.player.inv.get(this.player.inv.selectedSlot);

                if(input.isMouseButtonDown(game.settings.buttonDestroy)){
                    if(this.breakTileX != this.mousedTileX || this.breakTileY != this.mousedTileY){
                        this.breakProgress = 0;
                    }

                    Tile tile = this.player.world.getTile(layer, this.mousedTileX, this.mousedTileY);
                    if(tile.canBreak(this.player.world, this.mousedTileX, this.mousedTileY, layer)){
                        float hardness = tile.getHardness(this.player.world, this.mousedTileX, this.mousedTileY, layer);
                        float progressAmount = 0.05F/hardness;
                        boolean isRightTool = false;

                        if(selected != null){
                            Map<ToolType, Integer> tools = selected.getItem().getToolTypes(selected);
                            if(!tools.isEmpty()){
                                for(Map.Entry<ToolType, Integer> entry : tools.entrySet()){
                                    int level = entry.getValue();

                                    if(tile.isToolEffective(this.player.world, this.mousedTileX, this.mousedTileY, layer, entry.getKey(), level)){
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

                            this.player.world.destroyTile(this.mousedTileX, this.mousedTileY, layer, this.player, isRightTool);
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
                        Tile tileThere = this.player.world.getTile(layer, this.mousedTileX, this.mousedTileY);
                        if(layer != TileLayer.MAIN || !tileThere.onInteractWith(this.player.world, this.mousedTileX, this.mousedTileY, this.player)){
                            if(selected != null){
                                Item item = selected.getItem();
                                if(item instanceof ItemTile){
                                    if(layer != TileLayer.MAIN || this.player.world.getEntities(new BoundBox(this.mousedTileX, this.mousedTileY, this.mousedTileX+1, this.mousedTileY+1), PLACEMENT_TEST).isEmpty()){
                                        Tile tile = ((ItemTile)item).getTile();
                                        if(tileThere.canReplace(this.player.world, this.mousedTileX, this.mousedTileY, layer, tile)){
                                            if(tile.canPlace(this.player.world, this.mousedTileX, this.mousedTileY, layer)){

                                                tile.doPlace(this.player.world, this.mousedTileX, this.mousedTileY, layer, selected, this.player);

                                                selected.remove(1);
                                                if(selected.getAmount() <= 0){
                                                    this.player.inv.set(this.player.inv.selectedSlot, null);
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
                this.player.inv.selectedSlot++;
                if(this.player.inv.selectedSlot >= 8){
                    this.player.inv.selectedSlot = 0;
                }
            }
            else if(scroll > 0){
                this.player.inv.selectedSlot--;
                if(this.player.inv.selectedSlot < 0){
                    this.player.inv.selectedSlot = 7;
                }
            }
        }
        else{
            this.breakProgress = 0;
        }
    }

    public void onMouseAction(Game game, int button){
        game.guiManager.onMouseAction(game, button, game.getMouseInGuiX(), game.getMouseInGuiY());
    }

    public void onKeyboardAction(Game game, int button){
        if(!game.guiManager.onKeyboardAction(game, button)){
            for(int i = 0; i < game.settings.keysItemSelection.length; i++){
                if(button == game.settings.keysItemSelection[i]){
                    this.player.inv.selectedSlot = i;
                    break;
                }
            }
        }
    }
}
