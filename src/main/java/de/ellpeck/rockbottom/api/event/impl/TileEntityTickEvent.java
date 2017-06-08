package de.ellpeck.rockbottom.api.event.impl;

import de.ellpeck.rockbottom.api.event.Event;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;

public class TileEntityTickEvent extends Event{

    public final TileEntity tileEntity;

    public TileEntityTickEvent(TileEntity tileEntity){
        this.tileEntity = tileEntity;
    }
}
