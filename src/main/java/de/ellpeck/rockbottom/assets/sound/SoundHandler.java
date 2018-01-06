package de.ellpeck.rockbottom.assets.sound;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import org.lwjgl.openal.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class SoundHandler{

    private static final int MAX_SOURCES = 64;
    private static final List<Integer> SOURCES = new ArrayList<>();

    private static long context;
    private static long device;

    public static void init(){
        RockBottomAPI.logger().info("Initializing sounds");

        String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        device = ALC10.alcOpenDevice(defaultDeviceName);

        context = ALC10.alcCreateContext(device, new int[]{0});
        ALC10.alcMakeContextCurrent(context);

        ALCCapabilities caps = ALC.createCapabilities(device);
        AL.createCapabilities(caps);

        for(int i = 0; i < MAX_SOURCES; i++){
            try{
                int source = AL10.alGenSources();

                int error = AL10.alGetError();
                if(error == AL10.AL_NO_ERROR){
                    SOURCES.add(source);
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
            AL10.alListener3f(AL10.AL_POSITION, 0F, 0F, 0F);
            AL10.alListener3f(AL10.AL_VELOCITY, 0F, 0F, 0F);
            AL10.alListener3f(AL10.AL_ORIENTATION, 0F, 0F, -1F);
        }
    }

    public static int playAsSoundAt(int buffer, float pitch, float volume, boolean loop, float x, float y, float z){
        volume *= RockBottomAPI.getGame().getSettings().soundVolume;

        if(volume > 0F){
            int index = findFreeSourceIndex();
            if(index >= 0){
                int source = SOURCES.get(index);

                AL10.alSourceStop(source);

                AL10.alSourcei(source, AL10.AL_BUFFER, buffer);
                AL10.alSourcef(source, AL10.AL_PITCH, pitch);
                AL10.alSourcef(source, AL10.AL_GAIN, volume);
                AL10.alSourcei(source, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);

                AL10.alSource3f(source, AL10.AL_POSITION, x, y, z);
                AL10.alSource3f(source, AL10.AL_VELOCITY, 0F, 0F, 0F);

                AL10.alSourcePlay(source);

                return index;
            }
        }
        return -1;
    }

    public static boolean isPlaying(int index){
        return AL10.alGetSourcei(SOURCES.get(index), AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    private static int findFreeSourceIndex(){
        for(int i = 0; i < SOURCES.size(); i++){
            int state = AL10.alGetSourcei(SOURCES.get(i), AL10.AL_SOURCE_STATE);
            if(state != AL10.AL_PLAYING && state != AL10.AL_PAUSED){
                return i;
            }
        }
        return -1;
    }

    public static void stopSoundEffect(int id){
        AL10.alSourceStop(id);
    }

    public static void dispose(){
        for(int source : SOURCES){
            AL10.alDeleteSources(source);
        }

        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
    }
}
