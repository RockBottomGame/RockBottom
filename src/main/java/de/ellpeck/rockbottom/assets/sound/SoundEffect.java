package de.ellpeck.rockbottom.assets.sound;

import de.ellpeck.rockbottom.api.assets.ISound;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import java.io.InputStream;

public class SoundEffect extends Sound implements ISound{

    public SoundEffect(InputStream in, String ref) throws SlickException{
        super(in, ref);
    }

    @Override
    public void playAt(double x, double y, double z){
        this.playAt((float)x, (float)y, (float)z);
    }

    @Override
    public void playAt(float pitch, float volume, double x, double y, double z){
        this.playAt(pitch, volume, (float)x, (float)y, (float)z);
    }

    @Override
    public boolean isPlaying(){
        return this.playing();
    }
}
