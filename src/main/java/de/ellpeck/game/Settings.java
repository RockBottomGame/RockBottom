package de.ellpeck.game;

import java.util.Properties;

public class Settings{

    public int guiScale = 4;
    public int renderScale = 48;

    public void load(Properties props) throws Exception{
        this.guiScale = Integer.parseInt(props.getProperty("gui_scale"));
        this.renderScale = Integer.parseInt(props.getProperty("render_scale"));
    }

    public void save(Properties props){
        props.setProperty("gui_scale", String.valueOf(this.guiScale));
        props.setProperty("render_scale", String.valueOf(this.renderScale));
    }
}
