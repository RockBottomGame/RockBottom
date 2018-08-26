package de.ellpeck.rockbottom.net.client;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.GuiInformation;
import de.ellpeck.rockbottom.gui.menu.GuiMainMenu;
import de.ellpeck.rockbottom.log.Logging;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;

public class ClientNetworkHandler extends SimpleChannelInboundHandler<IPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket packet) {
        RockBottomAPI.getGame().enqueueAction((game, context) -> {
            try {
                packet.handle(game, context);
            } catch (Exception e) {
                RockBottomAPI.logger().log(Level.SEVERE, "There was an error handling a packet on the client, closing the connection", e);
                this.notifyAndClose(context, e);
            }
        }, ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Logging.nettyLogger.log(Level.SEVERE, "The client network handler caught an exception", cause);
        this.notifyAndClose(ctx, cause);
    }

    private void notifyAndClose(ChannelHandlerContext ctx, Throwable cause) {
        IGameInstance game = RockBottomAPI.getGame();
        ctx.disconnect();
        game.quitWorld();

        String message = cause.getMessage();
        if (message == null) {
            message = "Unspecified reason";
        }
        game.getGuiManager().openGui(new GuiInformation(new GuiMainMenu(), 0.5F, game.getAssetManager().localize(ResourceName.intern("info.reject.exception"), message)));
    }
}
