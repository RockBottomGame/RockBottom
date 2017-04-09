package de.ellpeck.game;

import org.newdawn.slick.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Settings{

    private final Properties props;

    public List<Keybind> keybinds = new ArrayList<>();

    public Keybind keyInventory = new Keybind("inventory", Input.KEY_E);
    public Keybind keyMenu = new Keybind("menu", Input.KEY_ESCAPE);
    public Keybind keyLeft = new Keybind("left", Input.KEY_A);
    public Keybind keyRight = new Keybind("right", Input.KEY_D);
    public Keybind keyJump = new Keybind("jump", Input.KEY_SPACE);
    public Keybind keyBackground = new Keybind("background", Input.KEY_LSHIFT);

    public int targetFps;
    public int autosaveIntervalSeconds;

    public int guiScale;
    public int renderScale;

    public float cursorScale;
    public boolean hardwareCursor;

    public int buttonDestroy;
    public int buttonPlace;
    public int buttonGuiAction1;
    public int buttonGuiAction2;

    public int[] keysItemSelection = new int[8];

    public Settings(Properties props){
        this.props = props;
    }

    public void load(){
        for(Keybind bind : this.keybinds){
            bind.key = this.getProp("key_"+bind.name, bind.def);
        }

        this.targetFps = this.getProp("target_fps", 60);
        this.autosaveIntervalSeconds = this.getProp("autosave_interval", 60);

        this.guiScale = this.getProp("gui_scale", 4);
        this.renderScale = this.getProp("render_scale", 48);

        this.cursorScale = this.getProp("cursor_scale", 3F);
        this.hardwareCursor = this.getProp("hardware_cursor", false);

        this.buttonDestroy = this.getProp("button_destroy", Input.MOUSE_LEFT_BUTTON);
        this.buttonPlace = this.getProp("button_place", Input.MOUSE_RIGHT_BUTTON);
        this.buttonGuiAction1 = this.getProp("button_gui_1", Input.MOUSE_LEFT_BUTTON);
        this.buttonGuiAction2 = this.getProp("button_gui_2", Input.MOUSE_RIGHT_BUTTON);

        int[] defaultKeys = new int[]{Input.KEY_1, Input.KEY_2, Input.KEY_3, Input.KEY_4, Input.KEY_5, Input.KEY_6, Input.KEY_7, Input.KEY_8};
        for(int i = 0; i < this.keysItemSelection.length; i++){
            this.keysItemSelection[i] = this.getProp("key_item_select_"+i, defaultKeys[i]);
        }
    }

    public Properties save(){
        this.props.clear();

        for(Keybind bind : this.keybinds){
            this.setProp("key_"+bind.name, bind.key);
        }

        this.setProp("target_fps", this.targetFps);
        this.setProp("autosave_interval", this.autosaveIntervalSeconds);

        this.setProp("gui_scale", this.guiScale);
        this.setProp("render_scale", this.renderScale);

        this.setProp("cursor_scale", this.cursorScale);
        this.setProp("hardware_cursor", this.hardwareCursor);

        this.setProp("button_destroy", this.buttonDestroy);
        this.setProp("button_place", this.buttonPlace);
        this.setProp("button_gui_1", this.buttonGuiAction1);
        this.setProp("button_gui_2", this.buttonGuiAction2);

        for(int i = 0; i < this.keysItemSelection.length; i++){
            this.setProp("key_item_select_"+i, this.keysItemSelection[i]);
        }

        return this.props;
    }

    private <T> void setProp(String name, T val){
        this.props.setProperty(name, String.valueOf(val));
    }

    private int getProp(String name, int def){
        return Integer.parseInt(this.props.getProperty(name, String.valueOf(def)));
    }

    private boolean getProp(String name, boolean def){
        return Boolean.parseBoolean(this.props.getProperty(name, String.valueOf(def)));
    }

    private float getProp(String name, float def){
        return Float.parseFloat(this.props.getProperty(name, String.valueOf(def)));
    }

    public class Keybind{

        private final int def;
        public final String name;

        public int key;

        public Keybind(String name, int def){
            this.def = def;
            this.name = name;

            Settings.this.keybinds.add(this);
        }
    }
}
