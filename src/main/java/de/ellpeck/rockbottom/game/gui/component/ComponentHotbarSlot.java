package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.net.NetHandler;
import de.ellpeck.rockbottom.game.net.packet.toserver.PacketHotbar;
import de.ellpeck.rockbottom.game.inventory.InventoryPlayer;
import de.ellpeck.rockbottom.game.item.ItemInstance;
import de.ellpeck.rockbottom.game.util.Util;
import org.newdawn.slick.Graphics;

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
        Util.renderSlotInGui(game, manager, g, this.inv.get(this.id), this.x, this.y, 0.75F);

        if(this.inv.selectedSlot == this.id){
            manager.getImage("gui.selection_arrow").draw(this.x+0.75F, 1);
        }
    }

    @Override
    public void renderOverlay(RockBottom game, AssetManager manager, Graphics g){
        if(this.isMouseOver(game)){
            ItemInstance instance = this.inv.get(this.id);
            if(instance != null){
                Util.describeItem(game, manager, g, instance);
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
