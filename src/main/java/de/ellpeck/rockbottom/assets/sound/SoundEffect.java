package de.ellpeck.rockbottom.assets.sound;

import com.google.common.base.Preconditions;
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
import java.util.HashSet;
import java.util.Set;

public class SoundEffect implements ISound {

    private final int id;
    private final Set<Integer> currentIndices = new HashSet<>();

    public SoundEffect(InputStream stream) throws Exception {
        this.id = AL10.alGenBuffers();

        STBVorbisInfo info = STBVorbisInfo.malloc();
        ShortBuffer pcm = this.readVorbis(stream, info);
        AL10.alBufferData(this.id, info.channels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, pcm, info.sample_rate());
        info.free();
    }

    private ShortBuffer readVorbis(InputStream stream, STBVorbisInfo info) throws Exception {
        byte[] input = ByteStreams.toByteArray(stream);
        stream.close();

        ByteBuffer data = BufferUtils.createByteBuffer(input.length);
        data.put(input);
        ((Buffer) data).flip();

        IntBuffer error = BufferUtils.createIntBuffer(1);
        long decoder = STBVorbis.stb_vorbis_open_memory(data, error, null);
        Preconditions.checkState(decoder != MemoryUtil.NULL, "Failed to load sound:\n" + error.get(0));

        STBVorbis.stb_vorbis_get_info(decoder, info);

        int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);
        ShortBuffer pcm = BufferUtils.createShortBuffer(lengthSamples);

        int channels = info.channels();
        ((Buffer) pcm).limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
        STBVorbis.stb_vorbis_close(decoder);

        return pcm;
    }

    @Override
    public void play() {
        this.play(1F, 1F);
    }

    @Override
    public void play(float pitch, float volume) {
        this.play(pitch, volume, false);
    }

    @Override
    public void play(float pitch, float volume, boolean loop) {
        this.playAt(pitch, volume, SoundHandler.listenerX, SoundHandler.listenerY, SoundHandler.playerZ, loop);
    }

    @Override
    public void playAt(double x, double y, double z) {
        this.playAt(1F, 1F, x, y, z);
    }

    @Override
    public void playAt(float pitch, float volume, double x, double y, double z) {
        this.playAt(pitch, volume, x, y, z, false);
    }

    @Override
    public void playAt(float pitch, float volume, double x, double y, double z, boolean loop) {
        this.playAt(pitch, volume, x, y, z, loop, SoundHandler.ROLLOFF, SoundHandler.REF_DIST, SoundHandler.MAX_DIST);
    }

    @Override
    public void playAt(float pitch, float volume, double x, double y, double z, boolean loop, float rolloffFactor, float refDistance, float maxDistance) {
        int index = SoundHandler.playAsSoundAt(this, this.id, pitch, volume, loop, (float) x, (float) y, (float) z, rolloffFactor, refDistance, maxDistance);
        if (index >= 0) {
            this.currentIndices.add(index);
        }
    }

    @Override
    public boolean isIndexPlaying(int index) {
        return this.currentIndices.contains(index) && SoundHandler.isPlaying(index);
    }

    @Override
    public boolean isPlaying() {
        for (int i : this.currentIndices) {
            if (this.isIndexPlaying(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void stop() {
        for (int i : this.currentIndices) {
            SoundHandler.stopSoundEffect(i);
        }
        this.currentIndices.clear();
    }

    @Override
    public void stopIndex(int index) {
        if (this.currentIndices.contains(index)) {
            SoundHandler.stopSoundEffect(index);
            this.currentIndices.remove(index);
        }
    }

    @Override
    public Set<Integer> getPlayingSourceIds() {
        return this.currentIndices;
    }

    @Override
    public void dispose() {
        this.stop();
        AL10.alDeleteBuffers(this.id);
    }
}
