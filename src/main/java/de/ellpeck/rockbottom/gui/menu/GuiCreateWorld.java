package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.newdawn.slick.Graphics;

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
        super(100, 100, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.nameField = new ComponentInputField(this, this.guiLeft+this.sizeX/2-75, this.guiTop+12, 150, 16, true, true, false, 40, true);
        this.components.add(this.nameField);

        this.seedField = new ComponentInputField(this, this.guiLeft+this.sizeX/2-75, this.guiTop+52, 150, 16, true, true, false, 40, true);
        this.components.add(this.seedField);

        int bottomY = (int)game.getHeightInGui();
        this.components.add(new ComponentButton(this, 0, this.guiLeft+this.sizeX/2-82, bottomY-30, 80, 16, "Create"));

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2+2, bottomY-30, 80, 16, game.getAssetManager().localize(AbstractGame.internalRes("button.back"))));

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

            this.seed = 0;
            for(char c : seed.toCharArray()){
                this.seed *= 31;
                this.seed += c;
            }
        }
    }

    private File makeWorldFile(IGameInstance game){
        return new File(game.getDataManager().getWorldsDir(), this.worldName);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        Font font = manager.getFont();
        int middle = this.guiLeft+this.sizeX/2;

        font.drawCenteredString(middle, this.guiTop, "World Name", 0.5F, false);
        font.drawString(middle-75, this.guiTop+30, "Final Name: "+this.worldName, 0.25F);

        font.drawCenteredString(middle, this.guiTop+40, "Seed", 0.5F, false);
        font.drawString(middle-75, this.guiTop+70, "Final Seed: "+this.seed, 0.25F);
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        if(button == -1){
            game.getGuiManager().openGui(this.parent);
            return true;
        }
        else if(button == 0){
            File file = this.makeWorldFile(game);
            WorldInfo info = new WorldInfo(file);
            info.seed = this.seed;
            info.save();
            game.startWorld(file, info);
            return true;
        }
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("create_world");
    }
}
