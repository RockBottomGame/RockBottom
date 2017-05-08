package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.Gui;
import de.ellpeck.rockbottom.gui.GuiContainer;
import de.ellpeck.rockbottom.gui.container.ContainerSlot;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.toserver.PacketSlotModification;
import de.ellpeck.rockbottom.render.item.IItemRenderer;
import de.ellpeck.rockbottom.util.Util;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class ComponentSlot extends GuiComponent{

    public final ContainerSlot slot;
    public final int componentId;
    private final GuiContainer container;

    public ComponentSlot(GuiContainer container, ContainerSlot slot, int componentId, int x, int y){
        super(container, x, y, 18, 18);
        this.container = container;
        this.slot = slot;
        this.componentId = componentId;
    }

    @Override
    public boolean onMouseAction(RockBottom game, int button, float x, float y){
        if(this.isMouseOver(game)){
            ItemInstance slotInst = this.slot.get();
            ItemInstance slotCopy = slotInst == null ? null : slotInst.copy();

            if(button == game.settings.buttonGuiAction1){
                if(this.container.holdingInst == null){
                    if(slotCopy != null){
                        if(this.setToInv(null)){
                            this.container.holdingInst = slotCopy;

                            return true;
                        }
                    }
                }
                else{
                    if(slotCopy == null){
                        if(this.setToInv(this.container.holdingInst)){
                            this.container.holdingInst = null;

                            return true;
                        }
                    }
                    else{
                        if(slotCopy.isItemEqual(this.container.holdingInst)){
                            int possible = Math.min(slotCopy.getMaxAmount()-slotCopy.getAmount(), this.container.holdingInst.getAmount());
                            if(possible > 0){
                                if(this.setToInv(slotCopy.addAmount(possible))){
                                    this.container.holdingInst.removeAmount(possible);
                                    if(this.container.holdingInst.getAmount() <= 0){
                                        this.container.holdingInst = null;
                                    }

                                    return true;
                                }
                            }
                        }
                        else{
                            ItemInstance copy = this.container.holdingInst.copy();
                            if(this.setToInv(copy)){
                                this.container.holdingInst = slotCopy;

                                return true;
                            }
                        }
                    }
                }
            }
            else if(button == game.settings.buttonGuiAction2){
                if(this.container.holdingInst == null){
                    if(slotCopy != null){
                        int half = Util.ceil((double)slotCopy.getAmount()/2);
                        slotCopy.removeAmount(half);

                        if(this.setToInv(slotCopy.getAmount() <= 0 ? null : slotCopy)){
                            this.container.holdingInst = slotCopy.copy().setAmount(half);

                            return true;
                        }
                    }
                }
                else{
                    if(slotCopy == null){
                        if(this.setToInv(this.container.holdingInst.copy().setAmount(1))){
                            this.container.holdingInst.removeAmount(1);
                            if(this.container.holdingInst.getAmount() <= 0){
                                this.container.holdingInst = null;
                            }

                            return true;
                        }
                    }
                    else if(slotCopy.isItemEqual(this.container.holdingInst)){
                        if(slotCopy.getAmount() < slotCopy.getMaxAmount()){
                            if(this.setToInv(slotCopy.addAmount(1))){
                                this.container.holdingInst.removeAmount(1);
                                if(this.container.holdingInst.getAmount() <= 0){
                                    this.container.holdingInst = null;
                                }

                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean setToInv(ItemInstance inst){
        if(inst == null ? this.slot.canRemove() : this.slot.canPlace(inst)){
            this.slot.set(inst);

            if(NetHandler.isClient()){
                NetHandler.sendToServer(new PacketSlotModification(RockBottom.get().player.getUniqueId(), this.componentId, inst));
            }
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        IItemRenderer.renderSlotInGui(game, manager, g, this.slot.get(), this.x, this.y, 1F);
    }

    @Override
    public void renderOverlay(RockBottom game, AssetManager manager, Graphics g){
        if(this.container.holdingInst == null && this.isMouseOver(game)){
            ItemInstance instance = this.slot.get();
            if(instance != null){
                List<String> desc = new ArrayList<>();
                instance.getItem().describeItem(manager, instance, desc);
                Gui.drawHoverInfoAtMouse(game, manager, g, true, 0, desc);
            }
        }
    }
}
