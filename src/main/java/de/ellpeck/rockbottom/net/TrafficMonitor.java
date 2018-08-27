package de.ellpeck.rockbottom.net;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;

public class TrafficMonitor extends ChannelTrafficShapingHandler {

    public TrafficMonitor() {
        super(30 * 1000);
    }

    @Override
    protected void doAccounting(TrafficCounter counter) {
        RockBottomAPI.logger().config("Traffic information for the last 30 seconds: "
                + (counter.lastWrittenBytes() / 1000) + " (" + (counter.lastWriteThroughput() / 1000) + " per second) kB written, "
                + (counter.lastReadBytes() / 1000) + " (" + (counter.lastReadThroughput() / 1000) + " per second) kB read");
    }
}
