package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class GuiCredits extends Gui{

    private final List<String> credits = new ArrayList<>();
    private int renderY;

    public GuiCredits(Gui parent){
        super(100, 100, parent);

        this.credits.add("A game by Ellpeck");
        this.credits.add("");
        this.credits.add("");
        this.credits.add("Programming");
        this.credits.add("  Ellpeck");
        this.credits.add("");
        this.credits.add("Additional Programming");
        this.credits.add("  canitzp");
        this.credits.add("  xdjackiexd");
        this.credits.add("");
        this.credits.add("Art");
        this.credits.add("  wiiv");
        this.credits.add("");
        this.credits.add("Special Thanks");
        this.credits.add("  witsend66 (Game name)");
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, -1, (int)game.getWidthInGui()-47, 2, 45, 10, game.getAssetManager().localize(RockBottom.internalRes("button.back"))));

        this.renderY = (int)game.getHeightInGui();
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        int y = this.renderY;
        for(String s : this.credits){
            manager.getFont().drawString(20, y, s, 0.75F);
            y += manager.getFont().getHeight(0.75F);
        }

        super.render(game, manager, g);
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        this.renderY--;

        if(this.renderY <= -(this.credits.size()*game.getAssetManager().getFont().getHeight(0.75F))){
            this.renderY = (int)game.getHeightInGui();
        }
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        if(button == -1){
            game.getGuiManager().openGui(this.parent);
            return true;
        }
        return false;
    }
}
