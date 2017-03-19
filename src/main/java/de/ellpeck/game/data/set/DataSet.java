package de.ellpeck.game.data.set;

import de.ellpeck.game.Game;
import de.ellpeck.game.Main;
import de.ellpeck.game.data.DataManager;
import de.ellpeck.game.data.set.part.DataPart;
import org.newdawn.slick.util.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataSet{

    private final Map<String, DataPart> data = new HashMap<>();

    public void put(DataPart part){
        this.data.put(part.getName(), part);
    }

    public <T> T getDataInPart(String key){
        DataPart part = this.data.get(key);

        if(part != null){
            return (T)part.get();
        }
        else{
            return null;
        }
    }

    public void write(File file){
        try{
            if(!file.exists()){
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

            for(DataPart part : this.data.values()){
                String s = writeDataPart(part);
                if(s != null){
                    writer.write(s);
                    writer.newLine();
                }
            }

            writer.flush();
            writer.close();
        }
        catch(IOException e){
            Main.doExceptionInfo(Game.get(), e);
        }
    }

    public void read(File file){
        this.data.clear();

        try{
            if(file.exists()){
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

                String line = reader.readLine();
                while(line != null){
                    if(!line.isEmpty()){
                        DataPart part = readDataPart(line);
                        if(part != null){
                            this.data.put(part.getName(), part);
                        }
                    }

                    line = reader.readLine();
                }

                reader.close();
            }
        }
        catch(IOException e){
            Main.doExceptionInfo(Game.get(), e);
        }
    }

    @Override
    public String toString(){
        return this.data.toString();
    }

    public Map<String, DataPart> getData(){
        return this.data;
    }

    public boolean isEmpty(){
        return this.data.isEmpty();
    }

    public static String writeDataPart(DataPart part){
        String name = part.getName();

        int id = DataManager.PART_REGISTRY.getId(part.getClass());
        if(id >= 0){
            return "[{"+id+","+name+"}@{"+part.write()+"}]";
        }
        else{
            Log.error("Cannot write DataSet property "+part+" with name "+part.getName()+" because it is not registered!");
            return null;
        }
    }

    public static DataPart readDataPart(String data){
        try{
            String totalPart = getStringInbetween(data, "[", "]");
            String[] totalPartSplit = totalPart.split("@", 2);
            String actualData = getStringInbetween(totalPartSplit[1], "{", "}");

            if(!actualData.isEmpty()){
                String idAndName = getStringInbetween(totalPartSplit[0], "{", "}");
                String[] idNameSplit = idAndName.split(",");

                Class<? extends DataPart> partClass = DataManager.PART_REGISTRY.byId(Integer.parseInt(idNameSplit[0]));
                DataPart part = partClass.getConstructor(String.class).newInstance(idNameSplit[1]);
                part.read(actualData);

                return part;
            }
        }
        catch(Exception e){
            Log.error("Cannot read DataSet property with data "+data+"!", e);
        }
        return null;
    }

    private static String getStringInbetween(String input, String left, String right){
        String fromLeft = input.substring(input.indexOf(left)+1);
        return fromLeft.substring(0, input.lastIndexOf(right)-1);
    }
}
