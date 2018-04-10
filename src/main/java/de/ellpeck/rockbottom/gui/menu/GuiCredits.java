package de.ellpeck.rockbottom.gui.menu;

import com.google.common.base.Charsets;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.content.ContentManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class GuiCredits extends Gui{

    private final List<String> credits = new ArrayList<>();
    private float renderY;

    public GuiCredits(Gui parent){
        super(parent);

        String path = "assets/rockbottom/text/credits.txt";
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(ContentManager.getResourceAsStream(path), Charsets.UTF_8));

            while(true){
                String line = reader.readLine();
                if(line != null){
                    this.credits.add(line);
                }
                else{
                    break;
                }
            }

            reader.close();
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't read credits file from "+path, e);

            this.credits.clear();
            this.credits.add("Credits couldn't be loaded :(");
            this.credits.add("Check the log for info!");
        }
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        this.components.add(new ComponentButton(this, this.width-47, 2, 45, 10, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.back"))));

        this.renderY = this.height;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g){
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

        if(this.renderY > -(this.credits.size()*game.getAssetManager().getFont().getHeight(0.45F))+this.height-5){
            this.renderY -= 0.6F;
        }
    }

    @Override
    public ResourceName getName(){
        return ResourceName.intern("credits");
    }
}
