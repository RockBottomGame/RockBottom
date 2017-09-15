package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.WorldInfo;

import java.io.File;

public class GuiCreateWorld extends Gui{

    private static final String[] DISALLOWED_CHARACTERS = new String[]{"/", "<", ">", ":", "\\|", "\\?", "\"", "\\\\", "\\*", "~"};
    private static final String[] DISALLOWED_FILENAMES = new String[]{"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    private ComponentInputField nameField;
    private ComponentInputField seedField;

    private String worldName = "";

    private String lastSeed = "";
    private final long defaultSeed = Util.RANDOM.nextLong();
    private long seed;

    public GuiCreateWorld(Gui parent){
        super(parent);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        this.nameField = new ComponentInputField(this, this.width/2-75, 32, 150, 16, true, true, false, 40, true);
        this.components.add(this.nameField);

        this.seedField = new ComponentInputField(this, this.width/2-75, 72, 150, 16, true, true, false, 40, true);
        this.components.add(this.seedField);

        int bottomY = this.height;
        this.components.add(new ComponentButton(this, this.width/2-82, bottomY-30, 80, 16, () -> {
            File file = this.makeWorldFile(game);
            WorldInfo info = new WorldInfo(file);
            info.seed = this.seed;
            info.save();
            game.startWorld(file, info);
            return true;
        }, "Create"));

        this.components.add(new ComponentButton(this, this.width/2+2, bottomY-30, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.back"))));

        this.updateNameAndSeed(game);
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);
        this.updateNameAndSeed(game);
    }

    private void updateNameAndSeed(IGameInstance game){
        String name = this.nameField.getText();

        if(name.trim().isEmpty()){
            name = "Unnamed";
        }

        if(!this.worldName.equals(name)){
            this.worldName = name;

            for(String s : DISALLOWED_CHARACTERS){
                this.worldName = this.worldName.replaceAll(s, "-");
            }

            for(String s : DISALLOWED_FILENAMES){
                if(this.worldName.equals(s)){
                    this.worldName = "-"+s+"-";
                }
            }

            File file = this.makeWorldFile(game);
            while(file.exists()){
                this.worldName += "-";
                file = this.makeWorldFile(game);
            }
        }

        String seed = this.seedField.getText();

        if(seed.trim().isEmpty()){
            this.seed = this.defaultSeed;
            this.lastSeed = "";
        }
        else if(!this.lastSeed.equals(seed)){
            this.lastSeed = seed;

            try{
                this.seed = Long.parseLong(seed);
            }
            catch(NumberFormatException e){
                this.seed = seed.hashCode();
            }
        }
    }

    private File makeWorldFile(IGameInstance game){
        return new File(game.getDataManager().getWorldsDir(), this.worldName);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g){
        super.render(game, manager, g);

        Font font = manager.getFont();
        int middle = this.width/2;

        font.drawCenteredString(this.x+middle, this.y+20, "World Name", 0.5F, false);
        font.drawString(this.x+middle-75, this.y+50, "Final Name: "+this.worldName, 0.25F);

        font.drawCenteredString(this.x+middle, this.y+60, "Seed", 0.5F, false);
        font.drawString(this.x+middle-75, this.y+90, "Final Seed: "+this.seed, 0.25F);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("create_world");
    }
}
