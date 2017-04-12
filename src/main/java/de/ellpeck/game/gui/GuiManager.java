package de.ellpeck.game.gui;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.component.ComponentButton;
import de.ellpeck.game.gui.component.ComponentHotbarSlot;
import de.ellpeck.game.gui.component.GuiComponent;
import de.ellpeck.game.gui.menu.MainMenuBackground;
import de.ellpeck.game.util.Util;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GuiManager{

    private MainMenuBackground background;
    private final List<GuiComponent> onScreenComponents = new ArrayList<>();
    private Gui gui;
    public boolean shouldReInit;

    public void reInitSelf(Game game){
        Log.info("Re-initializing Gui Manager");

        if(game.isInWorld()){
            this.initInWorldComponents(game, game.player);
            this.background = null;
        }
        else{
            this.onScreenComponents.clear();

            this.background = new MainMenuBackground();
            this.background.init(game);
        }

        Log.info("Successfully re-initialized Gui Manager");
    }

    private void initInWorldComponents(Game game, EntityPlayer player){
        double width = game.getWidthInGui();

        this.onScreenComponents.clear();

        for(int i = 0; i < 8; i++){
            int x = (int)(width/2-59.25+i*15);
            this.onScreenComponents.add(new ComponentHotbarSlot(player.inv, i, x, 3));
        }

        this.onScreenComponents.add(new ComponentButton(null, 0, (int)width-33, 3, 30, 10, game.assetManager.localize("button.menu")){
            @Override
            public boolean onPressed(Game game){
                game.openIngameMenu();
                return true;
            }

            @Override
            public boolean isMouseOver(Game game){
                return GuiManager.this.getGui() == null && super.isMouseOver(game);
            }
        });

        this.onScreenComponents.add(new ComponentButton(null, 0, 3, 3, 30, 10, game.assetManager.localize("button.inventory")){
            @Override
            public boolean onPressed(Game game){
                GuiManager.this.openGui(new GuiInventory(player));
                return true;
            }

            @Override
            public boolean isMouseOver(Game game){
                return GuiManager.this.getGui() == null && super.isMouseOver(game);
            }
        });
    }

    public void update(Game game){
        if(this.shouldReInit){
            this.reInitSelf(game);

            if(this.gui != null){
                this.gui.initGui(game);
            }

            this.shouldReInit = false;
        }

        if(this.gui != null){
            this.gui.update(game);
        }

        if(this.background != null){
            this.background.update(game);
        }
    }

    public void render(Game game, AssetManager manager, Graphics g, EntityPlayer player){
        g.scale(game.settings.guiScale, game.settings.guiScale);

        if(player != null && player.isDead()){
            String deathInfo = manager.localize("info.dead");
            manager.getFont().drawCenteredString((float)game.getWidthInGui()/2F, (float)game.getHeightInGui()/2F, deathInfo, 2F, true);
        }
        else{
            this.onScreenComponents.forEach(comp -> comp.render(game, manager, g));

            if(game.isInWorld()){
                this.drawHealth(game, manager, g, player);
            }
            else{
                this.background.render(game, manager, g);
            }

            Gui gui = game.guiManager.getGui();
            if(gui != null){
                if(gui.hasGradient()){
                    g.setColor(Gui.GRADIENT);
                    g.fillRect(0F, 0F, (float)game.getWidthInGui(), (float)game.getHeightInGui());
                }

                gui.render(game, manager, g);
                gui.renderOverlay(game, manager, g);
            }
            else{
                this.onScreenComponents.forEach(comp -> comp.renderOverlay(game, manager, g));
            }
        }
    }

    private void drawHealth(Game game, AssetManager manager, Graphics g, EntityPlayer player){
        int healthParts = Util.floor(player.getHealth()/20);
        int maxHealthParts = Util.floor(player.getMaxHealth()/20);

        Image heart = manager.getImage("gui.heart");
        Image heartEmpty = manager.getImage("gui.heart_empty");

        int step = 13;
        int xStart = (int)game.getWidthInGui()-3-maxHealthParts*step;
        int yStart = (int)game.getHeightInGui()-3-12;

        int currX = 0;
        for(int i = 0; i < maxHealthParts; i++){
            Gui.drawScaledImage(g, healthParts > i ? heart : heartEmpty, xStart+currX, yStart, 0.75F, Color.white);
            currX += step;
        }

        if(game.guiManager.getGui() == null){
            float mouseX = game.getMouseInGuiX();
            float mouseY = game.getMouseInGuiY();

            if(mouseX >= xStart && mouseX < xStart+step*maxHealthParts-1 && mouseY >= yStart && mouseY < yStart+12){
                Gui.drawHoverInfoAtMouse(game, manager, g, false, 0, manager.localize("info.health")+":", player.getHealth()+"/"+player.getMaxHealth());
            }
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

        if(this.gui == null){
            Log.info("Closed Gui");
        }
        else{
            Log.info("Opened Gui "+this.gui);
        }
    }

    public void closeGui(){
        this.openGui(null);
    }

    public Gui getGui(){
        return this.gui;
    }

    public boolean onMouseAction(Game game, int button, float x, float y){
        if(this.gui != null){
            return this.gui.onMouseAction(game, button, x, y);
        }
        else{
            for(GuiComponent comp : this.onScreenComponents){
                if(comp.onMouseAction(game, button, x, y)){
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