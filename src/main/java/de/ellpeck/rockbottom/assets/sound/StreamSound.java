package de.ellpeck.rockbottom.assets.sound;

import de.ellpeck.rockbottom.api.assets.ISound;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.Set;

public class StreamSound implements ISound{

    public static final int COMPRESSED_BUFFER_SIZE = 4096;
    public static final int UNCOMPRESSED_BUFFER_SECONDS = 10;
    public static final int AL_BUFFERS = 2;
    public static final int NEED_MORE_DATA_INCREMENT = 256;

    private final URL url;
    private final IntBuffer ids;
    private final IntBuffer processedBuffer;
    private final byte[] buffer = new byte[COMPRESSED_BUFFER_SIZE];
    private InputStream stream;
    private int limit;
    private long vorbis;
    private final int firstUsed;
    private final ByteBuffer compressedBuffer;
    private final PointerBuffer uncompressedBuffer;
    private final ShortBuffer preAlBuffer;
    private final int channels;
    private final int sampleRate;
    private int currentIndex = -1;
    private boolean loading = true;
    private boolean shouldPlay = false;

    private float pitch;
    private float volume;
    private float x;
    private float y;
    private float z;
    private boolean loop;
    private float rolloff;
    private float refDist;
    private float maxDist;

    public StreamSound(URL url) throws Exception{
        this.url = url;
        this.stream = url.openStream();

        this.ids = MemoryUtil.memAllocInt(AL_BUFFERS);
        this.compressedBuffer = MemoryUtil.memAlloc(COMPRESSED_BUFFER_SIZE);
        this.processedBuffer = MemoryUtil.memAllocInt(1);

        AL10.alGenBuffers(this.ids);

        ByteBuffer buf = MemoryUtil.memAlloc(COMPRESSED_BUFFER_SIZE);

        this.stream.read(this.buffer);
        buf.put(this.buffer);
        this.limit = buf.position();
        buf.position(0);

        MemoryStack stack = MemoryStack.stackPush();
        IntBuffer error = stack.mallocInt(1);
        IntBuffer used = stack.mallocInt(1);

        while(true){
            this.vorbis = STBVorbis.stb_vorbis_open_pushdata(buf, used, error, null);
            if(this.vorbis == MemoryUtil.NULL){
                int err = error.get(0);
                if(err == STBVorbis.VORBIS_need_more_data){
                    buf = MemoryUtil.memRealloc(buf, buf.capacity()+NEED_MORE_DATA_INCREMENT);
                    buf.position(this.limit);

                    int read = this.stream.read(this.buffer, 0, buf.remaining());
                    buf.put(this.buffer, 0, read);
                    this.limit = buf.position();
                    buf.position(0);
                }
                else{
                    throw new IllegalStateException("Failed to create vorbis stream: "+err);
                }
            }
            else{
                break;
            }
        }

        this.firstUsed = used.get(0);
        buf.position(this.firstUsed);
        this.limit -= buf.position();

        this.compressedBuffer.put(buf);
        this.compressedBuffer.position(0);

        stack.pop();
        MemoryUtil.memFree(buf);

        STBVorbisInfo info = STBVorbisInfo.malloc();
        STBVorbis.stb_vorbis_get_info(this.vorbis, info);
        this.channels = info.channels();
        this.sampleRate = info.sample_rate();
        STBVorbis.stb_vorbis_flush_pushdata(this.vorbis);

        this.uncompressedBuffer = MemoryUtil.memAllocPointer(this.channels*this.sampleRate*UNCOMPRESSED_BUFFER_SECONDS);
        this.preAlBuffer = MemoryUtil.memAllocShort(this.channels*this.sampleRate*UNCOMPRESSED_BUFFER_SECONDS);
    }

