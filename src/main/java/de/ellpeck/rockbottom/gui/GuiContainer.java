package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.component.ComponentSlot;
import de.ellpeck.rockbottom.gui.container.ContainerSlot;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.toserver.PacketDropItem;
import de.ellpeck.rockbottom.render.item.IItemRenderer;
import de.ellpeck.rockbottom.world.entity.EntityItem;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Graphics;

import java.util.List;

public class GuiContainer extends Gui{

    protected final EntityPlayer player;

    public ItemInstance holdingInst;

    public GuiContainer(EntityPlayer player, int sizeX, int sizeY){
        super(sizeX, sizeY);
        this.player = player;
    }

    @Override
    public void onClosed(RockBottom game){
        if(this.holdingInst != null){
            this.dropHeldItem();
        }

        this.player.closeContainer();
    }

    @Override
    public boolean onMouseAction(RockBottom game, int button, float x, float y){
        if(super.onMouseAction(game, button, x, y)){
            return true;
        }

        if(this.holdingInst != null && button == game.settings.buttonGuiAction1){
            if(!this.isMouseOver(game)){
                this.dropHeldItem();
                this.holdingInst = null;

                return true;
            }
        }

        return false;
    }

    @Override
    public void initGui(RockBottom game){
        super.initGui(game);

        List<ContainerSlot> slots = this.player.getContainer().slots;
        for(int i = 0; i < slots.size(); i++){
            ContainerSlot slot = slots.get(i);
            this.components.add(new ComponentSlot(this, slot, i, this.guiLeft+slot.x, this.guiTop+slot.y));
        }
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        if(this.holdingInst != null){
            float mouseX = game.getMouseInGuiX();
            float mouseY = game.getMouseInGuiY();

            IItemRenderer.renderItemInGui(game, manager, g, this.holdingInst, mouseX-4F, mouseY-4F, 0.8F);
        }
    }

    private void dropHeldItem(){
        if(NetHandler.isClient()){
            NetHandler.sendToServer(new PacketDropItem(this.player.getUniqueId(), this.holdingInst));
        }
        else{
            EntityItem.spawn(this.player.world, this.holdingInst, this.player.x, this.player.y+1, this.player.facing.x*0.25, 0);
        }
    }

    @Override
    public boolean doesPauseGame(){
        return false;
    }
}
