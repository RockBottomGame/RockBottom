package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
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

        this.nameField = new ComponentInputField(this, this.width/2-75, 32, 150, 16, true, true, false, 40, true, string -> this.updateNameAndSeed(game));
        this.components.add(this.nameField);

        this.seedField = new ComponentInputField(this, this.width/2-75, 72, 150, 16, true, true, false, 40, true, string -> this.updateNameAndSeed(game));
        this.components.add(this.seedField);

        int bottomY = this.height;
        this.components.add(new ComponentButton(this, this.width/2-82, bottomY-30, 80, 16, () -> {
            this.updateNameAndSeed(game);

            File file = makeWorldFile(game, this.worldName);
            WorldInfo info = new WorldInfo(file);
            info.seed = this.seed;
            info.save();

            IGuiManager gui = game.getGuiManager();
            gui.fadeOut(20, () -> {
                game.startWorld(file, info, true);
                gui.fadeIn(20, null);
            });

            return true;
        }, "Create"));

        this.components.add(new ComponentButton(this, this.width/2+2, bottomY-30, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.back"))));

        this.updateNameAndSeed(game);
    }

    private void updateNameAndSeed(IGameInstance game){
        this.worldName = makeNameSafe(game, this.nameField.getText());

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

    public static String makeNameSafe(IGameInstance game, String name){
        if(name.trim().isEmpty()){
            name = "Unnamed";
        }

        for(String s : DISALLOWED_CHARACTERS){
            name = name.replaceAll(s, "-");
        }

        for(String s : DISALLOWED_FILENAMES){
            if(name.equals(s)){
                name = "-"+s+"-";
            }
        }

        int counter = 0;
        String actualName = name;

        File file = makeWorldFile(game, actualName);
        while(file.exists()){
            counter++;
            actualName = name+counter;

            file = makeWorldFile(game, actualName);
        }

        return actualName;
    }

    private static File makeWorldFile(IGameInstance game, String name){
        return new File(game.getDataManager().getWorldsDir(), name);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g){
        super.render(game, manager, g);

        IFont font = manager.getFont();
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
