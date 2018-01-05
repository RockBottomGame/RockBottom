package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.util.ChangelogManager;
import de.ellpeck.rockbottom.util.ChangelogManager.Changelog;
import de.ellpeck.rockbottom.util.ChangelogManager.VersionInfo;

import java.util.List;

public class GuiChangelog extends Gui{

    private float scrollAmount = 0F;

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
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g){
        super.render(game, manager, g);

        Changelog changelog = ChangelogManager.getChangelog();
        boolean changelogGrabError = ChangelogManager.isChangelogGrabError();

        IFont font = manager.getFont();
        if(changelog == null || changelogGrabError){
            IResourceName res = RockBottomAPI.createInternalRes("info.changelog."+(changelogGrabError ? "error" : "grabbing"));
            font.drawCenteredString(this.width/2, 50, manager.localize(res), 0.5F, false);
        }
        else{
            boolean showScrollForMore = false;
            float y = 5F+this.scrollAmount;
            int x = 20;
            int maxY = this.height/3*2-10;

            outer:
            for(VersionInfo info : changelog.versionInfo){
                FormattingCode color = info.versionName.equals(changelog.stable) ? FormattingCode.ORANGE : (info.versionName.equals(changelog.latest) ? FormattingCode.YELLOW : FormattingCode.WHITE);
                font.drawString(x, y, FormattingCode.UNDERLINED.toString()+color+info.versionName, 0.4F);

                y += 12F;
                if(y >= maxY){
                    showScrollForMore = true;
                    break;
                }

                for(String s : info.info){
                    List<String> subLines = font.splitTextToLength(this.width-x*2, 0.4F, true, s);
                    for(int i = 0; i < subLines.size(); i++){
                        String line = subLines.get(i);
                        font.drawString(x, y, (i == 0 ? " - " : "   ")+line, 0.3F);

                        y += 8F;
                        if(y >= maxY){
                            showScrollForMore = true;
                            break outer;
                        }
                    }
                }

                y += 3F;
            }

            if(showScrollForMore){
                font.drawStringFromRight(this.width-x, maxY, "("+manager.localize(RockBottomAPI.createInternalRes("info.changelog.scroll_for_more"))+")", 0.25F);
            }

            float scroll = 10F;
            int mouse = game.getInput().getMouseWheel();
            if(mouse > 0){
                if(this.scrollAmount < 0F){
                    this.scrollAmount += scroll;
                }
            }
            else if(mouse < 0){
                if(showScrollForMore){
                    this.scrollAmount -= scroll;
                }
            }

            font.drawString(x, maxY+10F, FormattingCode.ORANGE+manager.localize(RockBottomAPI.createInternalRes("info.changelog.stable"))+": "+changelog.stable+this.drawInfo(manager, changelog.isStableNewer), 0.4F);
            font.drawString(x, maxY+20F, FormattingCode.YELLOW+manager.localize(RockBottomAPI.createInternalRes("info.changelog.latest"))+": "+changelog.latest+this.drawInfo(manager, changelog.isLatestNewer), 0.4F);
            font.drawString(x, maxY+30F, FormattingCode.GREEN+manager.localize(RockBottomAPI.createInternalRes("info.changelog.current"))+": "+game.getVersion(), 0.4F);
        }
    }

    private String drawInfo(IAssetManager manager, boolean should){
        return should ? FormattingCode.RED+" "+manager.localize(RockBottomAPI.createInternalRes("info.changelog.update")) : "";
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("changelog");
    }
}
