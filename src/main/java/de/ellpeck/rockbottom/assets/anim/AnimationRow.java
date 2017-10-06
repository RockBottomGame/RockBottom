package de.ellpeck.rockbottom.assets.anim;

public class AnimationRow{

    private final float[] frameTimes;
    private final float totalTime;

    public AnimationRow(float[] frameTimes){
        this.frameTimes = frameTimes;

        float accumulator = 0F;
        for(float f : frameTimes){
            accumulator += f;
        }
        this.totalTime = accumulator;
    }

    public int getFrameAmount(){
        return this.frameTimes.length;
    }

    public float getTotalTime(){
        return this.totalTime;
    }

    public float getTime(int frame){
        return this.frameTimes[frame];
    }
}
