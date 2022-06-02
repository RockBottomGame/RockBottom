package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.AbstractWorld;
import io.netty.buffer.ByteBuf;

public class ParticlesPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("particles");

    private int x;
    private int y;
    private int type;
    private int[] args;

    public ParticlesPacket(int x, int y, int type, int... args) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.args = args;
    }

    public ParticlesPacket() {

    }

    public static ParticlesPacket tile(AbstractWorld world, int x, int y, TileState state) {
        return new ParticlesPacket(x, y, 0, world.getIdForState(state));
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.type);

        buf.writeInt(this.args.length);
        for (int arg : this.args) {
            buf.writeInt(arg);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.type = buf.readInt();

        this.args = new int[buf.readInt()];
        for (int i = 0; i < this.args.length; i++) {
            this.args[i] = buf.readInt();
        }
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        if (game.getWorld() != null) {
            if (this.type == 0) {
                TileState tile = game.getWorld().getStateForId(this.args[0]);
                game.getParticleManager().addTileParticles(game.getWorld(), this.x, this.y, tile);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
