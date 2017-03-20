package de.ellpeck.game.gui;

import de.ellpeck.game.Constants;
import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.render.item.IItemRenderer;
import de.ellpeck.game.world.entity.EntityItem;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class GuiContainer extends Gui{

    public ItemInstance holdingInst;

    public GuiContainer(EntityPlayer player, int sizeX, int sizeY){
        super(player, sizeX, sizeY);
    }

    @Override
    public void onClosed(Game game){
        super.onClosed(game);

        if(this.holdingInst != null){
            this.dropHeldItem();
        }
    }

    @Override
    public boolean onMouseAction(Game game, int button){
        if(super.onMouseAction(game, button)){
            return true;
        }

        if(this.holdingInst != null && button == Input.MOUSE_LEFT_BUTTON){
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
            Input input = game.getContainer().getInput();
            float mouseX = (float)input.getMouseX()/(float)Constants.GUI_SCALE;
            float mouseY = (float)input.getMouseY()/(float)Constants.GUI_SCALE;

            IItemRenderer.renderItemInGui(game, manager, g, this.holdingInst, mouseX-4F, mouseY-4F, 0.8F);
        }
    }

    private void dropHeldItem(){
        EntityItem.spawn(this.player.world, this.holdingInst, this.player.x, this.player.y+1, this.player.facing.offsetX*0.25, 0);
    }
}
