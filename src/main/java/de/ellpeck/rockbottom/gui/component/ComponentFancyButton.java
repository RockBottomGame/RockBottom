package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.Gui;
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
    public void render(RockBottom game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        manager.getImage(this.getTexture()).draw(this.x, this.y, this.sizeX, this.sizeY);
    }
}
