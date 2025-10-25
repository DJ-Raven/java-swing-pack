package raven.swingpack.multiselect.icons;

import raven.swingpack.JMultiSelectComboBox;

import java.awt.*;

/**
 * @author Raven
 */
public interface ItemActionIcon {

    void paintIcon(Component com, Graphics g, int x, int y, boolean pressed, boolean focus);

    Color getColor();

    void setColor(Color color);

    void updateUI();

    int getWidth();

    int getHeight();

    Rectangle getIconRectangle(JMultiSelectComboBox<?> multiSelect, Component com, int width, int height);
}
