package de.ellpeck.game;

import java.util.Properties;

public class Settings{

    public int guiScale;
    public int renderScale;

    public float cursorScale;
    public boolean hardwareCursor;

    public void load(Properties props){
        this.guiScale = Integer.parseInt(props.getProperty("gui_scale", "4"));
        this.renderScale = Integer.parseInt(props.getProperty("render_scale", "48"));

        this.cursorScale = Float.parseFloat(props.getProperty("cursor_scale", "3F"));
        this.hardwareCursor = Boolean.parseBoolean(props.getProperty("hardware_cursor", "false"));
    }

    public void save(Properties props){
        props.setProperty("gui_scale", String.valueOf(this.guiScale));
        props.setProperty("render_scale", String.valueOf(this.renderScale));

        props.setProperty("cursor_scale", String.valueOf(this.cursorScale));
        props.setProperty("hardware_cursor", String.valueOf(this.hardwareCursor));
    }
}
