package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.IInteractionManager;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.ComponentRenderEvent;
import de.ellpeck.rockbottom.api.event.impl.ComponentRenderOverlayEvent;
import de.ellpeck.rockbottom.api.event.impl.GuiOpenEvent;
import de.ellpeck.rockbottom.api.event.impl.OverlayRenderEvent;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.gui.component.ComponentHealth;
import de.ellpeck.rockbottom.gui.component.ComponentHotbarSlot;
import de.ellpeck.rockbottom.gui.menu.background.MainMenuBackground;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GuiManager implements IGuiManager{

    private static final IResourceName LOC_DEAD = AbstractGame.internalRes("info.dead");
    private static final IResourceName LOC_DEAD_INFO = AbstractGame.internalRes("info.dead.wait");
    private final List<GuiComponent> onScreenComponents = new ArrayList<>();
    private boolean shouldReInit;
    private MainMenuBackground background;
    private Gui gui;

    @Override
    public void reInitSelf(IGameInstance game){
        Log.debug("Re-initializing Gui Manager");

        if(!this.onScreenComponents.isEmpty()){
            this.onScreenComponents.clear();
        }

        if(game.getWorld() != null){
            this.initInWorldComponents(game, game.getPlayer());
            this.background = null;
        }
        else{
            if(this.background == null){
                this.background = new MainMenuBackground();
            }
        }

        Log.debug("Successfully re-initialized Gui Manager");
    }

    @Override
    public void initInWorldComponents(IGameInstance game, AbstractEntityPlayer player){
        double width = game.getWidthInGui();

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
        float width = game.getWidthInGui();
        float height = game.getHeightInGui();

        Gui gui = this.getGui();

        if(player != null && player.isDead()){
            font.drawCenteredString(width/2F, height/2F-10, FormattingCode.RED+manager.localize(LOC_DEAD), 2F, true);

            String s = manager.localize(LOC_DEAD_INFO);
            font.drawFadingString(width/2F-font.getWidth(s, 0.5F)/2, height/2F+10, s, 0.5F, (float)(Util.getTimeMillis()%1000L)/1000F, 0.5F, 0.5F);
        }
        else{
            for(int i = 0; i < this.onScreenComponents.size(); i++){
                GuiComponent component = this.onScreenComponents.get(i);
                if(RockBottomAPI.getEventHandler().fireEvent(new ComponentRenderEvent(null, i, component)) != EventResult.CANCELLED){
                    component.render(game, manager, g);
                }
            }

            if(game.getWorld() == null){
                this.background.render(game, manager, g);
            }
        }

        if(gui == null || !(gui instanceof GuiChat)){
            game.getChatLog().drawNewMessages(game, manager, g);
        }

        if(player == null || !player.isDead()){
            if(gui != null){
                if(gui.hasGradient()){
                    g.setColor(Gui.GRADIENT);
                    g.fillRect(0F, 0F, width, height);
                }

                gui.render(game, manager, g);
                gui.renderOverlay(game, manager, g);
            }
            else{
                for(int i = 0; i < this.onScreenComponents.size(); i++){
                    GuiComponent component = this.onScreenComponents.get(i);
                    if(RockBottomAPI.getEventHandler().fireEvent(new ComponentRenderOverlayEvent(null, i, component)) != EventResult.CANCELLED){
                        component.renderOverlay(game, manager, g);
                    }
                }
            }
        }

        RockBottomAPI.getEventHandler().fireEvent(new OverlayRenderEvent(game, manager, g, player, this, gui));

        if(game.getSettings().cursorInfos){
            if(player != null && !player.isDead() && gui == null && Mouse.isInsideWindow()){
                if(this.onScreenComponents.stream().noneMatch(comp -> comp.isMouseOver(game))){
                    IInteractionManager interaction = game.getInteractionManager();
                    double tileX = interaction.getMousedTileX();
                    double tileY = interaction.getMousedTileY();

                    float mouseX = game.getMouseInGuiX();
                    float mouseY = game.getMouseInGuiY();

                    ItemInstance holding = player.getInv().get(player.getSelectedSlot());
                    if(holding != null){
                        Item item = holding.getItem();

                        IItemRenderer renderer = item.getRenderer();
                        if(renderer != null){
                            boolean inRange = player.isInRange(tileX, tileY);
                            renderer.renderOnMouseCursor(game, manager, g, item, holding, mouseX+24F/game.getGuiScale(), mouseY, 36F/game.getGuiScale(), Colors.WHITE, inRange);
                        }
                    }

                    TileLayer layer = Settings.KEY_BACKGROUND.isDown() ? TileLayer.BACKGROUND : TileLayer.MAIN;
                    int x = Util.floor(tileX);
                    int y = Util.floor(tileY);

                    if(player.world.isPosLoaded(x, y)){
                        TileState state = player.world.getState(layer, x, y);
                        Tile tile = state.getTile();
                        ITileRenderer renderer = tile.getRenderer();
                        if(renderer != null){
                            renderer.renderOnMouseOver(game, manager, g, player.world, tile, state, x, y, layer, mouseX, mouseY);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void openGui(Gui gui){
        IGameInstance game = AbstractGame.get();

        GuiOpenEvent event = new GuiOpenEvent(gui);
        if(RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED){
            if(this.gui != null){
                this.gui.onClosed(game);
            }

            this.gui = event.gui;

            if(this.gui != null){
                this.gui.onOpened(game);
                this.gui.initGui(game);
            }

            if(this.gui == null){
                Log.debug("Closed Gui");
            }
            else{
                Log.debug("Opened Gui "+this.gui.getName()+" with "+this.gui.getComponents().size()+" components");
            }
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

    public void onMouseAction(RockBottom game, int button, float x, float y){
        if(game.getPlayer() == null || !game.getPlayer().isDead()){
            if(this.gui != null){
                this.gui.onMouseAction(game, button, x, y);
            }
            else{
                for(GuiComponent comp : this.onScreenComponents){
                    if(comp.onMouseAction(game, button, x, y)){
                        return;
                    }
                }
            }
        }
    }

    public boolean onKeyboardAction(RockBottom game, int button, char character){
        return (game.getPlayer() == null || !game.getPlayer().isDead()) && this.gui != null && this.gui.onKeyboardAction(game, button, character);
    }
}