    public void update() throws Exception{
        int index = this.currentIndex >= 0 ? this.currentIndex : SoundHandler.findFreeSourceIndex();
        int source = SoundHandler.getSource(index);

        if(this.shouldPlay){
            int processed = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED);
            if(this.loading){
                processed = AL_BUFFERS;
            }

            while(processed > 0){
                processed--;

                this.processedBuffer.clear();
                if(this.loading){
                    this.processedBuffer.put(this.ids.get(processed));
                }
                else{
                    AL10.alSourceUnqueueBuffers(source, this.processedBuffer);
                }

                int buf = this.processedBuffer.get(0);
                this.buffer(buf);
                AL10.alSourceQueueBuffers(source, buf);

                if(!this.shouldPlay){
                    return;
                }
            }
            this.loading = false;

            if(!SoundHandler.isPlaying(index)){
                if(SoundHandler.playStreamSoundAt(this, index, this.pitch, this.volume, this.x, this.y, this.z, this.rolloff, this.refDist, this.maxDist)){
                    this.currentIndex = index;
                }
            }
        }
        else if(this.loop){
            this.stream.close();
            this.stream = this.url.openStream();
            this.stream.skip(this.firstUsed);

            this.compressedBuffer.clear();
            this.limit = 0;
        }
    }

    private void buffer(int id) throws Exception{
        this.preAlBuffer.clear();
        while(true){
            int remaining = this.compressedBuffer.remaining();
            if(remaining < 1024){
                this.compressedBuffer.get(this.buffer, 0, remaining);
                this.compressedBuffer.clear();
                this.compressedBuffer.put(this.buffer, 0, remaining);
                this.compressedBuffer.position(0);
                this.limit = remaining;
            }

            int pos = this.compressedBuffer.position();
            this.compressedBuffer.position(this.limit);
            remaining = this.compressedBuffer.remaining();

            int read = this.stream.read(this.buffer, 0, remaining);
            if(read != -1 && (read != 0 || remaining == 0)){
                this.compressedBuffer.put(this.buffer, 0, read);
                this.limit = this.compressedBuffer.position();
                this.compressedBuffer.position(pos);
            }
            else{
                if(this.preAlBuffer.remaining() > 0){
                    this.preAlBuffer.limit(this.preAlBuffer.position());
                    this.preAlBuffer.position(0);

                    AL10.alBufferData(id, this.channels == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, this.preAlBuffer, this.sampleRate);
                }

                this.shouldPlay = false;
                return;
            }

            MemoryStack stack = MemoryStack.stackPush();
            IntBuffer samples = stack.mallocInt(1);

            int used = STBVorbis.stb_vorbis_decode_frame_pushdata(this.vorbis, this.compressedBuffer, null, this.uncompressedBuffer, samples);
            this.compressedBuffer.position(this.compressedBuffer.position()+used);

            int sampleAmount = samples.get(0);
            stack.pop();

            int error = STBVorbis.stb_vorbis_get_error(this.vorbis);
            if(error != STBVorbis.VORBIS__no_error && error != STBVorbis.VORBIS_need_more_data){
                this.shouldPlay = false;
                throw new IOException("Invalid stream, error: "+error);
            }

            if(sampleAmount > 0){
                PointerBuffer channelBuf = this.uncompressedBuffer.getPointerBuffer(this.channels);
                if(this.channels == 1){
                    FloatBuffer channel = channelBuf.getFloatBuffer(sampleAmount);
                    for(int i = 0; i < sampleAmount; i++){
                        this.preAlBuffer.put((short)(channel.get()*Short.MAX_VALUE));
                    }
                }
                else{
                    FloatBuffer channel1 = channelBuf.getFloatBuffer(sampleAmount);
                    FloatBuffer channel2 = channelBuf.getFloatBuffer(sampleAmount);
                    for(int i = 0; i < sampleAmount; i++){
                        this.preAlBuffer.put((short)(channel1.get()*Short.MAX_VALUE));
                        this.preAlBuffer.put((short)(channel2.get()*Short.MAX_VALUE));
                    }
                }

                this.uncompressedBuffer.clear();

                if(this.preAlBuffer.remaining() < 2048*this.channels){
                    this.preAlBuffer.limit(this.preAlBuffer.position());
                    this.preAlBuffer.position(0);

                    AL10.alBufferData(id, this.channels == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, this.preAlBuffer, this.sampleRate);
                    break;
                }
            }
        }
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
        this.playAt(pitch, volume, SoundHandler.playerX, SoundHandler.playerY, SoundHandler.playerZ, loop);
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
        this.playAt(pitch, volume, x, y, z, loop, SoundHandler.ROLLOFF, SoundHandler.REF_DIST, SoundHandler.MAX_DIST);
    }

    @Override
    public void playAt(float pitch, float volume, double x, double y, double z, boolean loop, float rolloffFactor, float refDistance, float maxDistance){
        this.pitch = pitch;
        this.volume = volume;
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
        this.loop = loop;
        this.rolloff = rolloffFactor;
        this.refDist = refDistance;
        this.maxDist = maxDistance;

        if(!this.isPlaying()){
            SoundHandler.addStreamingSound(this);
            this.shouldPlay = true;
        }
    }

    @Override
    public boolean isIndexPlaying(int index){
        return this.isPlaying();
    }

    @Override
    public boolean isPlaying(){
        return this.shouldPlay || (this.currentIndex >= 0 && SoundHandler.isPlaying(this.currentIndex));
    }

    @Override
    public void stop(){
        this.stopIndex(this.currentIndex);
    }

    @Override
    public void stopIndex(int index){
        if(this.currentIndex >= 0 && index == this.currentIndex){
            SoundHandler.stopSoundEffect(index);
            SoundHandler.removeStreamingSound(this);
            this.currentIndex = -1;
        }
    }

    @Override
    public Set<Integer> getPlayingSourceIds(){
        return this.currentIndex >= 0 ? Collections.singleton(this.currentIndex) : Collections.emptySet();
    }

    @Override
    public void dispose(){
        this.stop();
        AL10.alDeleteBuffers(this.ids);

        MemoryUtil.memFree(this.uncompressedBuffer);
        MemoryUtil.memFree(this.compressedBuffer);
        MemoryUtil.memFree(this.ids);
        MemoryUtil.memFree(this.preAlBuffer);
        MemoryUtil.memFree(this.processedBuffer);

        STBVorbis.stb_vorbis_close(this.vorbis);

        try{
            this.stream.close();
        }
        catch(Exception ignored){
        }
    }
}
