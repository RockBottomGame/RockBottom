package de.ellpeck.rockbottom.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.Util;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;

public final class ChangelogManager{

    private static Changelog changelog;
    private static boolean changelogGrabError;

    public static void loadChangelog(){
        Thread loaderThread = new Thread(() -> {
            try{
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
            catch(Exception e){
                RockBottomAPI.logger().log(Level.WARNING, "There was an error trying to grab and parse the changelog", e);
                changelogGrabError = true;
            }
        }, "ChangelogGrabber");
        loaderThread.setDaemon(true);
        loaderThread.start();
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

    public static Changelog getChangelog(){
        return changelog;
    }

    public static boolean isChangelogGrabError(){
        return changelogGrabError;
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
