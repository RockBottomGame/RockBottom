package de.ellpeck.game.data.set.part;

import de.ellpeck.game.data.set.DataSet;

public class PartDataSet extends BasicDataPart<DataSet>{

    public PartDataSet(String name){
        super(name);
    }

    public PartDataSet(String name, DataSet data){
        super(name, data);
    }

    @Override
    public String write(){
        if(!this.data.isEmpty()){
            String s = "";
            for(DataPart part : this.data.getData().values()){
                s += DataSet.writeDataPart(part)+"~";
            }
            return s.substring(0, s.length()-1);
        }
        else{
            return "";
        }
    }

    @Override
    public void read(String data){
        this.data = new DataSet();

        if(!data.isEmpty()){
            String[] split = data.split("~");
            for(String s : split){
                DataPart part = DataSet.readDataPart(s);
                if(part != null){
                    this.data.put(part);
                }
            }
        }
    }
}
