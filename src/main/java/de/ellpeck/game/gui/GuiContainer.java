package de.ellpeck.game.gui;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.component.ComponentSlot;
import de.ellpeck.game.inventory.IInventory;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.render.item.IItemRenderer;
import de.ellpeck.game.world.entity.EntityItem;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import org.newdawn.slick.Graphics;

public class GuiContainer extends Gui{

    protected final EntityPlayer player;
    public ItemInstance holdingInst;

    public GuiContainer(EntityPlayer player, int sizeX, int sizeY){
        super(sizeX, sizeY);
        this.player = player;
    }

    @Override
    public void onClosed(Game game){
        if(this.holdingInst != null){
            this.dropHeldItem();
        }
    }

    @Override
    public boolean onMouseAction(Game game, int button, float x, float y){
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
    public void render(Game game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        if(this.holdingInst != null){
            float mouseX = game.getMouseInGuiX();
            float mouseY = game.getMouseInGuiY();

            IItemRenderer.renderItemInGui(game, manager, g, this.holdingInst, mouseX-4F, mouseY-4F, 0.8F);
        }
    }

    private void dropHeldItem(){
        EntityItem.spawn(this.player.world, this.holdingInst, this.player.x, this.player.y+1, this.player.facing.x*0.25, 0);
    }

    protected void addSlotGrid(IInventory inventory, int start, int end, int xStart, int yStart, int width){
        int x = xStart;
        int y = yStart;
        for(int i = start; i < end; i++){
            this.components.add(new ComponentSlot(this, inventory, i, x, y));

            x += 20;
            if((i+1)%width == 0){
                y += 20;
                x = xStart;
            }
        }
    }

    protected void addPlayerInventory(int x, int y){
        this.addSlotGrid(this.player.inv, 0, 8, x, y, 8);
        this.addSlotGrid(this.player.inv, 8, this.player.inv.getSlotAmount(), x, y+25, 8);
    }
}
