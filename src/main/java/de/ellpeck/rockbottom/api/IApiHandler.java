package de.ellpeck.rockbottom.api;

import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.set.part.DataPart;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlot;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.util.List;

public interface IApiHandler{

    void writeDataSet(DataSet set, File file);

    void readDataSet(DataSet set, File file);

    void writeSet(DataOutput stream, DataSet set) throws Exception;

    void readSet(DataInput stream, DataSet set) throws Exception;

    void writePart(DataOutput stream, DataPart part) throws Exception;

    DataPart readPart(DataInput stream) throws Exception;

    void doDefaultEntityUpdate(Entity entity);

    boolean doDefaultSlotMovement(IGameInstance game, int button, float x, float y, ComponentSlot slot);

    void renderSlotInGui(IGameInstance game, IAssetManager manager, Graphics g, ItemInstance slot, float x, float y, float scale);

    void renderItemInGui(IGameInstance game, IAssetManager manager, Graphics g, ItemInstance slot, float x, float y, float scale, Color color);

    void describeItem(IGameInstance game, IAssetManager manager, Graphics g, ItemInstance instance);

    void drawHoverInfoAtMouse(IGameInstance game, IAssetManager manager, Graphics g, boolean firstLineOffset, int maxLength, String... text);

    void drawHoverInfoAtMouse(IGameInstance game, IAssetManager manager, Graphics g, boolean firstLineOffset, int maxLength, List<String> text);

    void drawHoverInfo(IGameInstance game, IAssetManager manager, Graphics g, float x, float y, float scale, boolean firstLineOffset, boolean canLeaveScreen, int maxLength, List<String> text);

    void drawScaledImage(Graphics g, Image image, float x, float y, float scale, Color color);
}
