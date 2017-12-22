package de.ellpeck.rockbottom.gui.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import org.lwjgl.input.Mouse;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class GuiChangelog extends Gui{

    public static Changelog changelog;
    public static boolean changelogGrabError;

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
    public void render(IGameInstance game, IAssetManager manager, IGraphics g){
        super.render(game, manager, g);

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
            int mouse = Mouse.getDWheel();
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

    public static void loadChangelog() throws Exception{
        RockBottomAPI.logger().info("Grabbing the changelog...");

        URL newestURL = new URL("https://raw.githubusercontent.com/RockBottomGame/Changelog/master/changelog.json");
        InputStreamReader reader = new InputStreamReader(newestURL.openStream());

        RockBottomAPI.logger().info("Parsing the changelog...");

        JsonObject main = Util.JSON_PARSER.parse(reader).getAsJsonObject();

        String latest = main.get("latest").getAsString();
        String stable = main.get("stable").getAsString();

        JsonObject changes = main.get("changes").getAsJsonObject();
        VersionInfo[] infos = new VersionInfo[changes.size()];

        int counter = 0;
        for(Map.Entry<String, JsonElement> change : changes.entrySet()){
            JsonArray changelog = change.getValue().getAsJsonArray();

            String[] changelogArray = new String[changelog.size()];
            for(int i = 0; i < changelog.size(); i++){
                changelogArray[i] = changelog.get(i).getAsString();
            }

            infos[counter] = new VersionInfo(change.getKey(), changelogArray);
            counter++;
        }

        double current = convertToNum(RockBottomAPI.getGame().getVersion());
        boolean isStableNewer = convertToNum(stable) > current;
        boolean isLatestNewer = convertToNum(latest) > current;

        changelog = new Changelog(latest, stable, isLatestNewer, isStableNewer, infos);

        RockBottomAPI.logger().info("Successfully grabbed and parsed the changelog.");
    }

    private static double convertToNum(String version){
        try{
            String[] split = version.split("\\.", 2);
            double num = Integer.parseInt(split[0]);
            double decimal = Integer.parseInt(split[1].replaceAll("\\.", ""));
            return num+(decimal/100D);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't parse version string "+version+" into a comparable number", e);
            return 0D;
        }
    }

    public static class Changelog{

        public final String latest;
        public final String stable;
        public final boolean isLatestNewer;
        public final boolean isStableNewer;
        public final VersionInfo[] versionInfo;

        public Changelog(String latest, String stable, boolean isLatestNewer, boolean isStableNewer, VersionInfo[] versionInfo){
            this.latest = latest;
            this.stable = stable;
            this.isLatestNewer = isLatestNewer;
            this.isStableNewer = isStableNewer;
            this.versionInfo = versionInfo;
        }
    }

    public static class VersionInfo{

        public final String versionName;
        public final String[] info;

        public VersionInfo(String versionName, String[] info){
            this.versionName = versionName;
            this.info = info;
        }
    }
}
