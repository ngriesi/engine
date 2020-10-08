package engine.hud.keys;

import engine.general.Window;
import engine.hud.actions.KeyAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KeyListener {

    /** delay before the key spam starts (input frames) */
    @SuppressWarnings("WeakerAccess")
    public static final int START_PAUSE = 0;

    /** interval the key spam uses (input frames) */
    @SuppressWarnings("WeakerAccess")
    public static final int PRESSED_INTERVAL = 0;

    /** last used key */
    private HashMap<Integer,KeyInfo> lastUsed;

    /** determines if the spam of keys should be enabled */
    @SuppressWarnings("FieldCanBeLocal")
    private boolean useKeySpam = true;

    @SuppressWarnings("unused")
    private KeyAction onKeyPressedAction;

    public KeyListener() {
        lastUsed = new HashMap<>();
    }

    /**
     * handles the key input
     *
     * @param window to get the last pressed key
     */
    public void handleKeyInput(Window window) {
        List<Integer> keys = window.getLastPressed();
        int key;

        List<Integer> remove = new ArrayList<>();

        for(Integer k : lastUsed.keySet()) {
            if(!keys.contains(k)) {
                remove.add(k);
            }
        }

        for(Integer k : remove) {
            lastUsed.remove(k);
        }

        for (Integer integer : keys) {
            key = integer;


            if (key != 0) {

                KeyInfo info = lastUsed.get(key);

                if (info == null || (useKeySpam && (!info.pressed && info.timer > START_PAUSE)) || (useKeySpam && (info.pressed && info.timer > PRESSED_INTERVAL))) {
                    if (info == null) {
                        lastUsed.put(key, new KeyInfo(key, 0, false));
                    } else {
                        info.timer++;
                    }

                    keyPressedAction(key);

                } else {
                    info.timer++;
                    if (info.timer == START_PAUSE) {
                        info.pressed = true;
                    }
                }
            }
        }
    }

    protected void keyPressedAction(int keyCode) {
        if(onKeyPressedAction != null) {
            onKeyPressedAction.execute(keyCode);
        }
    }

    public void setKeyAction(KeyAction keyAction) {
        this.onKeyPressedAction = keyAction;
    }

    private static class KeyInfo {

        private int key;
        private int timer;
        private boolean pressed;

        public KeyInfo(int key, int timer, boolean pressed) {
            this.key = key;
            this.timer = timer;
            this.pressed = pressed;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public int getTimer() {
            return timer;
        }

        public void setTimer(int timer) {
            this.timer = timer;
        }

        public boolean isPressed() {
            return pressed;
        }

        public void setPressed(boolean pressed) {
            this.pressed = pressed;
        }
    }
}
