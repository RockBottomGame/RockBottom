package de.ellpeck.rockbottom.assets.sound;

import com.google.common.io.ByteStreams;
import de.ellpeck.rockbottom.api.assets.ISound;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class SoundEffect implements ISound{

    private final int id;
    private int currentIndex;

    public SoundEffect(InputStream stream) throws Exception{
        this.id = AL10.alGenBuffers();

        STBVorbisInfo info = STBVorbisInfo.malloc();
        ShortBuffer pcm = this.readVorbis(stream, info);
        AL10.alBufferData(this.id, info.channels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, pcm, info.sample_rate());
        info.free();
    }

    private ShortBuffer readVorbis(InputStream stream, STBVorbisInfo info) throws Exception{
        byte[] input = ByteStreams.toByteArray(stream);
        ByteBuffer data = BufferUtils.createByteBuffer(input.length);
        data.put(input);
        ((Buffer)data).flip();

        IntBuffer error = BufferUtils.createIntBuffer(1);
        long decoder = STBVorbis.stb_vorbis_open_memory(data, error, null);
        if(decoder == MemoryUtil.NULL){
            throw new RuntimeException("Failed to load sound:\n"+error.get(0));
        }

        STBVorbis.stb_vorbis_get_info(decoder, info);

        int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);
        ShortBuffer pcm = BufferUtils.createShortBuffer(lengthSamples);

        int channels = info.channels();
        ((Buffer)pcm).limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm)*channels);
        STBVorbis.stb_vorbis_close(decoder);

        return pcm;
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
        this.currentIndex = SoundHandler.playAsSoundAt(this.id, pitch, volume, loop, (float)x, (float)y, (float)z);
    }

    @Override
    public boolean isPlaying(){
        return this.currentIndex >= 0 && SoundHandler.isPlaying(this.currentIndex);
    }

    @Override
    public void stop(){
        if(this.currentIndex >= 0){
            SoundHandler.stopSoundEffect(this.currentIndex);
            this.currentIndex = -1;
        }
    }

    @Override
    public void dispose(){
        AL10.alDeleteBuffers(this.id);
    }
}
