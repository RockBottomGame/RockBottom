package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.content.pack.IContentPackLoader;
import de.ellpeck.rockbottom.api.data.settings.ContentPackSettings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.*;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.ArrayList;
import java.util.List;

public class GuiContentPacks extends Gui{

    private ComponentMenu leftMenu;
    private ComponentMenu rightMenu;
    private boolean packsChanged;

    public GuiContentPacks(Gui parent){
        super(304, 150, parent);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);
        int halfWidth = this.width/2;

        this.leftMenu = new ComponentMenu(this, halfWidth/2-63, 0, this.height-42, 1, 4, new BoundBox());
        this.components.add(this.leftMenu);

        this.rightMenu = new ComponentMenu(this, halfWidth+halfWidth/2-63, 0, this.height-42, 1, 4, new BoundBox());
        this.components.add(this.rightMenu);

        this.organize();

        this.components.add(new ComponentFancyButton(this, this.width/2+83, this.height-16, 16, 16, () -> Util.createAndOpen(game.getDataManager().getContentPacksDir()), RockBottomAPI.createInternalRes("gui.mods_folder"), "Content Packs Folder"));

        this.components.add(new ComponentButton(this, this.width/2+1, this.height-16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.back"))));

        this.components.add(new ComponentButton(this, this.width/2-81, this.height-16, 80, 16, () -> {
            this.components.add(new ComponentConfirmationPopup(this, this.width/2-41, this.height-8, aBoolean -> {
                if(aBoolean){
                    if(this.packsChanged){
                        RockBottomAPI.getContentPackLoader().getPackSettings().save();
                    }
                    game.restart();
                }
            }));
            this.sortComponents();
            return true;
        }, "Restart Game"));
    }

    private void organize(){
        this.leftMenu.clear();
        this.rightMenu.clear();

        IContentPackLoader loader = RockBottomAPI.getContentPackLoader();
        ContentPackSettings settings = loader.getPackSettings();

        List<ContentPack> packs = loader.getAllPacks();
        List<ContentPack> enabledPacks = new ArrayList<>();
        List<ContentPack> disabledPacks = new ArrayList<>();

        for(ContentPack pack : packs){
            if(!pack.isDefault() && settings.isDisabled(pack.getId())){
                disabledPacks.add(pack);
            }
            else{
                enabledPacks.add(pack);
            }
        }
        enabledPacks.sort(settings.getPriorityComparator());

        for(ContentPack pack : disabledPacks){
            MenuComponent comp = new MenuComponent(120, 26);

            comp.add(0, 0, new ComponentText(this, 0, 0, 102, 8, 0.35F, false, pack.getName()));
            comp.add(0, 8, new ComponentText(this, 0, 0, 102, 16, 0.225F, false, FormattingCode.LIGHT_GRAY+pack.getDescription()));

            comp.add(104, 0, new ComponentButton(this, 0, 0, 16, 24, () -> {
                settings.setEnabledPriority(pack.getId(), 0);
                this.packsChanged = true;
                this.organize();
                return true;
            }, ">", "Click to enable this content pack"));
            this.leftMenu.add(comp);
        }

        for(int i = 0; i < enabledPacks.size(); i++){
            ContentPack pack = enabledPacks.get(i);
            int prio = settings.getPriority(pack.getId());

            MenuComponent comp = new MenuComponent(120, 26);
            if(!pack.isDefault()){
                comp.add(0, 0, new ComponentButton(this, 0, 0, 16, 24, () -> {
                    settings.setDisabled(pack.getId());
                    this.packsChanged = true;
                    this.organize();
                    return true;
                }, "<", "Click to disable this content pack"));

                comp.add(104, 0, new ComponentButton(this, 0, 0, 16, 11, () -> {
                    settings.setEnabledPriority(pack.getId(), prio+1);
                    this.packsChanged = true;
                    this.organize();
                    return true;
                }, "+", "Click to increase priority"));

                comp.add(104, 13, new ComponentButton(this, 0, 0, 16, 11, () -> {
                    settings.setEnabledPriority(pack.getId(), prio-1);
                    this.packsChanged = true;
                    this.organize();
                    return true;
                }, "-", "Click to decrease priority"));
            }

            comp.add(18, 0, new ComponentText(this, 0, 0, 84, 16, 0.35F, false, pack.getName()));
            comp.add(18, 16, new ComponentText(this, 0, 0, 84, 8, 0.3F, false, FormattingCode.LIGHT_GRAY+"Priority: "+prio));

            this.rightMenu.add(comp);
        }

        this.leftMenu.organize();
        this.rightMenu.organize();
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g){
        IFont font = manager.getFont();
        List<String> lines = font.splitTextToLength(this.width-40, 0.3F, true, FormattingCode.ORANGE+"Any changes you make require the game to be restarted to take effect.");
        for(int i = 0; i < lines.size(); i++){
            String s = lines.get(i);
            font.drawCenteredString(this.x+this.width/2, this.y+this.height-38+i*8, s, 0.3F, false);
        }

        super.render(game, manager, g);
    }

    @Override
    public void onClosed(IGameInstance game){
        super.onClosed(game);

        if(this.packsChanged){
            RockBottomAPI.getContentPackLoader().getPackSettings().save();
        }
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("content_packs");
    }
}
