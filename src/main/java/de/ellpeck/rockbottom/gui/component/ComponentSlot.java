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

    private final GuiContainer container;
    public final ContainerSlot slot;
    public final int componentId;

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

            if(button == game.settings.buttonGuiAction1){
                if(this.container.holdingInst == null){
                    if(slotInst != null){
                        this.container.holdingInst = slotInst;
                        this.setToInv(null);

                        return true;
                    }
                }
                else{
                    if(slotInst == null){
                        this.setToInv(this.container.holdingInst);
                        this.container.holdingInst = null;

                        return true;
                    }
                    else{
                        if(slotInst.isItemEqual(this.container.holdingInst)){
                            int possible = Math.min(slotInst.getItem().getMaxAmount()-slotInst.getAmount(), this.container.holdingInst.getAmount());
                            if(possible > 0){
                                slotInst.addAmount(possible);
                                this.setToInv(slotInst);

                                this.container.holdingInst.removeAmount(possible);
                                if(this.container.holdingInst.getAmount() <= 0){
                                    this.container.holdingInst = null;
                                }

                                return true;
                            }
                        }
                        else{
                            ItemInstance copy = this.container.holdingInst.copy();
                            this.container.holdingInst = slotInst;
                            this.setToInv(copy);

                            return true;
                        }
                    }
                }
            }
            else if(button == game.settings.buttonGuiAction2){
                if(this.container.holdingInst == null){
                    if(slotInst != null){
                        int half = Util.ceil((double)slotInst.getAmount()/2);
                        this.container.holdingInst = slotInst.copy().setAmount(half);

                        slotInst.removeAmount(half);
                        this.setToInv(slotInst.getAmount() <= 0 ? null : slotInst);

                        return true;
                    }
                }
                else{
                    if(slotInst == null){
                        this.setToInv(this.container.holdingInst.copy().setAmount(1));

                        this.container.holdingInst.removeAmount(1);
                        if(this.container.holdingInst.getAmount() <= 0){
                            this.container.holdingInst = null;
                        }

                        return true;
                    }
                    else if(slotInst.isItemEqual(this.container.holdingInst)){
                        if(slotInst.getAmount() < slotInst.getItem().getMaxAmount()){
                            slotInst.addAmount(1);
                            this.setToInv(slotInst);

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
        return false;
    }

    private void setToInv(ItemInstance inst){
        this.slot.set(inst);

        if(NetHandler.isClient()){
            NetHandler.sendToServer(new PacketSlotModification(RockBottom.get().player.getUniqueId(), this.componentId, inst));
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
