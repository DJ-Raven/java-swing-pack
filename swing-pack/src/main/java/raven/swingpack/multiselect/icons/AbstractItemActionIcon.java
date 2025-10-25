package raven.swingpack.multiselect.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;

/**
 * @author Raven
 */
public abstract class AbstractItemActionIcon implements ItemActionIcon {

    private final int width;
    private final int height;
    private Color color;

    private Color pressedColor;
    private Color hoverColor;
    private Color defaultColor;

    public AbstractItemActionIcon(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public final void paintIcon(Component com, Graphics g, int x, int y, boolean pressed, boolean focus) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            FlatUIUtils.setRenderingHints(g2);
            g2.translate(x, y);
            UIScale.scaleGraphics(g2);

            g2.setColor(getColor(pressed, focus));
            paintIcon(com, g2, pressed, focus);
        } finally {
            g2.dispose();
        }
    }

    protected abstract void paintIcon(Component com, Graphics2D g, boolean pressed, boolean focus);

    @Override
    public Color getColor() {
        return color;
    }

    public Color getColor(boolean pressed, boolean focus) {
        if (getColor() != null) {
            return getColor();
        }

        if (pressed) {
            return pressedColor;
        } else if (focus) {
            return hoverColor;
        }
        return defaultColor;
    }

    @Override
    public void updateUI() {
        pressedColor = UIManager.getColor("SearchField.clearIconPressedColor");
        hoverColor = UIManager.getColor("SearchField.clearIconHoverColor");
        defaultColor = UIManager.getColor("SearchField.clearIconColor");
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getWidth() {
        return UIScale.scale(width);
    }

    @Override
    public int getHeight() {
        return UIScale.scale(height);
    }
}
