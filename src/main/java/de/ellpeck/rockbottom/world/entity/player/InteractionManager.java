package de.ellpeck.rockbottom.world.entity.player;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.IInteractionManager;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.AddBreakProgressEvent;
import de.ellpeck.rockbottom.api.event.impl.BreakEvent;
import de.ellpeck.rockbottom.api.event.impl.InteractionEvent;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.net.packet.toserver.PacketBreakTile;
import de.ellpeck.rockbottom.net.packet.toserver.PacketHotbar;
import de.ellpeck.rockbottom.net.packet.toserver.PacketInteract;
import de.ellpeck.rockbottom.net.packet.toserver.PacketPlayerMovement;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Input;

import java.util.List;

public class InteractionManager implements IInteractionManager{

    public TileLayer breakingLayer;
    public int breakTileX;
    public int breakTileY;

    public float breakProgress;
    public int placeCooldown;

    public double mousedTileX;
    public double mousedTileY;

    public static boolean interact(AbstractEntityPlayer player, TileLayer layer, double mouseX, double mouseY){
        List<Entity> entities = player.world.getEntities(new BoundBox(mouseX, mouseY, mouseX, mouseY).expand(0.01F));

        int x = Util.floor(mouseX);
        int y = Util.floor(mouseY);

        InteractionEvent event = new InteractionEvent(player, entities, layer, x, y, mouseX, mouseY);
        if(RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED){
            layer = event.layer;
            x = event.x;
            y = event.y;

            for(Entity entity : entities){
                if(entity.onInteractWith(player, mouseX, mouseY)){
                    return true;
                }
            }

            Tile tile = player.world.getState(layer, x, y).getTile();
            if(tile.onInteractWith(player.world, x, y, layer, mouseX, mouseY, player)){
                return true;
            }

            ItemInstance selected = player.getInv().get(player.getSelectedSlot());
            if(selected != null){
                Item item = selected.getItem();
                return item.onInteractWith(player.world, x, y, layer, mouseX, mouseY, player, selected);
            }
        }

        return false;
    }

    public static void breakTile(Tile tile, AbstractEntityPlayer player, int x, int y, TileLayer layer, boolean effective){
        BreakEvent event = new BreakEvent(player, layer, x, y, effective);
        if(RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED){
            layer = event.layer;
            x = event.x;
            y = event.y;
            effective = event.effective;

            tile.doBreak(player.world, x, y, layer, player, effective, true);
        }
    }

    private static void moveAndSend(EntityPlayer player, int type){
        if(player.move(type)){
            if(RockBottomAPI.getNet().isClient()){
                RockBottomAPI.getNet().sendToServer(new PacketPlayerMovement(player.getUniqueId(), player.x, player.y, player.motionX, player.motionY, player.facing));
            }
        }
    }

