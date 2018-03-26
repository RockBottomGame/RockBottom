package de.ellpeck.rockbottom.assets.stub;

import de.ellpeck.rockbottom.api.assets.ISound;

import java.util.Collections;
import java.util.Set;

public class EmptySound implements ISound{

    @Override
    public void play(){

    }

    @Override
    public void play(float pitch, float volume){

    }

    @Override
    public void play(float pitch, float volume, boolean loop){

    }

    @Override
    public void playAt(double x, double y, double z){

    }

    @Override
    public void playAt(float pitch, float volume, double x, double y, double z){

    }

    @Override
    public void playAt(float pitch, float volume, double x, double y, double z, boolean loop){

    }

    @Override
    public void playAt(float pitch, float volume, double x, double y, double z, boolean loop, float rolloffFactor, float refDistance, float maxDistance){

    }

    @Override
    public boolean isIndexPlaying(int index){
        return false;
    }

    @Override
    public boolean isPlaying(){
        return false;
    }

    @Override
    public void stop(){

    }

    @Override
    public void stopIndex(int index){

    }

    @Override
    public Set<Integer> getPlayingSourceIds(){
        return Collections.emptySet();
    }

    @Override
    public void dispose(){

    }
}
