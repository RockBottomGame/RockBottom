package de.ellpeck.rockbottom.net;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;

import java.util.logging.Logger;

public class TrafficMonitor extends ChannelTrafficShapingHandler {

    public TrafficMonitor() {
        super(30 * 1000);
    }

    @Override
    protected void doAccounting(TrafficCounter counter) {
        Logger log = RockBottomAPI.logger();
        log.info("Traffic information for the last 30 seconds:");
        log.info(counter.lastWrittenBytes() + " written, " + counter.lastReadBytes() + " read in total");
        log.info(counter.lastWriteThroughput() + " written, " + counter.lastReadThroughput() + " read per second");
    }
}
