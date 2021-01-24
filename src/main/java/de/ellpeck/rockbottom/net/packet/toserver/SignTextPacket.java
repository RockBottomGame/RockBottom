package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.tile.entity.SignTileEntity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class SignTextPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("sign_text");

    private int x;
    private int y;
    private String[] text;

    public SignTextPacket(int x, int y, String[] text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public SignTextPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);

        for (int i = 0; i < this.text.length; i++) {
            NetUtil.writeStringToBuffer(this.text[i], buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();

        this.text = new String[SignTileEntity.TEXT_AMOUNT];
        for (int i = 0; i < this.text.length; i++) {
            this.text[i] = NetUtil.readStringFromBuffer(buf);
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            SignTileEntity tile = world.getTileEntity(this.x, this.y, SignTileEntity.class);
            if (tile != null) {
                System.arraycopy(this.text, 0, tile.text, 0, SignTileEntity.TEXT_AMOUNT);
                tile.sendToClients();
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
