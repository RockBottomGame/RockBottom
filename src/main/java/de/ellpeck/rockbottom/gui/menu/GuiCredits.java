package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.AbstractGame;

import java.util.ArrayList;
import java.util.List;

public class GuiCredits extends Gui{

    private final List<String> credits = new ArrayList<>();
    private float renderY;

    public GuiCredits(Gui parent){
        super(parent);

        this.credits.add(AbstractGame.NAME+" - a Game by Ellpeck");
        this.credits.add("");
        this.credits.add("");
        this.credits.add("Programming");
        this.credits.add("  Ellpeck");
        this.credits.add("");
        this.credits.add("");
        this.credits.add("Art");
        this.credits.add("  wiiv");
        this.credits.add("");
        this.credits.add("");
        this.credits.add("Additional Programming");
        this.credits.add("  superaxander");
        this.credits.add("  canitzp");
        this.credits.add("  xdjackiexd");
        this.credits.add("");
        this.credits.add("");
        this.credits.add("Libraries and Additional Code");
        this.credits.add("  The Slick2D Java Game Library");
        this.credits.add("  The Lightweight Java Game Library");
        this.credits.add("");
        this.credits.add("  Stefan Gustavson (Simplex Noise Impl)");
        this.credits.add("");
        this.credits.add("");
        this.credits.add("Special Thanks");
        this.credits.add("  TTFTCUTS (A lot of Terrain Gen help)");
        this.credits.add("  witsend66 (Game Name)");
        this.credits.add("");
        this.credits.add("  Beta Modders");
        this.credits.add("    raphydaphy");
        this.credits.add("    Quarris");
        this.credits.add("    Kinomora");
        this.credits.add("    AKTheKnight");
        this.credits.add("");
        this.credits.add("");
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        this.components.add(new ComponentButton(this, this.width-47, 2, 45, 10, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.back"))));

        this.renderY = this.height;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g){
        float y = this.renderY;
        for(String s : this.credits){
            manager.getFont().drawString(20, y, s, 0.45F);
            y += manager.getFont().getHeight(0.45F);
        }

        super.render(game, manager, g);
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        this.renderY -= 0.65F;

        if(this.renderY <= -(this.credits.size()*game.getAssetManager().getFont().getHeight(0.45F))){
            this.renderY = this.height;
        }
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("credits");
    }
}
