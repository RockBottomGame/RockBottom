package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.List;

public class GuiStartNote extends Gui{

    private static final IResourceName RES = RockBottomAPI.createInternalRes("gui.start_note");
    private static final IResourceName DAY_RES = RockBottomAPI.createInternalRes("lore.start_note.day");

    private final int variation;

    public GuiStartNote(int variation){
        super(36*3, 48*3);
        this.variation = variation;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g){
        super.render(game, manager, g);

        ITexture tex = manager.getTexture(RES);
        tex.draw(this.x, this.y, this.width, this.height);

        IFont font = manager.getFont();

        String day = manager.localize(DAY_RES);
        font.drawString(this.x+this.width/2-font.getWidth(day, 0.4F)/2, this.y+5, day, 0, day.length(), 0.4F, Colors.BLACK, Colors.NO_COLOR);

        String text = manager.localize(RockBottomAPI.createInternalRes("lore.start_note."+(this.variation+1)));
        List<String> split = font.splitTextToLength(this.width-14, 0.2F, true, text);

        int y = 0;
        for(String s : split){
            font.drawString(this.x+7, this.y+18+y, s, 0, s.length(), 0.2F, Colors.BLACK, Colors.NO_COLOR);
            y += 5;
        }
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("start_note");
    }

    @Override
    public boolean canCloseWithInvKey(){
        return true;
    }

    @Override
    public boolean doesPauseGame(){
        return false;
    }
}
