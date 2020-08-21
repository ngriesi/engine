package engine.hud.animations;

import engine.hud.components.Component;

public interface AnimationAction<T> {

    void execute(T nextValue, Component component);

    T getProgress(Component component);
}
