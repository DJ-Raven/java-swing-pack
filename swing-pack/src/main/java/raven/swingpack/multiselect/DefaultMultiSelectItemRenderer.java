package raven.swingpack.multiselect;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import raven.swingpack.JMultiSelectComboBox;
import raven.swingpack.multiselect.icons.ItemActionIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;

/**
 * @author Raven
 */
public class DefaultMultiSelectItemRenderer extends JLabel implements MultiSelectItemRenderer {

    protected Option option;

    public DefaultMultiSelectItemRenderer() {
    }

    @Override
    public void updateUI() {
        super.updateUI();
        initUI();
    }

    private void initUI() {
        if (option == null) {
            option = new Option();
        }

        if (option.removableIcon != null) {
            option.removableIcon.updateUI();
        }
        option.arc = UIManager.getInt("Button.arc");
        option.background = UIManager.getColor("Button.background");
        option.pressedBackground = UIManager.getColor("Button.pressedBackground");
        option.hoverBackground = UIManager.getColor("Button.hoverBackground");
    }

    @Override
    public Component getMultiSelectItemRendererComponent(JMultiSelectComboBox<?> multiSelect, Object value, boolean isPressed, boolean hasFocus, boolean removableFocus, int index) {
        option.multiSelect = multiSelect;
        option.isPressed = isPressed;
        option.hasFocus = hasFocus;
        option.removableFocus = removableFocus;
        option.item = value;
        option.index = index;
        option.removableIcon = multiSelect.getRemovableIcon();

        setForeground(multiSelect.getForeground());
        setFont(multiSelect.getFont());

        setText(Objects.toString(value, ""));
        putClientProperty(FlatClientProperties.STYLE, "border:" + getStyleInsets(multiSelect, index) + ";");
        return this;
    }

    private Rectangle getRemovableRectangle() {
        if (option.removableIcon == null) {
            return null;
        }
        return option.removableIcon.getIconRectangle(option.multiSelect, this, getWidth(), getHeight());
    }

    private String getStyleInsets(JMultiSelectComboBox<?> comboBox, int index) {
        Insets itemInsets = comboBox.getItemInsets();
        if (index == -1) {
            int bottom = (int) (option.overflowLineSize * 4) + itemInsets.left;
            return itemInsets.top + "," + bottom + "," + itemInsets.bottom + "," + itemInsets.right;
        }
        int iconGap = isRemovable() ? UIScale.unscale(option.removableIcon.getWidth() + option.multiSelect.getItemRemovableTextGap()) : 0;
        return itemInsets.top + "," + itemInsets.left + "," + itemInsets.bottom + "," + (itemInsets.right + iconGap);
    }

    private boolean isRemovable() {
        return option.multiSelect.isShowItemRemovableIcon() && option.multiSelect.isItemRemovable(option.item);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            FlatUIUtils.setRenderingHints(g2);
            int arc;
            if (option.multiSelect.getItemArc() == 999) {
                arc = getHeight();
            } else {
                arc = UIScale.scale(option.multiSelect.getItemArc() >= 0 ? option.multiSelect.getItemArc() : option.arc);
                if (arc > getHeight()) {
                    arc = getHeight();
                }
            }
            if (option.index != -1) {
                g2.setColor(getBackground(option.item));
                paintItem(g2, getWidth(), getHeight(), arc);
                if (isRemovable()) {
                    // paint removable icon
                    Rectangle rectangle = getRemovableRectangle();
                    if (rectangle != null) {
                        paintRemovableIcon(g2, rectangle);
                    }
                }
            } else {
                g2.setColor(getBackground(option.item));
                paintOverflow(g2, getWidth(), getHeight(), arc);
            }
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }

    protected void paintItem(Graphics2D g2, int width, int height, int arc) {
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height, arc, arc));
    }

    protected void paintOverflow(Graphics2D g2, int width, int height, int arc) {
        boolean ltr = getComponentOrientation().isLeftToRight();
        float lineSize = UIScale.scale(option.overflowLineSize);
        float x = lineSize * 4;
        g2.fill(new RoundRectangle2D.Double(ltr ? x : 0, 0, width - x, height, arc, arc));

        g2.fill(createShape(lineSize, width, height, arc, ltr, 0));
        g2.fill(createShape(lineSize, width, height, arc, ltr, 2));
    }

    protected void paintRemovableIcon(Graphics g, Rectangle rec) {
        boolean pressed = option.removableFocus && option.isPressed;
        boolean focus = option.removableFocus;
        option.removableIcon.setColor(getRemovableIconColor(pressed, focus));
        option.removableIcon.paintIcon(this, g, rec.x, rec.y, pressed, focus);
    }

    protected Color getBackground(Object item) {
        if (option.isPressed && !option.removableFocus) {
            return option.pressedBackground;
        } else if (option.hasFocus) {
            return option.hoverBackground;
        } else {
            return option.background;
        }
    }

    protected Color getRemovableIconColor(boolean pressed, boolean focus) {
        return null;
    }

    private Shape createShape(float lineSize, float width, float height, float arc, boolean ltr, int v) {
        float x = lineSize * v;
        float outerX = ltr ? x : 0f;
        float outerW = ltr ? width : width - x;
        float innerX = ltr ? x + lineSize : 0f;
        float innerW = ltr ? width : width - x - lineSize;

        Area area = new Area(new RoundRectangle2D.Float(outerX, 0f, outerW, height, arc, arc));
        area.subtract(new Area(new RoundRectangle2D.Float(innerX, 0f, innerW, height, arc, arc)));
        return area;
    }

    protected static class Option {

        public JMultiSelectComboBox<?> multiSelect;
        public float overflowLineSize = 2f;
        public boolean isPressed;
        public boolean hasFocus;
        public boolean removableFocus;
        public Object item;
        public int index;

        public ItemActionIcon removableIcon;
        public Color background;
        public Color pressedBackground;
        public Color hoverBackground;
        public int arc;
    }
}
