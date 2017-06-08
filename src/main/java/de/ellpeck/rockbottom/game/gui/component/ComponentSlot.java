package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.net.NetHandler;
import de.ellpeck.rockbottom.game.gui.GuiContainer;
import de.ellpeck.rockbottom.game.gui.container.ContainerSlot;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.game.net.packet.toserver.PacketSlotModification;
import de.ellpeck.rockbottom.game.util.Util;
import org.newdawn.slick.Graphics;

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
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(this.isMouseOver(game)){
            ItemInstance slotInst = this.slot.get();
            ItemInstance slotCopy = slotInst == null ? null : slotInst.copy();

            if(button == game.getSettings().buttonGuiAction1){
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
            else if(button == game.getSettings().buttonGuiAction2){
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
                NetHandler.sendToServer(new PacketSlotModification(RockBottom.get().getPlayer().getUniqueId(), this.componentId, inst));
            }
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void render(IGameInstance game, AssetManager manager, Graphics g){
        Util.renderSlotInGui(game, manager, g, this.slot.get(), this.x, this.y, 1F);
    }

    @Override
    public void renderOverlay(IGameInstance game, AssetManager manager, Graphics g){
        if(this.container.holdingInst == null && this.isMouseOver(game)){
            ItemInstance instance = this.slot.get();
            if(instance != null){
                Util.describeItem(game, manager, g, instance);
            }
        }
    }
}
