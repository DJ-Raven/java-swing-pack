package raven.swingpack.multiselect.icons;

import com.formdev.flatlaf.icons.FlatCheckBoxMenuItemIcon;

import java.awt.*;

/**
 * @author Raven
 */
public class CheckmarkIcon extends FlatCheckBoxMenuItemIcon {

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    private boolean selected;
    private boolean hasFocus;

    @Override
    protected void paintIcon(Component c, Graphics2D g2) {
        if (isSelected()) {
            g2.setColor(getCheckmarkColor(c));
            paintCheckmark(g2);
        }
    }

    @Override
    protected Color getCheckmarkColor(Component c) {
        if (isHasFocus()) {
            return selectionForeground;
        }
        return checkmarkColor;
    }
}
