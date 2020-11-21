package engine.hud.animations;

import engine.hud.Hud;
import engine.hud.color.ColorScheme;

/**
 * class contains a collection of static default animations for the default hud layout
 * which can be accessed by calling the <code>createForComponent</code> method of these
 * animations
 */
public class DefaultAnimations {

    public static ColorSchemeAnimation buttonEnteredAnimation;
    public static ColorSchemeAnimation buttonExitedAnimation;


    /**
     * constructor called by the hud
     *
     * @param hud of these animations
     */
    public DefaultAnimations(Hud hud) {
        buttonEnteredAnimation = new ColorSchemeAnimation(hud,10, ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_STANDARD),
                ColorScheme.getStandardColorScheme(ColorScheme.StandardColorSchemes.BUTTON_ENTERED));
        buttonExitedAnimation = (ColorSchemeAnimation) buttonEnteredAnimation.getInverted();
    }
}
