package raven.swingpack.multiselect.icons;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import raven.swingpack.JMultiSelectComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/**
 * @author Raven
 */
public class DefaultRemovableIcon extends AbstractItemActionIcon {

    public DefaultRemovableIcon() {
        super(16, 16);
    }

    @Override
    protected void paintIcon(Component com, Graphics2D g, boolean pressed, boolean focus) {
        Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
        path.append(new Ellipse2D.Float(1.75f, 1.75f, 12.5f, 12.5f), false);
        path.append(FlatUIUtils.createPath(4.5, 5.5, 5.5, 4.5, 8, 7, 10.5, 4.5, 11.5, 5.5, 9, 8, 11.5, 10.5, 10.5, 11.5, 8, 9, 5.5, 11.5, 4.5, 10.5, 7, 8), false);
        g.fill(path);
    }

    @Override
    public Rectangle getIconRectangle(JMultiSelectComboBox<?> multiSelect, Component com, int width, int height) {
        int iw = getWidth();
        int ih = getHeight();
        boolean ltr = multiSelect.getComponentOrientation().isLeftToRight();
        Insets insets;
        if (com instanceof JComponent) {
            insets = ((JComponent) com).getInsets();
        } else {
            insets = new Insets(0, 0, 0, 0);
        }
        int gap = UIScale.scale(multiSelect.getItemRemovableTextGap() + getExtraGap());
        int w = width - (insets.left + insets.right);
        int h = height - (insets.top + insets.bottom);
        int x = ltr ? insets.left + w + gap : (insets.left - iw - gap);
        int y = insets.top + (h - ih) / 2;
        return new Rectangle(x, y, iw, ih);
    }

    protected int getExtraGap() {
        // extra gap apply without effect component size
        return 3;
    }
}
