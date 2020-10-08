package engine.hud.animations;

import engine.hud.components.ContentComponent;

public interface AnimationAction<T> {

    void execute(T nextValue, ContentComponent component);

    T getProgress(ContentComponent component);
}
