package de.ellpeck.game.data.set.part.num;

import de.ellpeck.game.data.set.part.BasicDataPart;

public class PartDouble extends BasicDataPart<Double>{

    public PartDouble(String name){
        super(name);
    }

    public PartDouble(String name, Double data){
        super(name, data);
    }

    @Override
    public String write(){
        return this.data.toString();
    }

    @Override
    public void read(String data){
        this.data = Double.parseDouble(data);
    }
}
