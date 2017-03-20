package de.ellpeck.game.gui.component;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.GuiContainer;
import de.ellpeck.game.inventory.IInventory;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.render.item.IItemRenderer;
import de.ellpeck.game.util.MathUtil;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class ComponentSlot extends GuiComponent{

    private final GuiContainer container;
    private final IInventory inventory;
    public final int id;

    public ComponentSlot(GuiContainer container, IInventory inventory, int id, int x, int y){
        super(x, y, 18, 18);
        this.container = container;
        this.inventory = inventory;
        this.id = id;
    }

    @Override
    public boolean onMouseAction(Game game, int button){
        if(this.isMouseOver(game)){
            ItemInstance slotInst = this.inventory.get(this.id);

            if(button == Input.MOUSE_LEFT_BUTTON){
                if(this.container.holdingInst == null){
                    if(slotInst != null){
                        this.container.holdingInst = slotInst;
                        this.inventory.set(this.id, null);

                        return true;
                    }
                }
                else{
                    if(slotInst == null){
                        this.inventory.set(this.id, this.container.holdingInst);
                        this.container.holdingInst = null;

                        return true;
                    }
                    else{
                        if(slotInst.isItemEqual(this.container.holdingInst)){
                            int possible = Math.min(slotInst.getItem().getMaxAmount()-slotInst.getAmount(), this.container.holdingInst.getAmount());
                            if(possible > 0){
                                slotInst.add(possible);

                                this.container.holdingInst.remove(possible);
                                if(this.container.holdingInst.getAmount() <= 0){
                                    this.container.holdingInst = null;
                                }

                                return true;
                            }
                        }
                        else{
                            ItemInstance copy = this.container.holdingInst.copy();
                            this.container.holdingInst = slotInst;
                            this.inventory.set(this.id, copy);

                            return true;
                        }
                    }
                }
            }
            else if(button == Input.MOUSE_RIGHT_BUTTON){
                if(this.container.holdingInst == null){
                    if(slotInst != null){
                        int half = MathUtil.ceil((double)slotInst.getAmount()/2);
                        this.container.holdingInst = slotInst.copy().setAmount(half);

                        slotInst.remove(half);
                        if(slotInst.getAmount() <= 0){
                            this.inventory.set(this.id, null);
                        }

                        return true;
                    }
                }
                else{
                    if(slotInst == null){
                        this.inventory.set(this.id, this.container.holdingInst.copy().setAmount(1));

                        this.container.holdingInst.remove(1);
                        if(this.container.holdingInst.getAmount() <= 0){
                            this.container.holdingInst = null;
                        }

                        return true;
                    }
                    else if(slotInst.isItemEqual(this.container.holdingInst)){
                        if(slotInst.getAmount() < slotInst.getItem().getMaxAmount()){
                            slotInst.add(1);

                            this.container.holdingInst.remove(1);
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

    @Override
    public void render(Game game, AssetManager manager, Graphics g){
        IItemRenderer.renderSlotInGui(game, manager, g, this.inventory.get(this.id), this.x, this.y, 1F);
    }
}
