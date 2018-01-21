package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentScrollBar;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.util.ChangelogManager;
import de.ellpeck.rockbottom.util.ChangelogManager.Changelog;
import de.ellpeck.rockbottom.util.ChangelogManager.VersionInfo;

import java.util.List;

public class GuiChangelog extends Gui{

    private final Changelog changelog = ChangelogManager.getChangelog();
    private final boolean failed = ChangelogManager.isChangelogGrabError();

    private ComponentScrollBar scrollBar;

    public GuiChangelog(Gui parent){
        super(parent);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        IAssetManager assetManager = game.getAssetManager();

        this.components.add(new ComponentButton(this, this.width/2-40, this.height-20, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.back"))));

        int height = this.height/3*2-10;
        int max = (this.drawAndGetHeight(this.changelog, assetManager.getFont(), 20, 5, Integer.MAX_VALUE, false)-height)/10;
        this.scrollBar = new ComponentScrollBar(this, 12, 5, height, new BoundBox(0, 0, this.width-20*2, height).add(20, 5), max, null);
        this.components.add(this.scrollBar);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g){
        super.render(game, manager, g);

        IFont font = manager.getFont();
        if(this.changelog == null || this.failed){
            IResourceName res = RockBottomAPI.createInternalRes("info.changelog."+(this.failed ? "error" : "grabbing"));
            font.drawCenteredString(this.width/2, 50, manager.localize(res), 0.5F, false);
        }
        else{
            int y = 5-this.scrollBar.getNumber()*10;
            int x = 20;
            int maxY = this.height/3*2-10;
            this.drawAndGetHeight(this.changelog, font, x, y, maxY, true);

            font.drawString(x, maxY+10F, FormattingCode.ORANGE+manager.localize(RockBottomAPI.createInternalRes("info.changelog.stable"))+": "+this.changelog.stable+this.drawInfo(manager, this.changelog.isStableNewer), 0.4F);
            font.drawString(x, maxY+20F, FormattingCode.YELLOW+manager.localize(RockBottomAPI.createInternalRes("info.changelog.latest"))+": "+this.changelog.latest+this.drawInfo(manager, this.changelog.isLatestNewer), 0.4F);
            font.drawString(x, maxY+30F, FormattingCode.GREEN+manager.localize(RockBottomAPI.createInternalRes("info.changelog.current"))+": "+game.getVersion(), 0.4F);
        }
    }

    private int drawAndGetHeight(Changelog changelog, IFont font, int x, int y, int maxY, boolean doDraw){
        outer:
        for(VersionInfo info : changelog.versionInfo){
            if(doDraw){
                FormattingCode color = info.versionName.equals(changelog.stable) ? FormattingCode.ORANGE : (info.versionName.equals(changelog.latest) ? FormattingCode.YELLOW : FormattingCode.WHITE);
                font.drawString(x, y, FormattingCode.UNDERLINED.toString()+color+info.versionName, 0.4F);
            }

            y += 12;
            if(y >= maxY){
                break;
            }

            for(String s : info.info){
                List<String> subLines = font.splitTextToLength(this.width-x*2, 0.4F, true, s);
                for(int i = 0; i < subLines.size(); i++){
                    if(doDraw){
                        String line = subLines.get(i);
                        font.drawString(x, y, (i == 0 ? " - " : "   ")+line, 0.3F);
                    }

                    y += 8;
                    if(y >= maxY){
                        break outer;
                    }
                }
            }

            y += 3;
        }
        return y;
    }

    private String drawInfo(IAssetManager manager, boolean should){
        return should ? FormattingCode.RED+" "+manager.localize(RockBottomAPI.createInternalRes("info.changelog.update")) : "";
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("changelog");
    }
}
