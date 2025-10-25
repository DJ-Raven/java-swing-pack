package raven.swingpack.pagination.icons;

import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * @author Raven
 */
public class PageArrowIcon extends FlatAbstractIcon {

    private final boolean isLeft;

    public PageArrowIcon(boolean isLeft) {
        super(6, 10, null);
        this.isLeft = isLeft;
    }

    @Override
    protected void paintIcon(Component com, Graphics2D g) {
        if (isLeft) {
            g.rotate(Math.toRadians(180), width / 2f, height / 2f);
        }
        g.setColor(getArrowColor(com));
        Path2D path = FlatUIUtils.createPath(false, 1, 1, 5, 5, 1, 9);
        g.setStroke(new BasicStroke(1f));
        g.draw(path);
    }

    protected Color getArrowColor(Component com) {
        return com.getForeground();
    }
}