    public void update(RockBottom game){
        if(game.getWorld() != null){
            EntityPlayer player = game.getPlayer();
            Gui gui = game.getGuiManager().getGui();
            Settings settings = game.getSettings();

            if(gui == null && !player.isDead()){
                Input input = game.getInput();
                double mouseX = input.getMouseX();
                double mouseY = input.getMouseY();

                double worldAtScreenX = player.x-game.getWidthInWorld()/2;
                double worldAtScreenY = -player.y-game.getHeightInWorld()/2;
                this.mousedTileX = worldAtScreenX+mouseX/(double)game.getWorldScale();
                this.mousedTileY = -(worldAtScreenY+mouseY/(double)game.getWorldScale())+1;

                int x = Util.floor(this.mousedTileX);
                int y = Util.floor(this.mousedTileY);

                if(input.isKeyDown(settings.keyLeft.key)){
                    moveAndSend(player, 0);
                }
                else if(input.isKeyDown(settings.keyRight.key)){
                    moveAndSend(player, 1);
                }

                if(input.isKeyDown(settings.keyUp.key)){
                    moveAndSend(player, 3);
                }
                else if(input.isKeyDown(settings.keyDown.key)){
                    moveAndSend(player, 4);
                }

                if(input.isKeyDown(settings.keyJump.key)){
                    moveAndSend(player, 2);
                }

                if(player.world.isPosLoaded(x, y)){
                    TileLayer layer = input.isKeyDown(settings.keyBackground.key) ? TileLayer.BACKGROUND : TileLayer.MAIN;

                    if(input.isMouseButtonDown(settings.buttonDestroy)){
                        if(this.breakTileX != x || this.breakTileY != y){
                            this.breakProgress = 0;
                        }

                        Tile tile = player.world.getState(layer, x, y).getTile();
                        if(tile.canBreak(player.world, x, y, layer)){
                            float hardness = tile.getHardness(player.world, x, y, layer);
                            float progressAmount = 0.05F/hardness;

                            ItemInstance selected = player.getInv().get(player.getSelectedSlot());
                            boolean effective = RockBottomAPI.getApiHandler().isToolEffective(player, selected, tile, layer, x, y);
                            if(selected != null){
                                progressAmount *= selected.getItem().getMiningSpeed(player.world, x, y, layer, tile, effective);
                            }

                            AddBreakProgressEvent event = new AddBreakProgressEvent(player, layer, x, y, this.breakProgress, progressAmount);
                            RockBottomAPI.getEventHandler().fireEvent(event);
                            this.breakProgress = event.totalProgress;
                            progressAmount = event.progressAdded;

                            this.breakProgress += progressAmount;

                            if(this.breakProgress >= 1){
                                this.breakProgress = 0;

                                if(RockBottomAPI.getNet().isClient()){
                                    RockBottomAPI.getNet().sendToServer(new PacketBreakTile(player.getUniqueId(), layer, x, y));
                                }
                                else{
                                    breakTile(tile, player, x, y, layer, effective);
                                }
                            }
                            else{
                                this.breakTileX = x;
                                this.breakTileY = y;
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
                        if(input.isMouseButtonDown(settings.buttonPlace)){
                            if(interact(player, layer, this.mousedTileX, this.mousedTileY)){
                                if(RockBottomAPI.getNet().isClient()){
                                    RockBottomAPI.getNet().sendToServer(new PacketInteract(player.getUniqueId(), layer, this.mousedTileX, this.mousedTileY));
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
                    player.setSelectedSlot(player.getSelectedSlot()+1);
                    if(player.getSelectedSlot() >= 8){
                        player.setSelectedSlot(0);
                    }
                    slotChange = true;
                }
                else if(scroll > 0){
                    player.setSelectedSlot(player.getSelectedSlot()-1);
                    if(player.getSelectedSlot() < 0){
                        player.setSelectedSlot(7);
                    }
                    slotChange = true;
                }

                if(slotChange){
                    if(RockBottomAPI.getNet().isClient()){
                        RockBottomAPI.getNet().sendToServer(new PacketHotbar(player.getUniqueId(), player.getSelectedSlot()));
                    }
                }
            }
            else{
                this.breakProgress = 0;
            }
        }
    }

    public void onMouseAction(RockBottom game, int button){
        game.getGuiManager().onMouseAction(game, button, game.getMouseInGuiX(), game.getMouseInGuiY());
    }

    public void onKeyboardAction(RockBottom game, int button, char character){
        if(!game.getGuiManager().onKeyboardAction(game, button, character)){
            if(game.getWorld() != null && game.getGuiManager().getGui() == null){
                for(int i = 0; i < game.getSettings().keysItemSelection.length; i++){
                    if(button == game.getSettings().keysItemSelection[i]){
                        game.getPlayer().setSelectedSlot(i);

                        if(RockBottomAPI.getNet().isClient()){
                            RockBottomAPI.getNet().sendToServer(new PacketHotbar(game.getPlayer().getUniqueId(), i));
                        }

                        break;
                    }
                }
            }
        }
    }

    @Override
    public TileLayer getBreakingLayer(){
        return this.breakingLayer;
    }

    @Override
    public int getBreakTileX(){
        return this.breakTileX;
    }

    @Override
    public int getBreakTileY(){
        return this.breakTileY;
    }

    @Override
    public float getBreakProgress(){
        return this.breakProgress;
    }

    @Override
    public int getPlaceCooldown(){
        return this.placeCooldown;
    }

    @Override
    public double getMousedTileX(){
        return this.mousedTileX;
    }

    @Override
    public double getMousedTileY(){
        return this.mousedTileY;
    }
}
