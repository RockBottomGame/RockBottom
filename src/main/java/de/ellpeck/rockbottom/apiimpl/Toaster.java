package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.toast.IToaster;
import de.ellpeck.rockbottom.api.toast.Toast;
import de.ellpeck.rockbottom.api.util.Counter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Toaster implements IToaster{

    private final Map<Toast, Counter> toasts = new LinkedHashMap<>();

    public void update(){
        Iterator<Entry<Toast, Counter>> iterator = this.toasts.entrySet().iterator();
        while(iterator.hasNext()){
            Entry<Toast, Counter> entry = iterator.next();

            Counter timer = entry.getValue();
            timer.add(-1);
            if(timer.get() <= 0){
                iterator.remove();
            }
        }
    }

    public void render(IGameInstance game, IAssetManager manager, IRenderer g){
        float y = 2;
        for(Entry<Toast, Counter> entry : this.toasts.entrySet()){
            Toast toast = entry.getKey();
            float width = toast.getWidth();
            Counter timer = entry.getValue();

            float x;
            if(timer.get() <= 10){
                x = -width+((timer.get()/10F)*(width+2));
            }
            else if(timer.get() >= toast.getDisplayTime()-10){
                x = -width+(((toast.getDisplayTime()-timer.get())/10F)*(width+2));
            }
            else{
                x = 2;
            }

            toast.render(game, manager, g, x, y);
            y += toast.getHeight()+2;
        }
    }

    @Override
    public void displayToast(Toast toast){
        this.toasts.put(toast, new Counter(toast.getDisplayTime()));
    }

    @Override
    public void cancelToast(Toast toast){
        this.toasts.remove(toast);
    }

    @Override
    public void cancelAllToasts(){
        if(!this.toasts.isEmpty()){
            this.toasts.clear();
        }
    }
}
