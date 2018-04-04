package de.ellpeck.rockbottom.assets.sound;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.ISound;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.lwjgl.openal.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class SoundHandler{

    private static final int MAX_SOURCES = 64;
    private static final List<Integer> SOURCES = new ArrayList<>();
    private static final ISound[] PLAYING_SOUNDS = new ISound[MAX_SOURCES];
    private static final List<StreamSound> STREAM_SOUNDS = new ArrayList<>();

    public static final float ROLLOFF = 1F;
    public static final float REF_DIST = 3F;
    public static final float MAX_DIST = 20F;

    private static long context;
    private static long device;

    public static float playerX;
    public static float playerY;
    public static float playerZ = -10F;

    public static void init(){
        RockBottomAPI.logger().info("Initializing sounds");

        String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        device = ALC10.alcOpenDevice(defaultDeviceName);

        context = ALC10.alcCreateContext(device, new int[]{0});
        ALC10.alcMakeContextCurrent(context);

        ALCCapabilities caps = ALC.createCapabilities(device);
        AL.createCapabilities(caps);

        AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);

        for(int i = 0; i < MAX_SOURCES; i++){
            try{
                int source = AL10.alGenSources();

                int error = AL10.alGetError();
                if(error == AL10.AL_NO_ERROR){
                    SOURCES.add(source);
                    AL10.alSource3f(source, AL10.AL_VELOCITY, 0F, 0F, 0F);
                }
                else{
                    RockBottomAPI.logger().warning("Couldn't initialize source:\n"+AL10.alGetString(error));
                    break;
                }
            }
            catch(Exception e){
                RockBottomAPI.logger().log(Level.WARNING, "Failed to initialize sounds", e);
            }
        }

        RockBottomAPI.logger().info("Finished initializing sounds creating a total of "+SOURCES.size()+" sound sources");

        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR){
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't initialize sounds:\n"+AL10.alGetString(error));
        }
        else{
            AL10.alListener3f(AL10.AL_POSITION, playerX, playerY, playerZ);
        }
    }

    public static void setPlayerPos(double x, double y){
        playerX = (float)x;
        playerY = (float)y;
        AL10.alListener3f(AL10.AL_POSITION, playerX, playerY, playerZ);
    }

    public static int playAsSoundAt(SoundEffect effect, int id, float pitch, float volume, boolean loop, float x, float y, float z, float rolloffFactor, float refDistance, float maxDistance){
        volume = ensureVolume(volume);
        if(volume > 0F){
            int index = findFreeSourceIndex();
            if(index >= 0){
                addPlayingSound(index, effect);

                int source = getSource(index);
                AL10.alSourcei(source, AL10.AL_BUFFER, id);
                AL10.alSourcef(source, AL10.AL_PITCH, pitch);
                AL10.alSourcef(source, AL10.AL_GAIN, volume);
                AL10.alSourcei(source, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);

                AL10.alSource3f(source, AL10.AL_POSITION, x, y, z);
                AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, rolloffFactor);
                AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, refDistance);
                AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, maxDistance);

                AL10.alSourcePlay(source);

                return index;
            }
        }
        return -1;
    }

    public static boolean playStreamSoundAt(StreamSound sound, int index, float pitch, float volume, float x, float y, float z, float rolloffFactor, float refDistance, float maxDistance){
        volume = ensureVolume(volume);
        if(volume > 0F){
            addPlayingSound(index, sound);

            int source = getSource(index);
            AL10.alSourcef(source, AL10.AL_PITCH, pitch);
            AL10.alSourcef(source, AL10.AL_GAIN, volume);
            AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE);

            AL10.alSource3f(source, AL10.AL_POSITION, x, y, z);
            AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, rolloffFactor);
            AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, refDistance);
            AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, maxDistance);

            AL10.alSourcePlay(source);

            return true;
        }
        else{
            return false;
        }
    }

    private static float ensureVolume(float volume){
        return volume*RockBottomAPI.getGame().getSettings().soundVolume;
    }

    public static boolean isPlaying(int index){
        return AL10.alGetSourcei(getSource(index), AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public static int getSource(int index){
        return SOURCES.get(index);
    }

    public static int findFreeSourceIndex(){
        for(int i = 0; i < MAX_SOURCES; i++){
            if(PLAYING_SOUNDS[i] == null){
                return i;
            }
        }
        return -1;
    }

    public static int getFreeSources(){
        int amount = 0;
        for(int i = 0; i < SOURCES.size(); i++){
            if(!isPlaying(i)){
                amount++;
            }
        }
        return amount;
    }

    public static int getStreamingSoundAmount(){
        return STREAM_SOUNDS.size();
    }

    public static int getPlayingSoundAmount(){
        int amount = 0;
        for(int i = 0; i < MAX_SOURCES; i++){
            if(PLAYING_SOUNDS[i] != null){
                amount++;
            }
        }
        return amount;
    }

    public static void stopSoundEffect(int index){
        AL10.alSourceStop(getSource(index));
    }

    public static void addPlayingSound(int index, ISound sound){
        PLAYING_SOUNDS[index] = sound;
    }

    public static void addStreamingSound(StreamSound sound){
        STREAM_SOUNDS.add(sound);
    }

    public static void removeStreamingSound(StreamSound sound){
        STREAM_SOUNDS.remove(sound);
    }

    public static void updateSounds(AbstractGame game){
        long lastStreamTime = Util.getTimeMillis();
        long lastSoundTime = Util.getTimeMillis();

        while(game.isRunning){
            try{
                long currTime = Util.getTimeMillis();

                if(currTime >= lastStreamTime+5000){
                    lastStreamTime = currTime;

                    if(!STREAM_SOUNDS.isEmpty()){
                        for(int i = 0; i < STREAM_SOUNDS.size(); i++){
                            StreamSound sound = STREAM_SOUNDS.get(i);
                            if(sound.isPlaying()){
                                try{
                                    sound.update();
                                }
                                catch(Exception e){
                                    RockBottomAPI.logger().log(Level.WARNING, "There was an error streaming a sound", e);
                                }
                            }
                        }
                    }
                }

                if(currTime >= lastSoundTime+250){
                    lastSoundTime = currTime;

                    for(int i = 0; i < MAX_SOURCES; i++){
                        ISound sound = PLAYING_SOUNDS[i];
                        if(sound != null && !sound.isIndexPlaying(i)){
                            sound.stopIndex(i);
                            PLAYING_SOUNDS[i] = null;
                        }
                    }
                }
            }
            catch(Exception e){
                RockBottomAPI.logger().log(Level.WARNING, "There was an exception in the sound handling thread, but it will attempt to keep running", e);
            }

            Util.sleepSafe(1);
        }
    }

    public static void dispose(){
        for(int source : SOURCES){
            AL10.alDeleteSources(source);
        }

        if(context != 0){
            ALC10.alcDestroyContext(context);
        }
        if(device != 0){
            ALC10.alcCloseDevice(device);
        }
    }
}
