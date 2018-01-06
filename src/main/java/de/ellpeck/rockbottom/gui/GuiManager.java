package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.ComponentRenderEvent;
import de.ellpeck.rockbottom.api.event.impl.ComponentRenderOverlayEvent;
import de.ellpeck.rockbottom.api.event.impl.GuiOpenEvent;
import de.ellpeck.rockbottom.api.event.impl.OverlayRenderEvent;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.ISpecialCursor;
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
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.component.ComponentHealth;
import de.ellpeck.rockbottom.gui.component.ComponentHotbarSlot;
import de.ellpeck.rockbottom.gui.menu.background.MainMenuBackground;
import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiManager implements IGuiManager{

    private static final IResourceName LOC_DEAD = RockBottomAPI.createInternalRes("info.dead");
    private static final IResourceName LOC_DEAD_INFO = RockBottomAPI.createInternalRes("info.dead.wait");
    private final List<GuiComponent> onScreenComponents = new ArrayList<>();
    private MainMenuBackground background;
    private Gui gui;
    private ISpecialCursor currentCursor;
    private final Random debugRandom = new Random();

    private boolean isFadeOut;
    private int fadeTimer;
    private int fadeTimerStart;
    private Runnable fadeCallback;

    @Override
    public void updateDimensions(){
        RockBottomAPI.logger().config("Re-initializing Gui Manager");

        IGameInstance game = RockBottomAPI.getGame();

        if(this.gui != null){
            this.gui.init(game);
            this.gui.sortComponents();
        }

        if(!this.onScreenComponents.isEmpty()){
            this.onScreenComponents.clear();
        }

        if(game.getWorld() != null){
            this.initOnScreenComponents(game, game.getPlayer());
            this.background = null;
        }
        else{
            if(this.background == null){
                this.background = new MainMenuBackground();
            }
        }

        RockBottomAPI.logger().config("Successfully re-initialized Gui Manager");
    }

    private void initOnScreenComponents(IGameInstance game, AbstractEntityPlayer player){
        double width = game.getRenderer().getWidthInGui();

        for(int i = 0; i < 8; i++){
            int x = (int)(width/2-59.25+i*15);
            this.onScreenComponents.add(new ComponentHotbarSlot(player, player.getInv(), i, x, 3));
        }

        int maxHealthParts = Util.floor(game.getPlayer().getMaxHealth()/20);
        this.onScreenComponents.add(new ComponentHealth(null, (int)game.getRenderer().getWidthInGui()-3-maxHealthParts*13, (int)game.getRenderer().getHeightInGui()-3-12, 13*maxHealthParts-1, 12));
    }

    public void update(RockBottom game){
        IAssetManager manager = game.getAssetManager();
        if(!game.getSettings().hardwareCursor && game.getInput().isMouseInWindow()){
            ISpecialCursor cursor = manager.pickCurrentCursor(game);
            if(cursor != this.currentCursor){
                manager.setCursor(game, cursor);
                this.currentCursor = cursor;
            }
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

        if(this.fadeTimer > 0){
            this.fadeTimer--;

            if(this.fadeTimer <= 0){
                this.fadeTimerStart = 0;

                if(this.fadeCallback != null){
                    this.fadeCallback.run();
                    this.fadeCallback = null;
                }
            }
        }
    }

    public void render(RockBottom game, IAssetManager manager, IRenderer g, EntityPlayer player){
        IFont font = manager.getFont();
        float width = g.getWidthInGui();
        float height = g.getHeightInGui();

        Gui gui = this.getGui();
        float mouseX = g.getMouseInGuiX();
        float mouseY = g.getMouseInGuiY();

        if(player != null && player.isDead()){
            font.drawCenteredString(width/2F, height/2F-10, FormattingCode.RED+manager.localize(LOC_DEAD), 2F, true);

            String s = manager.localize(LOC_DEAD_INFO);
            font.drawFadingString(width/2F-font.getWidth(s, 0.5F)/2, height/2F+10, s, 0.5F, (float)(Util.getTimeMillis()%1000L)/1000F, 0.5F, 0.5F);
        }
        else{
            for(int i = 0; i < this.onScreenComponents.size(); i++){
                GuiComponent component = this.onScreenComponents.get(i);
                if(RockBottomAPI.getEventHandler().fireEvent(new ComponentRenderEvent(null, i, component)) != EventResult.CANCELLED){
                    component.render(game, manager, g, component.getRenderX(), component.getRenderY());
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
                    g.addFilledRect(0F, 0F, width, height, Gui.GRADIENT_COLOR);
                }

                gui.render(game, manager, g);
                gui.renderOverlay(game, manager, g);

                if(g.isGuiDebug()){
                    g.addEmptyRect(gui.getX(), gui.getY(), gui.getWidth(), gui.getHeight(), Colors.RED);

                    int addY = 0;
                    List<GuiComponent> components = gui.getComponents();
                    for(int i = 0; i < components.size(); i++){
                        GuiComponent component = components.get(i);
                        if(component.isActive()){
                            int x = component.getRenderX();
                            int y = component.getRenderY();
                            int w = component.getWidth();
                            int h = component.getHeight();

                            this.debugRandom.setSeed(Util.scrambleSeed(i));
                            int color = Colors.random(this.debugRandom);
                            g.addEmptyRect(x, y, w, h, 0.5F, color);

                            if(mouseX >= x && mouseY >= y && mouseX < x+w && mouseY < y+h){
                                font.drawString(3, 3+addY, "name: "+component.getName()+"; render_xy: "+x+", "+y+"; xy: "+component.getX()+", "+component.getY()+"; wh: "+w+", "+h, 0.2F, color);
                                addY += 5;
                            }
                        }
                    }
                }
            }
            else{
                for(int i = 0; i < this.onScreenComponents.size(); i++){
                    GuiComponent component = this.onScreenComponents.get(i);
                    if(RockBottomAPI.getEventHandler().fireEvent(new ComponentRenderOverlayEvent(null, i, component)) != EventResult.CANCELLED){
                        component.renderOverlay(game, manager, g, component.getRenderX(), component.getRenderY());
                    }
                }
            }
        }

        RockBottomAPI.getEventHandler().fireEvent(new OverlayRenderEvent(game, manager, g, player, this, gui));

        float fadeOpacity = 0F;

        if(this.fadeTimer > 0){
            float percentage = (float)this.fadeTimer/(float)this.fadeTimerStart;
            fadeOpacity = this.isFadeOut ? 1F-percentage : percentage;
        }
        else if(this.isFadeOut){
            fadeOpacity = 1F;
        }

        if(fadeOpacity != 0F){
            g.addFilledRect(0, 0, width, height, Colors.multiplyA(Colors.BLACK, fadeOpacity));
        }

        if(game.getSettings().cursorInfos){
            if(player != null && !player.isDead() && gui == null && game.getInput().isMouseInWindow()){
                if(this.onScreenComponents.stream().noneMatch(comp -> comp.isMouseOver(game))){
                    double tileX = g.getMousedTileX();
                    double tileY = g.getMousedTileY();

                    int x = Util.floor(tileX);
                    int y = Util.floor(tileY);
                    ItemInstance holding = player.getInv().get(player.getSelectedSlot());

                    for(TileLayer layer : TileLayer.getLayersByInteractionPrio()){
                        if(layer.canEditLayer(game, player)){
                            if(holding != null){
                                Item item = holding.getItem();

                                IItemRenderer renderer = item.getRenderer();
                                if(renderer != null){
                                    boolean inRange = player.isInRange(tileX, tileY, item.getMaxInteractionDistance(player.world, x, y, layer, tileX, tileY, player));
                                    renderer.renderOnMouseCursor(game, manager, g, item, holding, mouseX+24F/g.getGuiScale(), mouseY, 36F/g.getGuiScale(), Colors.WHITE, inRange);
                                }
                            }

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
        }
    }

    @Override
    public void openGui(Gui gui){
        IGameInstance game = RockBottomAPI.getGame();

        GuiOpenEvent event = new GuiOpenEvent(gui);
        if(RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED){
            if(this.gui != null){
                this.gui.onClosed(game);
            }

            this.gui = event.gui;

            if(this.gui != null){
                this.gui.onOpened(game);
                this.gui.init(game);
                this.gui.sortComponents();
            }

            if(this.gui == null){
                RockBottomAPI.logger().config("Closed Gui");
            }
            else{
                RockBottomAPI.logger().config("Opened Gui "+this.gui.getName()+" with "+this.gui.getComponents().size()+" components");
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

    @Override
    public boolean fadeOut(int ticks, Runnable after){
        return this.fade(ticks, after, true);
    }

    @Override
    public boolean fadeIn(int ticks, Runnable after){
        return this.fade(ticks, after, false);
    }

    @Override
    public ISpecialCursor getCursor(){
        return this.currentCursor;
    }

    private boolean fade(int ticks, Runnable after, boolean out){
        if(this.fadeTimer <= 0){
            this.fadeTimer = ticks;
            this.fadeTimerStart = ticks;

            this.isFadeOut = out;
            this.fadeCallback = after;

            return true;
        }
        else{
            return false;
        }
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

    public boolean onKeyPressed(RockBottom game, int button){
        return (game.getPlayer() == null || !game.getPlayer().isDead()) && this.gui != null && this.gui.onKeyPressed(game, button);
    }

    public boolean onCharInput(RockBottom game, int codePoint, char[] characters){
        return (game.getPlayer() == null || !game.getPlayer().isDead()) && this.gui != null && this.gui.onCharInput(game, codePoint, characters);
    }
}