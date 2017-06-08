package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import org.newdawn.slick.Graphics;

public class ComponentFancyButton extends ComponentButton{

    protected final String texture;

    public ComponentFancyButton(Gui gui, int id, int x, int y, int sizeX, int sizeY, String texture, String... hover){
        super(gui, id, x, y, sizeX, sizeY, null, hover);
        this.texture = texture;
    }

    protected String getTexture(){
        return this.texture;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        manager.getImage(this.getTexture()).draw(this.x, this.y, this.sizeX, this.sizeY);
    }
}
