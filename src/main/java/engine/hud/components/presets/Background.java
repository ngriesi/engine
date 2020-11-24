package engine.hud.components.presets;

import engine.hud.color.ColorScheme;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.constraints.positionConstraints.RelativeToWindowPosition;
import engine.hud.constraints.sizeConstraints.RelativeToWindowSize;

/**
 * class used to create a background with a color scheme
 * by setting a quad component to the full window size
 */
@SuppressWarnings("unused")
public class Background extends QuadComponent {

    /**
     * creates a background with a color scheme
     *
     * @param color color scheme of the background
     */
    public Background(ColorScheme color) {
        super();
        this.setxPositionConstraint(new RelativeToWindowPosition(0.5f));
        this.setyPositionConstraint(new RelativeToWindowPosition(0.5f));
        this.setHeightConstraint(new RelativeToWindowSize(1));
        this.setWidthConstraint(new RelativeToWindowSize(1));

        this.setColorScheme(color);

        this.setUseColorShade(true);
    }
}
