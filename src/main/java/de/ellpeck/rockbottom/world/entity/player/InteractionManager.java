package de.ellpeck.rockbottom.world.entity.player;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.gui.Gui;
import de.ellpeck.rockbottom.item.Item;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.item.ItemTile;
import de.ellpeck.rockbottom.item.ToolType;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.toserver.PacketBreakTile;
import de.ellpeck.rockbottom.net.packet.toserver.PacketHotbar;
import de.ellpeck.rockbottom.net.packet.toserver.PacketInteract;
import de.ellpeck.rockbottom.net.packet.toserver.PacketPlayerMovement;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.entity.EntityItem;
import de.ellpeck.rockbottom.world.tile.Tile;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Input;

import java.util.Map;

public class InteractionManager{

    public TileLayer breakingLayer;
    public int breakTileX;
    public int breakTileY;

    public float breakProgress;
    public int placeCooldown;

    public int mousedTileX;
    public int mousedTileY;

    public void update(RockBottom game){
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
                    moveAndSend(player, 0);
                }
                else if(input.isKeyDown(game.settings.keyRight.key)){
                    moveAndSend(player, 1);
                }

                if(input.isKeyDown(game.settings.keyJump.key)){
                    moveAndSend(player, 2);
                }

                if(player.world.isPosLoaded(this.mousedTileX, this.mousedTileY)){
                    TileLayer layer = input.isKeyDown(game.settings.keyBackground.key) ? TileLayer.BACKGROUND : TileLayer.MAIN;

                    if(input.isMouseButtonDown(game.settings.buttonDestroy)){
                        if(this.breakTileX != this.mousedTileX || this.breakTileY != this.mousedTileY){
                            this.breakProgress = 0;
                        }

                        Tile tile = player.world.getTile(layer, this.mousedTileX, this.mousedTileY);
                        if(tile.canBreak(player.world, this.mousedTileX, this.mousedTileY, layer)){
                            float hardness = tile.getHardness(player.world, this.mousedTileX, this.mousedTileY, layer);
                            float progressAmount = 0.05F/hardness;

                            int effectiveness = getToolEffectiveness(player, tile, layer, this.mousedTileX, this.mousedTileY);
                            if(effectiveness > 0){
                                progressAmount += effectiveness/200F;
                            }

                            this.breakProgress += progressAmount;

                            if(this.breakProgress >= 1){
                                this.breakProgress = 0;

                                if(NetHandler.isClient()){
                                    NetHandler.sendToServer(new PacketBreakTile(player.getUniqueId(), layer, this.mousedTileX, this.mousedTileY));
                                }
                                else{
                                    player.world.destroyTile(this.mousedTileX, this.mousedTileY, layer, player, effectiveness > 0);
                                }
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
                            boolean client = NetHandler.isClient();

                            if(interact(player, layer, this.mousedTileX, this.mousedTileY, client)){
                                if(client){
                                    NetHandler.sendToServer(new PacketInteract(player.getUniqueId(), layer, this.mousedTileX, this.mousedTileY));
                                }

                                this.placeCooldown = 5;
                            }
                        }
                    }
                    else{
                        this.placeCooldown--;
                    }
                }

                boolean slotChange = false;

                int scroll = Mouse.getDWheel();
                if(scroll < 0){
                    player.inv.selectedSlot++;
                    if(player.inv.selectedSlot >= 8){
                        player.inv.selectedSlot = 0;
                    }
                    slotChange = true;
                }
                else if(scroll > 0){
                    player.inv.selectedSlot--;
                    if(player.inv.selectedSlot < 0){
                        player.inv.selectedSlot = 7;
                    }
                    slotChange = true;
                }

                if(slotChange){
                    if(NetHandler.isClient()){
                        NetHandler.sendToServer(new PacketHotbar(player.getUniqueId(), player.inv.selectedSlot));
                    }
                }
            }
            else{
                this.breakProgress = 0;
            }
        }
    }

    public static boolean interact(EntityPlayer player, TileLayer layer, int x, int y, boolean simulate){
        Tile tileThere = player.world.getTile(layer, x, y);

        if(layer == TileLayer.MAIN){
            if(tileThere.onInteractWith(player.world, x, y, player)){
                return true;
            }
        }

        ItemInstance selected = player.inv.get(player.inv.selectedSlot);
        if(selected != null){
            Item item = selected.getItem();
            if(item instanceof ItemTile){
                if(layer != TileLayer.MAIN || player.world.getEntities(new BoundBox(x, y, x+1, y+1), entity -> !(entity instanceof EntityItem)).isEmpty()){
                    Tile tile = ((ItemTile)item).getTile();
                    if(tileThere.canReplace(player.world, x, y, layer, tile)){
                        if(tile.canPlace(player.world, x, y, layer)){

                            if(!simulate){
                                tile.doPlace(player.world, x, y, layer, selected, player);

                                selected.remove(1);
                                player.inv.notifyChange(player.inv.selectedSlot);
                                if(selected.getAmount() <= 0){
                                    player.inv.set(player.inv.selectedSlot, null);
                                }
                            }

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static void moveAndSend(EntityPlayer player, int type){
        player.move(type);

        if(NetHandler.isClient()){
            NetHandler.sendToServer(new PacketPlayerMovement(player.getUniqueId(), type));
        }
    }

    public static int getToolEffectiveness(EntityPlayer player, Tile tile, TileLayer layer, int x, int y){
        ItemInstance selected = player.inv.get(player.inv.selectedSlot);
        if(selected != null){
            Map<ToolType, Integer> tools = selected.getItem().getToolTypes(selected);
            if(!tools.isEmpty()){
                for(Map.Entry<ToolType, Integer> entry : tools.entrySet()){
                    int level = entry.getValue();

                    if(tile.isToolEffective(player.world, x, y, layer, entry.getKey(), level)){
                        return level;
                    }
                }
            }
        }
        return 0;
    }

    public void onMouseAction(RockBottom game, int button){
        game.guiManager.onMouseAction(game, button, game.getMouseInGuiX(), game.getMouseInGuiY());
    }

    public void onKeyboardAction(RockBottom game, int button, char character){
        if(!game.guiManager.onKeyboardAction(game, button, character)){
            if(game.isInWorld() && game.guiManager.getGui() == null){
                for(int i = 0; i < game.settings.keysItemSelection.length; i++){
                    if(button == game.settings.keysItemSelection[i]){
                        game.player.inv.selectedSlot = i;

                        if(NetHandler.isClient()){
                            NetHandler.sendToServer(new PacketHotbar(game.player.getUniqueId(), i));
                        }

                        break;
                    }
                }
            }
        }
    }
}
