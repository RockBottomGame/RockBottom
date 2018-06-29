package de.ellpeck.rockbottom.content;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.content.pack.IContentPackLoader;
import de.ellpeck.rockbottom.api.data.settings.ContentPackSettings;
import de.ellpeck.rockbottom.api.util.Util;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ContentPackLoader implements IContentPackLoader {

    private final List<ContentPack> allPacks = new ArrayList<>();
    private final List<ContentPack> activePacks = new ArrayList<>();
    private final List<ContentPack> disabledPacks = new ArrayList<>();

    private final ContentPackSettings packSettings = new ContentPackSettings();

    public ContentPackLoader() {
        ContentPack defaultPack = new ContentPack(ContentPack.DEFAULT_PACK_ID, "Default", "~", new String[0], "The default content of the game and all installed mods");
        this.allPacks.add(defaultPack);
        this.activePacks.add(defaultPack);
    }

    @Override
    public void load(File dir) {
        this.packSettings.load();

        File infoFile = new File(dir, "HOW TO INSTALL CONTENT PACKS.txt");

        if (!dir.exists()) {
            dir.mkdirs();

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(infoFile));
                String l = System.lineSeparator();

                writer.write("----------------------------------------------------------" + l);
                writer.write("To install a content pack, place the zip into this folder." + l);
                writer.write("Note that a content pack has to be enabled in the content " + l);
                writer.write("packs settings page for it to have an effect on the game. " + l);
                writer.write("                                                          " + l);
                writer.write("If your content pack did not come in a zip, then please   " + l);
                writer.write("refer to the content pack documentation or contact the    " + l);
                writer.write("author of the pack as content packs should be distributed " + l);
                writer.write("and used in zip form only.                                " + l);
                writer.write("----------------------------------------------------------" + l);
                writer.write("~Also known as README.txt~");

                writer.close();
            } catch (Exception e) {
                RockBottomAPI.logger().log(Level.WARNING, "Couldn't create info file in content packs folder", e);
            }
            RockBottomAPI.logger().info("Content packs folder not found, creating at " + dir);
        } else {
            int amount = 0;

            RockBottomAPI.logger().info("Loading jar mods from mods folder " + dir);

            for (File file : dir.listFiles()) {
                if (!file.equals(infoFile)) {
                    String name = file.getName();
                    if (name != null && name.endsWith(".zip")) {
                        try {
                            ZipFile zip = new ZipFile(file);
                            Enumeration<? extends ZipEntry> entries = zip.entries();

                            Main.classLoader.addURL(file.toURI().toURL());

                            boolean foundPack = false;
                            while (entries.hasMoreElements()) {
                                ZipEntry entry = entries.nextElement();

                                if (this.findPack(zip, entry)) {
                                    amount++;

                                    foundPack = true;
                                    break;
                                }
                            }

                            zip.close();

                            if (!foundPack) {
                                RockBottomAPI.logger().warning("Zip file " + file + " doesn't contain a valid pack.json");
                            }
                        } catch (Exception e) {
                            RockBottomAPI.logger().log(Level.WARNING, "Loading content pack from file " + file + " failed", e);
                        }
                    } else {
                        RockBottomAPI.logger().warning("Found non-zip file " + file + " in content packs folder " + dir);
                    }
                }
            }

            RockBottomAPI.logger().info("Loaded a total of " + amount + " content packs");
        }

        RockBottomAPI.logger().info("Sorting content packs");

        Comparator comp = this.packSettings.getPriorityComparator();
        this.allPacks.sort(comp);
        this.activePacks.sort(comp);
        this.disabledPacks.sort(comp);

        RockBottomAPI.logger().info("----- Loaded Content Packs ------");
        for (ContentPack pack : this.allPacks) {
            String s = pack.getName() + " @ " + pack.getVersion() + " (" + pack.getId() + ')';
            if (this.packSettings.isDisabled(pack.getId())) {
                s += " [DISABLED]";
            }
            RockBottomAPI.logger().info(s);
        }
        RockBottomAPI.logger().info("---------------------------------");
    }

    private boolean findPack(ZipFile file, ZipEntry entry) throws Exception {
        String entryName = entry.getName();
        if (entryName != null && entryName.equals("pack.json")) {
            InputStream stream = file.getInputStream(entry);
            JsonElement main = Util.JSON_PARSER.parse(new InputStreamReader(stream, Charsets.UTF_8));
            stream.close();

            JsonObject mainObj = main.getAsJsonObject();
            String id = mainObj.get("id").getAsString();
            String name = mainObj.get("name").getAsString();
            String version = mainObj.get("version").getAsString();
            String desc = mainObj.get("description").getAsString();

            JsonArray authors = mainObj.get("authors").getAsJsonArray();
            String[] authorStrgs = new String[authors.size()];
            for (int i = 0; i < authors.size(); i++) {
                authorStrgs[i] = authors.get(i).getAsString();
            }

            if (id != null && !id.isEmpty() && id.toLowerCase(Locale.ROOT).equals(id) && id.replaceAll(" ", "").equals(id)) {
                if (this.getPack(id) == null) {
                    ContentPack pack = new ContentPack(id, name, version, authorStrgs, desc);

                    if (!pack.isDefault() && this.packSettings.isDisabled(id)) {
                        this.disabledPacks.add(pack);
                        RockBottomAPI.logger().info("Content pack " + name + " with id " + id + " and version " + version + " is loaded but disabled");
                    } else {
                        this.activePacks.add(pack);
                        RockBottomAPI.logger().info("Loaded content pack " + name + " with id " + id + " and version " + version);
                    }

                    this.allPacks.add(pack);

                    return true;
                } else {
                    RockBottomAPI.logger().warning("Cannot load content pack " + name + " with id " + id + " and version " + version + " because a pack with that id is already present");
                }
            } else {
                RockBottomAPI.logger().warning("Cannot load content pack " + name + " with id " + id + " and version " + version + " because the id is either missing, empty, not all lower case or contains spaces");
            }
        }
        return false;
    }

    @Override
    public List<ContentPack> getAllPacks() {
        return Collections.unmodifiableList(this.allPacks);
    }

    @Override
    public List<ContentPack> getActivePacks() {
        return Collections.unmodifiableList(this.activePacks);
    }

    @Override
    public List<ContentPack> getDisabledPacks() {
        return Collections.unmodifiableList(this.disabledPacks);
    }

    @Override
    public ContentPackSettings getPackSettings() {
        return this.packSettings;
    }

    @Override
    public ContentPack getPack(String id) {
        for (ContentPack pack : this.allPacks) {
            if (pack.getId().equals(id)) {
                return pack;
            }
        }
        return null;
    }
}
