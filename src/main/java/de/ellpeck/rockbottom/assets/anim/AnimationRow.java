package de.ellpeck.rockbottom.assets.anim;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

public class AnimationRow {

    private final float[] frameTimes;
    private final float totalTime;
    private Map<String, JsonElement[]> additionalFrameData;

    public AnimationRow(float[] frameTimes) {
        this.frameTimes = frameTimes;

        float accumulator = 0F;
        for (float f : frameTimes) {
            accumulator += f;
        }
        this.totalTime = accumulator;
    }

    public void addAdditionalFrameData(String name, JsonElement[] data) {
        if (this.additionalFrameData == null) {
            this.additionalFrameData = new HashMap<>();
        }
        this.additionalFrameData.put(name, data);
    }

    public JsonElement[] getAdditionalData(String name) {
        return this.additionalFrameData != null ? this.additionalFrameData.get(name) : null;
    }

    public JsonElement getAdditionalData(String name, int frame) {
        JsonElement[] data = this.getAdditionalData(name);
        return data != null ? data[frame] : null;
    }

    public int getFrameAmount() {
        return this.frameTimes.length;
    }

    public float getTotalTime() {
        return this.totalTime;
    }

    public float getTime(int frame) {
        return this.frameTimes[frame];
    }
}
