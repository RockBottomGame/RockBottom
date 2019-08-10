package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.toast.IToast;
import de.ellpeck.rockbottom.api.toast.IToaster;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Toaster implements IToaster {

    private final Map<IToast, ToastPosition> toasts = new LinkedHashMap<>();

    public void update() {
        Iterator<ToastPosition> iterator = this.toasts.values().iterator();
        while (iterator.hasNext()) {
            ToastPosition pos = iterator.next();
            pos.timer--;

            if (pos.timer <= 0) {
                iterator.remove();
            }
        }
    }

    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        for (Entry<IToast, ToastPosition> entry : this.toasts.entrySet()) {
            IToast toast = entry.getKey();
            ToastPosition pos = entry.getValue();

            float width = toast.getWidth();
            int time = toast.getDisplayTime();
            float movementTime = toast.getMovementTime();

            float x;
            if (pos.timer <= movementTime) {
                x = -width + ((pos.timer / movementTime) * (width + 2));
            } else if (pos.timer >= time - movementTime) {
                x = -width + (((time - pos.timer) / movementTime) * (width + 2));
            } else {
                x = 2;
            }

            toast.render(game, manager, g, x, pos.y);
        }
    }

    @Override
    public void displayToast(IToast toast) {
        float lowestY = 2;

        for (Map.Entry<IToast, ToastPosition> entry : this.toasts.entrySet()) {
            float y = entry.getValue().y + entry.getKey().getHeight() + 2;
            if (lowestY < y) {
                lowestY = y;
            }
        }

        this.toasts.put(toast, new ToastPosition(lowestY, toast.getDisplayTime()));
    }

    @Override
    public void cancelToast(IToast toast) {
        this.toasts.remove(toast);
    }

    @Override
    public void cancelAllToasts() {
        if (!this.toasts.isEmpty()) {
            this.toasts.clear();
        }
    }

    private static class ToastPosition {

        private final float y;
        protected int timer;

        public ToastPosition(float y, int timer) {
            this.y = y;
            this.timer = timer;
        }
    }
}
