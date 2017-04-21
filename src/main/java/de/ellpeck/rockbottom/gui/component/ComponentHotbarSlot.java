package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.Gui;
import de.ellpeck.rockbottom.inventory.InventoryPlayer;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.toserver.PacketHotbar;
import de.ellpeck.rockbottom.render.item.IItemRenderer;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class ComponentHotbarSlot extends GuiComponent{

    private final InventoryPlayer inv;
    private final int id;

    public ComponentHotbarSlot(InventoryPlayer inventory, int id, int x, int y){
        super(null, x, y, 14, 14);
        this.inv = inventory;
        this.id = id;
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        IItemRenderer.renderSlotInGui(game, manager, g, this.inv.get(this.id), this.x, this.y, 0.75F);

        if(this.inv.selectedSlot == this.id){
            manager.getImage("gui.selection_arrow").draw(this.x+0.75F, 1);
        }
    }

    @Override
    public void renderOverlay(RockBottom game, AssetManager manager, Graphics g){
        if(this.isMouseOver(game)){
            ItemInstance instance = this.inv.get(this.id);
            if(instance != null){
                List<String> desc = new ArrayList<>();
                instance.getItem().describeItem(manager, instance, desc);
                Gui.drawHoverInfoAtMouse(game, manager, g, true, 0, desc);
            }
        }
    }

    @Override
    public boolean onMouseAction(RockBottom game, int button, float x, float y){
        if(this.isMouseOver(game)){
            if(this.inv.selectedSlot != this.id){
                this.inv.selectedSlot = this.id;

                if(NetHandler.isClient()){
                    NetHandler.sendToServer(new PacketHotbar(game.player.getUniqueId(), this.id));
                }

                return true;
            }
        }
        return false;
    }
}
