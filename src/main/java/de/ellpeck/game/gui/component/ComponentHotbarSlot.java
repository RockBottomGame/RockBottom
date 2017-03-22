package de.ellpeck.game.gui.component;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.inventory.InventoryPlayer;
import de.ellpeck.game.render.item.IItemRenderer;
import org.newdawn.slick.Graphics;

public class ComponentHotbarSlot extends GuiComponent{

    private final InventoryPlayer inv;
    private final int id;

    public ComponentHotbarSlot(InventoryPlayer inventory, int id, int x, int y){
        super(x, y, 14, 14);
        this.inv = inventory;
        this.id = id;
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g){
        IItemRenderer.renderSlotInGui(game, manager, g, this.inv.get(this.id), this.x, this.y, 0.75F);

        if(this.inv.selectedSlot == this.id){
            manager.getImage("gui.selection_arrow").draw(this.x+0.75F, 1);
        }
    }

    @Override
    public boolean onMouseAction(Game game, int button){
        if(this.isMouseOver(game)){
            if(this.inv.selectedSlot != this.id){
                this.inv.selectedSlot = this.id;
                return true;
            }
        }
        return false;
    }
}
