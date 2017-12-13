package de.ellpeck.rockbottom.assets.sound;

import de.ellpeck.rockbottom.api.assets.ISound;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;

import java.io.IOException;
import java.io.InputStream;

public class SoundEffect implements ISound{

    private final Audio underlyingSound;

    public SoundEffect(InputStream stream) throws IOException{
        this(AudioLoader.getAudio("OGG", stream));
    }

    public SoundEffect(Audio sound){
        this.underlyingSound = sound;
    }

    @Override
    public void play(){
        this.play(1F, 1F);
    }

    @Override
    public void play(float pitch, float volume){
        this.play(pitch, volume, false);
    }

    @Override
    public void play(float pitch, float volume, boolean loop){
        this.playAt(pitch, volume, 0D, 0D, 0D, loop);
    }

    @Override
    public void playAt(double x, double y, double z){
        this.playAt(1F, 1F, x, y, z);
    }

    @Override
    public void playAt(float pitch, float volume, double x, double y, double z){
        this.playAt(pitch, volume, x, y, z, false);
    }

    @Override
    public void playAt(float pitch, float volume, double x, double y, double z, boolean loop){
        this.underlyingSound.playAsSoundEffect(pitch, volume, loop, (float)x, (float)y, (float)z);
    }

    @Override
    public boolean isPlaying(){
        return this.underlyingSound.isPlaying();
    }

    @Override
    public void stop(){
        this.underlyingSound.stop();
    }
}
