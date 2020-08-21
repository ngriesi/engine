package engine.hud.components.presets;

import engine.hud.color.ColorScheme;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.constraints.positionConstraints.RelativeToWindowPosition;
import engine.hud.constraints.sizeConstraints.RelativeToWindowSize;

public class Background extends QuadComponent {

    public Background(ColorScheme color) {
        this.setxPositionConstraint(new RelativeToWindowPosition(0.5f));
        this.setyPositionConstraint(new RelativeToWindowPosition(0.5f));
        this.setHeightConstraint(new RelativeToWindowSize(1));
        this.setWidthConstraint(new RelativeToWindowSize(1));

        this.setColorScheme(color);

        this.setUseColorShade(true);
    }
}
