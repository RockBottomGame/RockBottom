package de.ellpeck.game.data.set.part.num;

import de.ellpeck.game.data.set.part.BasicDataPart;

public class PartFloat extends BasicDataPart<Float>{

    public PartFloat(String name){
        super(name);
    }

    public PartFloat(String name, Float data){
        super(name, data);
    }

    @Override
    public String write(){
        return this.data.toString();
    }

    @Override
    public void read(String data){
        this.data = Float.parseFloat(data);
    }
}
