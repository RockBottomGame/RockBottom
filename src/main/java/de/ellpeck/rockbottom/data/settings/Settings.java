package de.ellpeck.rockbottom.data.settings;

import de.ellpeck.rockbottom.data.DataManager;
import de.ellpeck.rockbottom.util.Util;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Settings implements IPropSettings{

    public static final float DEFAULT_GUI_R = 0.32156864F;
    public static final float DEFAULT_GUI_G = 0.5882353F;
    public static final float DEFAULT_GUI_B = 0.32156864F;
    private static final String[] DEFAULT_NAMES = new String[]{"Bob", "Doley", "Jason", "Huffelpuff", "Megan", "Jennifer", "Bottle", "Bus Stop", "ThePlayer99", "Genelele", "Karina", "Heinz", "Ketchup", "Dan", "David", "Penguin", "Hubert", "Penny", "Vinny", "Xx_TheBestLP_xX", "Bozo", "Patrick", "InigoMontoya", "Pebbles", "Noodles", "Milkshake"};
    public List<Keybind> keybinds = new ArrayList<>();
    public Keybind keyInventory = new Keybind("inventory", Input.KEY_E);
    public Keybind keyMenu = new Keybind("menu", Input.KEY_ESCAPE);
    public Keybind keyLeft = new Keybind("left", Input.KEY_A);
    public Keybind keyRight = new Keybind("right", Input.KEY_D);
    public Keybind keyJump = new Keybind("jump", Input.KEY_SPACE);
    public Keybind keyBackground = new Keybind("background", Input.KEY_LSHIFT);
    public Keybind keyChat = new Keybind("chat", Input.KEY_ENTER);
    public Keybind keyAdvancedInfo = new Keybind("advanced_info", Input.KEY_LSHIFT);
    public String chatName;

    public int targetFps;
    public int autosaveIntervalSeconds;

    public int guiScale;
    public int renderScale;
    public Color guiColor;

    public boolean hardwareCursor;
    public boolean cursorInfos;

    public int buttonDestroy;
    public int buttonPlace;
    public int buttonGuiAction1;
    public int buttonGuiAction2;

    public int serverStartPort;

    public int[] keysItemSelection = new int[8];

    public static String getRandomChatName(){
        return DEFAULT_NAMES[Util.RANDOM.nextInt(DEFAULT_NAMES.length)];
    }

    @Override
    public void load(Properties props){
        for(Keybind bind : this.keybinds){
            bind.key = this.getProp(props, "key_"+bind.name, bind.def);
        }

        this.chatName = Util.trimString(this.getProp(props, "chat_name", getRandomChatName()), 24).trim();

        this.targetFps = this.getProp(props, "target_fps", 60);
        this.autosaveIntervalSeconds = this.getProp(props, "autosave_interval", 60);

        this.guiScale = this.getProp(props, "gui_scale", 4);
        this.renderScale = this.getProp(props, "render_scale", 48);

        this.guiColor = new Color(this.getProp(props, "gui_r", DEFAULT_GUI_R), this.getProp(props, "gui_g", DEFAULT_GUI_G), this.getProp(props, "gui_b", DEFAULT_GUI_B));

        this.hardwareCursor = this.getProp(props, "hardware_cursor", false);
        this.cursorInfos = this.getProp(props, "cursor_infos", true);

        this.buttonDestroy = this.getProp(props, "button_destroy", Input.MOUSE_LEFT_BUTTON);
        this.buttonPlace = this.getProp(props, "button_place", Input.MOUSE_RIGHT_BUTTON);
        this.buttonGuiAction1 = this.getProp(props, "button_gui_1", Input.MOUSE_LEFT_BUTTON);
        this.buttonGuiAction2 = this.getProp(props, "button_gui_2", Input.MOUSE_RIGHT_BUTTON);

        this.serverStartPort = this.getProp(props, "server_port", 8000);

        int[] defaultKeys = new int[]{Input.KEY_1, Input.KEY_2, Input.KEY_3, Input.KEY_4, Input.KEY_5, Input.KEY_6, Input.KEY_7, Input.KEY_8};
        for(int i = 0; i < this.keysItemSelection.length; i++){
            this.keysItemSelection[i] = this.getProp(props, "key_item_select_"+i, defaultKeys[i]);
        }
    }

    @Override
    public void save(Properties props){
        for(Keybind bind : this.keybinds){
            this.setProp(props, "key_"+bind.name, bind.key);
        }

        this.setProp(props, "chat_name", this.chatName);

        this.setProp(props, "target_fps", this.targetFps);
        this.setProp(props, "autosave_interval", this.autosaveIntervalSeconds);

        this.setProp(props, "gui_scale", this.guiScale);
        this.setProp(props, "render_scale", this.renderScale);

        this.setProp(props, "gui_r", this.guiColor.r);
        this.setProp(props, "gui_g", this.guiColor.g);
        this.setProp(props, "gui_b", this.guiColor.b);

        this.setProp(props, "hardware_cursor", this.hardwareCursor);
        this.setProp(props, "cursor_infos", this.cursorInfos);

        this.setProp(props, "button_destroy", this.buttonDestroy);
        this.setProp(props, "button_place", this.buttonPlace);
        this.setProp(props, "button_gui_1", this.buttonGuiAction1);
        this.setProp(props, "button_gui_2", this.buttonGuiAction2);

        this.setProp(props, "server_port", this.serverStartPort);

        for(int i = 0; i < this.keysItemSelection.length; i++){
            this.setProp(props, "key_item_select_"+i, this.keysItemSelection[i]);
        }
    }

    @Override
    public File getFile(DataManager manager){
        return manager.settingsFile;
    }

    @Override
    public String getName(){
        return "Game settings";
    }

    private <T> void setProp(Properties props, String name, T val){
        props.setProperty(name, String.valueOf(val));
    }

    private int getProp(Properties props, String name, int def){
        return Integer.parseInt(props.getProperty(name, String.valueOf(def)));
    }

    private boolean getProp(Properties props, String name, boolean def){
        return Boolean.parseBoolean(props.getProperty(name, String.valueOf(def)));
    }

    private float getProp(Properties props, String name, float def){
        return Float.parseFloat(props.getProperty(name, String.valueOf(def)));
    }

    private String getProp(Properties props, String name, String def){
        return props.getProperty(name, def);
    }

    public class Keybind{

        public final String name;
        private final int def;
        public int key;

        public Keybind(String name, int def){
            this.def = def;
            this.name = name;

            Settings.this.keybinds.add(this);
        }
    }
}
