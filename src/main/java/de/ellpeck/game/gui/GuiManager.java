package de.ellpeck.game.gui;

import de.ellpeck.game.Constants;
import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.component.ComponentHotbarSlot;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class GuiManager{

    private final List<ComponentHotbarSlot> hotbarSlots = new ArrayList<>();
    private Gui gui;

    public GuiManager(EntityPlayer player){
        for(int i = 0; i < 8; i++){
            int x = (int)(Game.get().getWidthInGui()/2-59.25+i*15);
            this.hotbarSlots.add(new ComponentHotbarSlot(player.inv, i, x, 3));
        }
    }

    public void update(Game game){
        if(this.gui != null){
            this.gui.update(game);
        }
    }

    public void render(Game game, AssetManager manager, Graphics g, EntityPlayer player){
        g.scale(game.settings.guiScale, game.settings.guiScale);

        this.hotbarSlots.forEach(slot -> slot.render(game, manager, g));

        Gui gui = player.guiManager.getGui();
        if(gui != null){
            g.setColor(Gui.GRADIENT);
            g.fillRect(0F, 0F, (float)game.getWidthInGui(), (float)game.getHeightInGui());

            gui.render(game, manager, g);
            gui.renderOverlay(game, manager, g);
        }
        else{
            this.hotbarSlots.forEach(slot -> slot.renderOverlay(game, manager, g));
        }
    }

    public void openGui(Gui gui){
        Game game = Game.get();

        if(this.gui != null){
            this.gui.onClosed(game);
        }

        this.gui = gui;

        if(this.gui != null){
            this.gui.initGui(game);
        }
    }

    public void closeGui(){
        this.openGui(null);
    }

    public Gui getGui(){
        return this.gui;
    }

    public boolean onMouseAction(Game game, int button){
        if(this.gui != null){
            return this.gui.onMouseAction(game, button);
        }
        else{
            for(ComponentHotbarSlot slot : this.hotbarSlots){
                if(slot.onMouseAction(game, button)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onKeyboardAction(Game game, int button){
        return this.gui != null && this.gui.onKeyboardAction(game, button);
    }
}