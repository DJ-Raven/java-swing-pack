package raven.swingpack.util;

import javax.swing.*;
import java.awt.*;

/**
 * @author Raven
 */
public class SwingPackUtils {

    public static String VISUAL_PADDING_PROPERTY = "visualPadding";

    public static void applyVisualPadding(Component c, Rectangle rec) {
        if (c instanceof JComponent) {
            Object padding = ((JComponent) c).getClientProperty(VISUAL_PADDING_PROPERTY);
            if (padding instanceof Insets) {
                Insets insets = (Insets) padding;
                rec.x -= insets.left;
                rec.y -= insets.top;
                rec.width += (insets.left + insets.right);
                rec.height += (insets.top + insets.bottom);
            }
        }
    }
}
