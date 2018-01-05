package de.ellpeck.rockbottom.assets.sound;

import de.ellpeck.rockbottom.api.assets.ISound;

import java.io.IOException;
import java.io.InputStream;

public class SoundEffect implements ISound{

    //TODO Load sounds
    public SoundEffect(InputStream stream) throws IOException{

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

    //TODO Figure all this out
    @Override
    public void playAt(float pitch, float volume, double x, double y, double z, boolean loop){

    }

    @Override
    public boolean isPlaying(){
        return false;
    }

    @Override
    public void stop(){

    }

    @Override
    public void dispose(){

    }
}
