package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.gui.component.ComponentToggleButton;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.menu.GuiMainMenu;

import java.util.ArrayList;
import java.util.List;

public class ComponentLogin extends GuiComponent{

    public ComponentLogin(Gui gui, int x, int y, int width){
        super(gui, x, y, width, 92);

        List<GuiComponent> components = new ArrayList<>();

        ComponentInputField usernameField = new ComponentInputField(gui, x+2, y+10, width-4, 12, true, true, false, 64, false);
        components.add(usernameField);
        components.add(new ComponentInputField(gui, x+2, y+32, width-4, 12, true, true, false, 2048, false).setCensored(true));

        components.add(new ComponentButton(gui, x+2, y+48, width-4, 12, () -> {
            GuiMainMenu.loggedInUsername = usernameField.getText();
            GuiMainMenu.loginComplete = true;

            gui.getComponents().removeAll(components);
            gui.init(RockBottomAPI.getGame());
            return true;
        }, "Login"));
        components.add(new ComponentCheckBox(gui, x+width-14, y+62, 12, 12, false));

        components.add(new ComponentButton(gui, x+2, y+78, width-4, 12, () -> {
            GuiMainMenu.loginComplete = true;

            gui.getComponents().removeAll(components);
            gui.init(RockBottomAPI.getGame());
            return true;
        }, "Cancel", "If you don't log in, you will not able to use the Multiplayer!"));

        gui.getComponents().addAll(components);
        components.add(this);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g, int x, int y){
        super.render(game, manager, g, x, y);

        g.fillRect(x, y, this.width, this.height, Colors.setA(Colors.BLACK, 0.65F));
        g.drawRect(x, y, this.width, this.height, Colors.BLACK);

        IFont font = manager.getFont();
        font.drawCenteredString(x+this.width/2, y+3, "Username", 0.25F, false);
        font.drawCenteredString(x+this.width/2, y+25, "Password", 0.25F, false);

        font.drawStringFromRight(x+this.width-16, y+65, "Remember?", 0.35F);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("login");
    }

    @Override
    public boolean shouldDoFingerCursor(IGameInstance game){
        return false;
    }
}
