package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.impl.OverlayRenderEvent;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentHealth;
import de.ellpeck.rockbottom.gui.component.ComponentHotbarSlot;
import de.ellpeck.rockbottom.gui.menu.MainMenuBackground;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GuiManager implements IGuiManager{

    private static final IResourceName LOC_DEAD = RockBottom.internalRes("info.dead");
    private final List<GuiComponent> onScreenComponents = new ArrayList<>();
    private boolean shouldReInit;
    private MainMenuBackground background;
    private Gui gui;

    @Override
    public void reInitSelf(IGameInstance game){
        Log.debug("Re-initializing Gui Manager");

        if(game.isInWorld()){
            this.initInWorldComponents(game, game.getPlayer());
            this.background = null;
        }
        else{
            if(!this.onScreenComponents.isEmpty()){
                this.onScreenComponents.clear();
            }

            this.background = new MainMenuBackground();
            this.background.init(game);
        }

        Log.debug("Successfully re-initialized Gui Manager");
    }

    @Override
    public void initInWorldComponents(IGameInstance game, AbstractEntityPlayer player){
        double width = game.getWidthInGui();

        if(!this.onScreenComponents.isEmpty()){
            this.onScreenComponents.clear();
        }

        for(int i = 0; i < 8; i++){
            int x = (int)(width/2-59.25+i*15);
            this.onScreenComponents.add(new ComponentHotbarSlot(player, player.getInv(), i, x, 3));
        }

        int maxHealthParts = Util.floor(game.getPlayer().getMaxHealth()/20);
        this.onScreenComponents.add(new ComponentHealth(null, (int)game.getWidthInGui()-3-maxHealthParts*13, (int)game.getHeightInGui()-3-12, 13*maxHealthParts-1, 12));
    }

    @Override
    public void setReInit(){
        this.shouldReInit = true;
    }

    public void update(RockBottom game){
        if(this.shouldReInit){
            this.reInitSelf(game);

            if(this.gui != null){
                this.gui.initGui(game);
            }

            this.shouldReInit = false;
        }

        game.getChatLog().updateNewMessages();

        if(game.getPlayer() == null || !game.getPlayer().isDead()){
            if(this.gui != null){
                this.gui.update(game);
            }
        }

        if(this.background != null){
            this.background.update(game);
        }
    }

    public void render(RockBottom game, IAssetManager manager, Graphics g, EntityPlayer player){
        g.scale(game.getGuiScale(), game.getGuiScale());

        Font font = manager.getFont();
        float width = (float)game.getWidthInGui();
        float height = (float)game.getHeightInGui();

        Gui gui = this.getGui();

        if(player != null && player.isDead()){
            String deathInfo = manager.localize(LOC_DEAD);
            font.drawCenteredString(width/2F, height/2F, deathInfo, 2F, true);
        }
        else{
            this.onScreenComponents.forEach(comp -> comp.render(game, manager, g));

            if(!game.isInWorld()){
                this.background.render(game, manager, g);
            }

            if(gui == null || !(gui instanceof GuiChat)){
                game.getChatLog().drawNewMessages(game, manager, g);
            }

            if(gui != null){
                if(gui.hasGradient()){
                    g.setColor(Gui.GRADIENT);
                    g.fillRect(0F, 0F, width, height);
                }

                gui.render(game, manager, g);
                gui.renderOverlay(game, manager, g);
            }
            else{
                this.onScreenComponents.forEach(comp -> comp.renderOverlay(game, manager, g));
            }
        }

        RockBottomAPI.getEventHandler().fireEvent(new OverlayRenderEvent(game, manager, g, player, this, gui));

        font.drawString(2, height-font.getHeight(0.25F), game.getTitle(), 0.25F);

        if(game.getSettings().cursorInfos){
            if(player != null && !player.isDead() && gui == null && Mouse.isInsideWindow()){
                if(this.onScreenComponents.stream().noneMatch(comp -> comp.isMouseOver(game))){
                    ItemInstance holding = player.getInv().get(player.getSelectedSlot());
                    if(holding != null){
                        Item item = holding.getItem();

                        IItemRenderer renderer = item.getRenderer();
                        if(renderer != null){
                            float mouseX = game.getMouseInGuiX();
                            float mouseY = game.getMouseInGuiY();

                            renderer.renderOnMouseCursor(game, manager, g, item, holding, mouseX+24F/game.getGuiScale(), mouseY, 36F/game.getGuiScale(), Color.white);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void openGui(Gui gui){
        IGameInstance game = RockBottom.get();

        if(this.gui != null){
            this.gui.onClosed(game);
        }

        this.gui = gui;

        if(this.gui != null){
            this.gui.onOpened(game);
            this.gui.initGui(game);
        }

        if(this.gui == null){
            Log.debug("Closed Gui");
        }
        else{
            Log.debug("Opened Gui "+this.gui);
        }
    }

    @Override
    public void closeGui(){
        this.openGui(null);
    }

    @Override
    public Gui getGui(){
        return this.gui;
    }

    public boolean onMouseAction(RockBottom game, int button, float x, float y){
        if(game.getPlayer() == null || !game.getPlayer().isDead()){
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
        }
        return false;
    }

    public boolean onKeyboardAction(RockBottom game, int button, char character){
        if(game.getPlayer() == null || !game.getPlayer().isDead()){
            if(this.background != null){
                this.background.onKeyInput(button);
            }

            return this.gui != null && this.gui.onKeyboardAction(game, button, character);
        }
        return false;
    }
}