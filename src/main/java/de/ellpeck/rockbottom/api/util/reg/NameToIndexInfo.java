package de.ellpeck.rockbottom.api.util.reg;

import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.settings.IPropSettings;
import de.ellpeck.rockbottom.api.net.NetUtil;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.util.Map;
import java.util.Properties;

public class NameToIndexInfo implements IPropSettings{

    private final IndexRegistry<String> reg;

    private boolean needsSave;
    private final File file;

    public NameToIndexInfo(String name, File file, int max){
        this.file = file;
        this.reg = new IndexRegistry<>(name, max);
    }

    public <T> void populate(NameRegistry<T> registry){
        for(Map.Entry<String, T> entry : registry.map.entrySet()){
            String key = entry.getKey();

            if(this.getId(key) < 0){
                this.reg.register(this.reg.getNextFreeId(), key);
                this.needsSave = true;
            }
        }
    }

    public int getId(String name){
        return this.reg.getId(name);
    }

    public String get(int id){
        return this.reg.get(id);
    }

    public boolean needsSave(){
        return this.needsSave;
    }

    @Override
    public void load(Properties props){
        this.reg.map.clear();

        for(String key : props.stringPropertyNames()){
            int index = Integer.parseInt(key);
            this.reg.map.put(index, props.getProperty(key));
        }
    }

    public void fromBuffer(ByteBuf buf){
        this.reg.map.clear();

        int amount = buf.readInt();
        for(int i = 0; i < amount; i++){
            this.reg.map.put(buf.readInt(), NetUtil.readStringFromBuffer(buf));
        }
    }

    @Override
    public void save(Properties props){
        for(Map.Entry<Integer, String> entry : this.reg.map.entrySet()){
            props.setProperty(entry.getKey().toString(), entry.getValue());
        }

        this.needsSave = false;
    }

    public void toBuffer(ByteBuf buf){
        buf.writeInt(this.reg.getSize());

        for(Map.Entry<Integer, String> entry : this.reg.map.entrySet()){
            buf.writeInt(entry.getKey());
            NetUtil.writeStringToBuffer(entry.getValue(), buf);
        }
    }

    @Override
    public File getFile(IDataManager manager){
        return this.file;
    }

    @Override
    public String getName(){
        return "Name to index info "+this.reg;
    }
}
