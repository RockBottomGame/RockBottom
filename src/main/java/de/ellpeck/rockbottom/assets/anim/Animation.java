package de.ellpeck.rockbottom.assets.anim;

import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.assets.IAnimation;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.util.Util;

import java.util.List;

public class Animation implements IAnimation {

    private final int frameWidth;
    private final int frameHeight;

    private final ITexture texture;
    private final List<AnimationRow> rows;

    public Animation(ITexture texture, int frameWidth, int frameHeight, List<AnimationRow> rows) {
        this.texture = texture;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.rows = rows;
    }

    @Override
    public void drawFrame(int row, int frame, float x, float y, float width, float height, int filter) {
        this.drawFrame(row, frame, x, y, width, height, null, filter);
    }

    @Override
    public void drawFrame(int row, int frame, float x, float y, float width, float height, int[] light, int filter) {
        this.drawFrame(row, frame, x, y, x + width, y + height, 0, 0, this.frameWidth, this.frameHeight, light, filter);
    }

    @Override
    public void drawFrame(int row, int frame, float x, float y, float scale, int filter) {
        this.drawFrame(row, frame, x, y, scale, null, filter);
    }

    @Override
    public void drawFrame(int row, int frame, float x, float y, float scale, int[] light, int filter) {
        this.drawFrame(row, frame, x, y, this.frameWidth * scale, this.frameHeight * scale, light, filter);
    }

    @Override
    public void drawFrame(int row, int frame, float x1, float y1, float x2, float y2, float srcX1, float srcY1, float srcX2, float srcY2, int[] light, int filter) {
        if (frame < 0 || frame >= this.getFrameAmount(row)) {
            frame = 0;
        }

        float srcX = frame * this.frameWidth;
        float srcY = row * this.frameHeight;

        this.texture.draw(x1, y1, x2, y2, srcX + srcX1, srcY + srcY1, srcX + srcX2, srcY + srcY2, light, filter);
    }

    @Override
    public void drawRow(int row, float x, float y, float width, float height, int filter) {
        this.drawRow(0L, row, x, y, width, height, null, filter);
    }

    @Override
    public void drawRow(int row, float x, float y, float width, float height, int[] light, int filter) {
        this.drawRow(0L, row, x, y, x + width, y + height, 0, 0, this.frameWidth, this.frameHeight, light, filter);
    }

    @Override
    public void drawRow(int row, float x, float y, float width, float height, int filter, boolean mirrorHor, boolean mirrorVert) {
        this.drawRow(0L, row, x, y, width, height, filter, mirrorHor, mirrorVert);
    }

    @Override
    public void drawRow(int row, float x, float y, float width, float height, int[] light, int filter, boolean mirrorHor, boolean mirrorVert) {
        this.drawRow(0L, row, x, y, width, height, light, filter, mirrorHor, mirrorVert);
    }

    @Override
    public void drawRow(int row, float x, float y, float scale, int filter) {
        this.drawRow(0L, row, x, y, scale, null, filter);
    }

    @Override
    public void drawRow(int row, float x, float y, float scale, int[] light, int filter) {
        this.drawRow(0L, row, x, y, this.frameWidth * scale, this.frameHeight * scale, light, filter);
    }

    @Override
    public void drawRow(int row, float x1, float y1, float x2, float y2, float srcX1, float srcY1, float srcX2, float srcY2, int[] light, int filter) {
        this.drawRow(0L, row, x1, y1, x2, y2, srcX1, srcY1, srcX2, srcY2, light, filter);
    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x, float y, float width, float height, int filter) {
        this.drawRow(startTimeMillis, row, x, y, width, height, null, filter);
    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x, float y, float width, float height, int[] light, int filter) {
        this.drawRow(startTimeMillis, row, x, y, x + width, y + height, 0, 0, this.frameWidth, this.frameHeight, light, filter);
    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x, float y, float scale, int filter) {
        this.drawRow(startTimeMillis, row, x, y, scale, null, filter);
    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x, float y, float width, float height, int filter, boolean mirrorHor, boolean mirrorVert) {
        this.drawRow(startTimeMillis, row, x, y, width, height, null, filter, mirrorHor, mirrorVert);
    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x, float y, float width, float height, int[] light, int filter, boolean mirrorHor, boolean mirrorVert) {
        float srcX = mirrorHor ? this.frameWidth : 0;
        float srcY = mirrorVert ? this.frameHeight : 0;
        float srcX2 = mirrorHor ? 0 : this.frameWidth;
        float srcY2 = mirrorVert ? 0 : this.frameHeight;
        this.drawRow(startTimeMillis, row, x, y, x + width, y + height, srcX, srcY, srcX2, srcY2, light, filter);
    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x, float y, float scale, int[] light, int filter) {
        this.drawRow(startTimeMillis, row, x, y, this.frameWidth * scale, this.frameHeight * scale, light, filter);
    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x1, float y1, float x2, float y2, float srcX1, float srcY1, float srcX2, float srcY2, int[] light, int filter) {
        this.drawFrame(row, this.getFrameByTime(row, Util.getTimeMillis() - startTimeMillis), x1, y1, x2, y2, srcX1, srcY1, srcX2, srcY2, light, filter);
    }

    @Override
    public int getFrameAmount(int row) {
        return this.getRow(row).getFrameAmount();
    }

    @Override
    public long getTotalTime(int row) {
        return (long) (this.getRow(row).getTotalTime() * 1000F);
    }

    @Override
    public long getFrameTime(int row, int frame) {
        return (long) (this.getRow(row).getTime(frame) * 1000F);
    }

    private AnimationRow getRow(int row) {
        if (row < 0 || row >= this.rows.size()) {
            row = 0;
        }

        return this.rows.get(row);
    }

    @Override
    public ITexture getTexture() {
        return this.texture;
    }

    @Override
    public int getFrameWidth() {
        return this.frameWidth;
    }

    @Override
    public int getFrameHeight() {
        return this.frameHeight;
    }

    @Override
    public int getFrameByTime(int row, long millis) {
        long runningTime = millis % this.getTotalTime(row);

        long accum = 0;
        for (int i = 0; i < this.getFrameAmount(row); i++) {
            accum += this.getFrameTime(row, i);
            if (accum >= runningTime) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public JsonElement[] getAdditionalFrameData(String name, int row) {
        return this.getRow(row).getAdditionalData(name);
    }

    @Override
    public JsonElement getAdditionalFrameData(String name, int row, int frame) {
        return this.getRow(row).getAdditionalData(name, frame);
    }

    @Override
    public void dispose() {
        this.texture.dispose();
    }
}
