package engine.hud.animations;

import engine.hud.Hud;
import engine.hud.color.ColorScheme;

public class DefaultAnimations {

    public static Animation buttonEnteredAnimation;
    public static Animation buttonExitedAnimation;

    public DefaultAnimations(Hud hud) {
        buttonEnteredAnimation = new ColorSchemeAnimation(hud,10, ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_STANDARD),
                ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_ENTERED));
        buttonExitedAnimation = buttonEnteredAnimation.getInverted();
    }
}
