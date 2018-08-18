package de.ellpeck.rockbottom.apiimpl;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.IShaderProgram;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.construction.resource.ResInfo;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.TooltipEvent;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.engine.IVAO;
import de.ellpeck.rockbottom.api.render.engine.IVBO;
import de.ellpeck.rockbottom.api.render.engine.TextureBank;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.assets.font.Font;
import de.ellpeck.rockbottom.assets.font.SimpleFont;
import de.ellpeck.rockbottom.assets.shader.ShaderProgram;
import de.ellpeck.rockbottom.assets.stub.SimpleShaderProgram;
import de.ellpeck.rockbottom.assets.tex.Texture;
import de.ellpeck.rockbottom.render.engine.VertexArrayObject;
import de.ellpeck.rockbottom.render.engine.VertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Renderer implements IRenderer {

    private static final ResourceName SLOT_NAME = ResourceName.intern("gui.slot");
    private final IGameInstance game;
    private final VertexBufferObject vbo;
    private final FloatBuffer vertices;
    public ShaderProgram simpleProgram;
    public Font simpleFont;
    public boolean isDebug;
    public boolean isItemInfoDebug;
    public boolean isChunkBorderDebug;
    public boolean isGuiDebug;
    public boolean isLineDebug;
    public boolean isHeightDebug;
    public boolean isBiomeDebug;
    public boolean isBoundBoxDebug;
    public boolean isCullingDebug;
    public double cameraX;
    public double cameraY;
    private IShaderProgram defaultProgram;
    private IShaderProgram program;
    private int vertexAmount;
    private int componentCounter;
    private boolean isDrawing;
    private ITexture texture;
    private int backgroundColor;
    private float displayRatio;
    private float guiScale;
    private float worldScale;
    private float guiWidth;
    private float guiHeight;
    private float worldWidth;
    private float worldHeight;
    private float rotationCenterX;
    private float rotationCenterY;
    private float rotation;
    private float sinRot;
    private float cosRot;
    private float translationX;
    private float translationY;
    private float scaleX;
    private float scaleY;
    private boolean mirroredHor;
    private boolean mirroredVert;
    private int lastFlushes;
    private int flushCounter;

    public Renderer(IGameInstance game) {
        this.game = game;

        this.vbo = new VertexBufferObject(false);
        this.vertices = MemoryUtil.memAllocFloat(Main.vertexCache);

        this.vbo.data(this.vertices.capacity() * Float.BYTES);

        this.simpleProgram = new SimpleShaderProgram(game.getWidth(), game.getHeight());
        this.setDefaultProgram(this.simpleProgram);

        this.simpleFont = new SimpleFont();
    }

    @Override
    public void setDefaultProgram(IShaderProgram defaultProgram) {
        this.defaultProgram = defaultProgram;
        this.setProgram(null);
    }

    @Override
    public void addTexturedRegion(ITexture texture, float x, float y, float x2, float y2, float x3, float y3, float x4, float y4, float srcX, float srcY, float srcX2, float srcY2, int[] light, int filter) {
        this.setTexture(texture);

        float u = (srcX + texture.getRenderOffsetX()) / texture.getTextureWidth();
        float v = (srcY + texture.getRenderOffsetY()) / texture.getTextureHeight();
        float u2 = (srcX2 + texture.getRenderOffsetX()) / texture.getTextureWidth();
        float v2 = (srcY2 + texture.getRenderOffsetY()) / texture.getTextureHeight();

        if (this.mirroredHor) {
            float temp = u2;
            u2 = u;
            u = temp;
        }
        if (this.mirroredVert) {
            float temp = v2;
            v2 = v;
            v = temp;
        }

        int topLeft = this.combineLight(light, ITexture.TOP_LEFT, filter);
        int bottomLeft = this.combineLight(light, ITexture.BOTTOM_LEFT, filter);
        int bottomRight = this.combineLight(light, ITexture.BOTTOM_RIGHT, filter);
        int topRight = this.combineLight(light, ITexture.TOP_RIGHT, filter);

        this.program.getProcessor().addTexturedRegion(this, texture, x, y, x2, y2, x3, y3, x4, y4, u, v, u2, v2, topLeft, bottomLeft, bottomRight, topRight);
    }

    @Override
    public void addTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int color1, int color2, int color3, float u1, float v1, float u2, float v2, float u3, float v3) {
        this.program.getProcessor().addTriangle(this, x1, y1, x2, y2, x3, y3, color1, color2, color3, u1, v1, u2, v2, u3, v3);
    }

    @Override
    public void addVertex(float x, float y, int color, float u, float v) {
        float theX;
        float theY;

        if (this.rotation != 0F) {
            if (this.rotationCenterX != 0F) {
                x -= this.rotationCenterX;
                theX = this.rotationCenterX + x * this.cosRot - y * this.sinRot;
            } else {
                theX = x * this.cosRot - y * this.sinRot;
            }

            if (this.rotationCenterY != 0F) {
                y -= this.rotationCenterY;
                theY = this.rotationCenterY + x * this.sinRot + y * this.cosRot;
            } else {
                theY = x * this.sinRot + y * this.cosRot;
            }

        } else {
            theX = x;
            theY = y;
        }

        if (this.translationX != 0F) {
            theX += this.translationX;
        }
        if (this.translationY != 0F) {
            theY += this.translationY;
        }

        if (this.scaleX != 1F) {
            theX *= this.scaleX;
        }
        if (this.scaleY != 1F) {
            theY *= this.scaleY;
        }

        this.program.getProcessor().addVertex(this, theX, theY, color, u, v);
    }

    @Override
    public IRenderer put(float f) {
        Preconditions.checkState(this.isDrawing, "Can't add vertices to a renderer while it's not drawing!");

        if (this.vertices.remaining() < this.program.getComponentsPerVertex() * 3 && this.vertexAmount % 3 == 0) {
            this.flush();
        }

        this.vertices.put(f);

        this.componentCounter++;
        if (this.componentCounter >= this.program.getComponentsPerVertex()) {
            this.vertexAmount++;
            this.program.getProcessor().onVertexCompleted(this);

            this.componentCounter = 0;
        }

        return this;
    }

    @Override
    public void begin() {
        Preconditions.checkState(!this.isDrawing, "Can't begin a renderer that is already drawing!");

        ((Buffer) this.vertices).clear();
        this.vertexAmount = 0;
        this.flushCounter = 0;

        this.resetTransformation();

        this.isDrawing = true;

        this.program.getProcessor().onBegin(this);
    }

    @Override
    public void end() {
        Preconditions.checkState(this.isDrawing, "Can't end a renderer that isn't drawing!");

        this.flush();

        this.isDrawing = false;

        this.lastFlushes = this.flushCounter;
        this.flushCounter = 0;

        this.program.getProcessor().onEnd(this);
    }

    @Override
    public void flush() {
        if (this.vertexAmount > 0) {
            ((Buffer) this.vertices).flip();
            this.program.bind();

            this.vbo.subData(this.vertices);
            this.program.draw(this.vertexAmount);

            ((Buffer) this.vertices).clear();
            this.vertexAmount = 0;

            this.flushCounter++;

            this.program.getProcessor().onFlush(this);
        }
    }

    @Override
    public void rotate(float angle) {
        this.setRotation(this.rotation + angle);
    }

    @Override
    public void setRotationCenter(float x, float y) {
        this.rotationCenterX = x;
        this.rotationCenterY = y;
    }

    @Override
    public float getRotationCenterX() {
        return this.rotationCenterX;
    }

    @Override
    public float getRotationCenterY() {
        return this.rotationCenterY;
    }

    @Override
    public void translate(float x, float y) {
        this.setTranslation(this.translationX + x, this.translationY + y);
    }

    @Override
    public void setTranslation(float x, float y) {
        this.translationX = x;
        this.translationY = y;
    }

    @Override
    public void scale(float x, float y) {
        this.setScale(this.scaleX * x, this.scaleY * y);
    }

    @Override
    public void setScale(float x, float y) {
        this.scaleX = x;
        this.scaleY = y;
    }

    @Override
    public void mirror(boolean hor, boolean vert) {
        this.setMirrored(hor != this.mirroredHor, vert != this.mirroredVert);
    }

    @Override
    public void setMirrored(boolean hor, boolean vert) {
        this.mirroredHor = hor;
        this.mirroredVert = vert;
    }

    @Override
    public void resetTransformation() {
        this.setRotation(0F);
        this.setTranslation(0F, 0F);
        this.setScale(1F, 1F);
        this.setMirrored(false, false);
        this.setRotationCenter(0F, 0F);
    }

    @Override
    public float getRotation() {
        return this.rotation;
    }

    @Override
    public void setRotation(float angle) {
        this.rotation = angle % 360F;

        double rads = Math.toRadians(this.rotation);
        this.sinRot = (float) Math.sin(rads);
        this.cosRot = (float) Math.cos(rads);
    }

    @Override
    public float getTranslationX() {
        return this.translationX;
    }

    @Override
    public float getTranslationY() {
        return this.translationY;
    }

    @Override
    public float getScaleX() {
        return this.scaleX;
    }

    @Override
    public float getScaleY() {
        return this.scaleY;
    }

    @Override
    public boolean isMirroredHor() {
        return this.mirroredHor;
    }

    @Override
    public boolean isMirroredVert() {
        return this.mirroredVert;
    }

    @Override
    public IShaderProgram getProgram() {
        return this.program;
    }

    @Override
    public void setProgram(IShaderProgram program) {
        if (program == null) {
            program = this.defaultProgram;
        }

        if ((this.program == null) != (program == null) || this.program.getId() != program.getId()) {
            if (this.isDrawing) {
                this.flush();
            }

            this.program = program;
        }
    }

    @Override
    public ITexture getTexture() {
        return this.texture;
    }

    @Override
    public void setTexture(ITexture texture) {
        if ((this.texture == null) != (texture == null) || this.texture.getId() != texture.getId()) {
            if (this.isDrawing) {
                this.flush();
            }

            this.texture = texture;

            if (this.texture != null) {
                this.texture.bind();
            }
        }
    }

    @Override
    public void dispose() {
        MemoryUtil.memFree(this.vertices);
        this.vbo.dispose();

        this.simpleProgram.dispose();
        this.simpleFont.dispose();
    }

    @Override
    public int getFlushes() {
        return this.lastFlushes;
    }

    @Override
    public void renderSlotInGui(IGameInstance game, IAssetManager manager, ItemInstance slot, float x, float y, float scale, boolean hovered, boolean canPlaceInto) {
        ITexture texture = manager.getTexture(SLOT_NAME);

        int color = game.getSettings().guiColor;

        if (!canPlaceInto) {
            color = Colors.multiply(color, 0.5F);
        } else if (!hovered) {
            color = Colors.multiply(color, 0.75F);
        }

        texture.draw(x, y, texture.getRenderWidth() * scale, texture.getRenderHeight() * scale, color);

        if (slot != null) {
            this.renderItemInGui(game, manager, slot, x + 3F * scale, y + 3F * scale, scale, Colors.WHITE);
        }
    }


    @Override
    public void renderItemInGui(IGameInstance game, IAssetManager manager, ItemInstance slot, float x, float y, float scale, int color) {
        this.renderItemInGui(game, manager, slot, x, y, scale, color, true, true);
    }

    @Override
    public void renderItemInGui(IGameInstance game, IAssetManager manager, ItemInstance slot, float x, float y, float scale, int color, boolean displayAmount, boolean displayDurability) {
        Item item = slot.getItem();

        if (displayDurability) {
            if (item.useMetaAsDurability()) {
                int meta = slot.getMeta();
                int max = item.getHighestPossibleMeta();
                float percentage = 1F - meta / (float) max;

                float r = 1F - percentage;
                float g = percentage * 0.75F;

                this.addFilledRect(x - 2F * scale, y + 12F * scale - 14F * scale * percentage, 14F * scale, 14F * scale * percentage, Colors.rgb(r, g, 0F, 0.45F));
            }
        }

        IItemRenderer renderer = item.getRenderer();
        if (renderer != null) {
            renderer.render(game, manager, this, item, slot, x, y, 10F * scale, color);
        }

        if (displayAmount) {
            if (slot.getAmount() != 1) {
                manager.getFont().drawStringFromRight(x + 12F * scale, y + 6F * scale, String.valueOf(slot.getAmount()), 0.3F * scale);
            }
        }
    }

    @Override
    public void describeItem(IGameInstance game, IAssetManager manager, ItemInstance instance) {
        boolean advanced = Settings.KEY_ADVANCED_INFO.isDown();

        List<String> desc = new ArrayList<>();
        instance.getItem().describeItem(manager, instance, desc, advanced);

        if (this.isItemInfoDebug()) {
            desc.add("");
            desc.add(FormattingCode.GRAY + "Name: " + instance.getItem().getName());
            desc.add(FormattingCode.GRAY + "Meta: " + instance.getMeta());

            JsonObject json = new JsonObject();
            try {
                RockBottomAPI.getApiHandler().writeDataSet(json, instance.getAdditionalData());
            } catch (Exception ignored) {
            }
            desc.add(FormattingCode.GRAY + "Data: " + Util.GSON.toJson(json));
            desc.add(FormattingCode.GRAY + "Max Amount: " + instance.getMaxAmount());
            desc.add(FormattingCode.GRAY + "Resources: " + RockBottomAPI.getResourceRegistry().getNames(new ResInfo(instance)));
        }

        if (RockBottomAPI.getEventHandler().fireEvent(new TooltipEvent(instance, game, manager, this, desc)) != EventResult.CANCELLED) {
            this.drawHoverInfoAtMouse(game, manager, true, 125, desc);
        }
    }

    @Override
    public void drawHoverInfoAtMouse(IGameInstance game, IAssetManager manager, boolean firstLineOffset, int maxLength, String... text) {
        this.drawHoverInfoAtMouse(game, manager, firstLineOffset, maxLength, Arrays.asList(text));
    }

    @Override
    public void drawHoverInfoAtMouse(IGameInstance game, IAssetManager manager, boolean firstLineOffset, int maxLength, List<String> text) {
        float mouseX = this.getMouseInGuiX();
        float mouseY = this.getMouseInGuiY();

        this.drawHoverInfo(game, manager, mouseX + 18F / this.getGuiScale(), mouseY + 18F / this.getGuiScale(), 0.25F, firstLineOffset, false, maxLength, text);
    }

    @Override
    public void drawHoverInfo(IGameInstance game, IAssetManager manager, float x, float y, float scale, boolean firstLineOffset, boolean canLeaveScreen, int maxLength, List<String> text) {
        IFont font = manager.getFont();

        float boxWidth = 0F;
        float boxHeight = 0F;

        if (maxLength > 0) {
            text = font.splitTextToLength(maxLength, scale, true, text);
        }

        for (String s : text) {
            float length = font.getWidth(s, scale);
            if (length > boxWidth) {
                boxWidth = length;
            }

            if (firstLineOffset && boxHeight == 0F && text.size() > 1) {
                boxHeight += 3F;
            }
            boxHeight += font.getHeight(scale);
        }

        if (boxWidth > 0F && boxHeight > 0F) {
            boxWidth += 4F;
            boxHeight += 4F;

            if (!canLeaveScreen) {
                x = Math.max(0, Math.min(x, this.getWidthInGui() - boxWidth));
                y = Math.max(0, Math.min(y, this.getHeightInGui() - boxHeight));
            }

            this.addFilledRect(x, y, boxWidth, boxHeight, Gui.HOVER_INFO_BACKGROUND);
            this.addEmptyRect(x, y, boxWidth, boxHeight, Colors.BLACK);

            float yOffset = 0F;
            for (String s : text) {
                font.drawString(x + 2F, y + 2F + yOffset, s, scale);

                if (firstLineOffset && yOffset == 0F) {
                    yOffset += 3F;
                }
                yOffset += font.getHeight(scale);
            }
        }
    }

    @Override
    public void addEmptyRect(float x, float y, float width, float height, int color) {
        this.addEmptyRect(x, y, width, height, 1F, color);
    }

    @Override
    public void addEmptyRect(float x, float y, float width, float height, float lineWidth, int color) {
        this.addFilledRect(x, y, width - lineWidth, lineWidth, color);
        this.addFilledRect(x + lineWidth, y + height - lineWidth, width - lineWidth, lineWidth, color);
        this.addFilledRect(x + width - lineWidth, y, lineWidth, height - lineWidth, color);
        this.addFilledRect(x, y + lineWidth, lineWidth, height - lineWidth, color);
    }

    @Override
    public void addFilledRect(float x, float y, float width, float height, int color) {
        this.addTriangle(x, y, x, y + height, x + width, y, color, color, color, 0F, 0F, 0F, 0F, 0F, 0F);
        this.addTriangle(x + width, y, x, y + height, x + width, y + height, color, color, color, 0F, 0F, 0F, 0F, 0F, 0F);
    }

    @Override
    public void activateTextureBank(TextureBank bank) {
        Texture.activateTextureBank(bank);
    }

    @Override
    public void unbindTexture() {
        Texture.unbindCurrentBank();
    }

    @Override
    public void unbindAllTextures() {
        Texture.unbindAllBanks();
    }

    @Override
    public void unbindVAO() {
        VertexArrayObject.unbindAll();
    }

    @Override
    public void unbindVBO() {
        VertexBufferObject.unbindAll();
    }

    @Override
    public void unbindShaderProgram() {
        ShaderProgram.unbindAll();
    }

    @Override
    public IVAO createVAO() {
        return new VertexArrayObject();
    }

    @Override
    public IVBO createVBO(boolean isStatic) {
        return new VertexBufferObject(isStatic);
    }

    @Override
    public void calcScales() {
        RockBottomAPI.logger().config("Calculating render scales");

        IGameInstance game = RockBottomAPI.getGame();
        float width = game.getWidth();
        float height = game.getHeight();

        this.displayRatio = Math.min(width / 16F, height / 9F);

        this.guiScale = (this.getDisplayRatio() / 20F) * this.game.getSettings().guiScale;
        this.guiWidth = width / this.guiScale;
        this.guiHeight = height / this.guiScale;

        this.worldScale = this.getDisplayRatio() * this.game.getSettings().renderScale;
        this.worldWidth = width / this.worldScale;
        this.worldHeight = height / this.worldScale;

        RockBottomAPI.logger().config("Successfully calculated render scales");
    }

    @Override
    public float getDisplayRatio() {
        return this.displayRatio;
    }

    @Override
    public float getGuiScale() {
        return this.guiScale;
    }

    @Override
    public float getWorldScale() {
        return this.worldScale;
    }

    @Override
    public float getWidthInWorld() {
        return this.worldWidth;
    }

    @Override
    public float getHeightInWorld() {
        return this.worldHeight;
    }

    @Override
    public float getWidthInGui() {
        return this.guiWidth;
    }

    @Override
    public float getHeightInGui() {
        return this.guiHeight;
    }

    @Override
    public float getMouseInGuiX() {
        return (float) this.game.getInput().getMouseX() / this.getGuiScale();
    }

    @Override
    public float getMouseInGuiY() {
        return (float) this.game.getInput().getMouseY() / this.getGuiScale();
    }

    @Override
    public boolean isDebug() {
        return this.isDebug;
    }

    @Override
    public boolean isItemInfoDebug() {
        return this.isItemInfoDebug;
    }

    @Override
    public boolean isChunkBorderDebug() {
        return this.isChunkBorderDebug;
    }

    @Override
    public boolean isGuiDebug() {
        return this.isGuiDebug;
    }

    @Override
    public boolean isLineDebug() {
        return this.isLineDebug;
    }

    @Override
    public boolean isBiomeDebug() {
        return this.isBiomeDebug;
    }

    @Override
    public boolean isHeightDebug() {
        return this.isHeightDebug;
    }

    @Override
    public boolean isBoundBoxDebug() {
        return this.isBoundBoxDebug;
    }

    @Override
    public boolean isCullingDebug() {
        return this.isCullingDebug;
    }

    @Override
    public double getMousedTileX() {
        double mouseX = this.game.getInput().getMouseX();
        double worldAtScreenX = this.cameraX - this.getWidthInWorld() / 2;
        return worldAtScreenX + mouseX / (double) this.getWorldScale();
    }

    @Override
    public double getMousedTileY() {
        double mouseY = this.game.getInput().getMouseY();
        double worldAtScreenY = -this.cameraY - this.getHeightInWorld() / 2;
        return -(worldAtScreenY + mouseY / (double) this.getWorldScale()) + 1;
    }

    @Override
    public void backgroundColor(int color) {
        if (this.backgroundColor != color) {
            GL11.glClearColor(Colors.getR(color), Colors.getG(color), Colors.getB(color), Colors.getA(color));
            this.backgroundColor = color;
        }
    }

    private int combineLight(int[] light, int corner, int filter) {
        if (light != null) {
            return Colors.multiply(light[corner], filter);
        } else {
            return filter;
        }
    }

    @Override
    public FloatBuffer getVertices() {
        return this.vertices;
    }

    @Override
    public int getVertexAmount() {
        return this.vertexAmount;
    }

    @Override
    public double getCameraX() {
        return this.cameraX;
    }

    @Override
    public double getCameraY() {
        return this.cameraY;
    }
}
