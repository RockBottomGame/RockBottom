package de.ellpeck.game.data.set.part.num;

import de.ellpeck.game.data.set.part.BasicDataPart;

public class PartInt extends BasicDataPart<Integer>{

    public PartInt(String name){
        super(name);
    }

    public PartInt(String name, Integer data){
        super(name, data);
    }

    @Override
    public String write(){
        return this.data.toString();
    }

    @Override
    public void read(String data){
        this.data = Integer.parseInt(data);
    }
}
