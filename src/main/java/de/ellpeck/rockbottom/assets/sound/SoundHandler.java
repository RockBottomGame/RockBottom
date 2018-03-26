package de.ellpeck.rockbottom.assets.sound;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import org.lwjgl.openal.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class SoundHandler{

    private static final int MAX_SOURCES = 64;
    private static final List<Integer> SOURCES = new ArrayList<>();
    private static final Map<Integer, SoundEffect> CURRENT_EFFECTS = new HashMap<>();

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
                SoundEffect current = CURRENT_EFFECTS.get(index);
                if(current != null){
                    current.stopIndex(index);
                }

                CURRENT_EFFECTS.put(index, effect);

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

    private static float ensureVolume(float volume){
        return volume*RockBottomAPI.getGame().getSettings().soundVolume;
    }

    public static boolean isPlaying(int index){
        return AL10.alGetSourcei(getSource(index), AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public static int getSource(int index){
        return SOURCES.get(index);
    }

    private static int findFreeSourceIndex(){
        for(int i = 0; i < SOURCES.size(); i++){
            if(!isPlaying(i)){
                return i;
            }
        }
        return -1;
    }

    public static void stopSoundEffect(int index){
        AL10.alSourceStop(getSource(index));
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
