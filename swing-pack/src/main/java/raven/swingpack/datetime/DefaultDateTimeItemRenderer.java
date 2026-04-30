package raven.swingpack.datetime;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.ScaledEmptyBorder;
import com.formdev.flatlaf.util.UIScale;
import raven.swingpack.JDateTimeField;
import raven.swingpack.datetime.validation.ValidationResult;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.awt.*;

/**
 * @author Raven
 */
public class DefaultDateTimeItemRenderer extends JLabel implements DateTimeItemRenderer {

    protected Option option;
    protected Border defaultBorder;
    protected boolean isUseSpaceWidth;

    public DefaultDateTimeItemRenderer() {
        defaultBorder = new ScaledEmptyBorder(new Insets(1, 2, 1, 2));
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

        option.placeholderForeground = UIManager.getColor("TextField.placeholderForeground");
        option.errorForeground = UIManager.getColor("Component.error.borderColor");
        option.warningForeground = UIManager.getColor("Component.warning.borderColor");
        option.arc = UIManager.getInt("Component.arc");
    }

    private ValidationResult.Violation getViolation(JDateTimeField field, DateTimePart part) {
        if (part.getType() == DateTimePart.Type.DAY) {
            if (field.getHandler().isInvalidDay()) {
                return new ValidationResult.Violation(DateTimePart.Type.DAY, ValidationResult.Severity.ERROR, "Invalid Day");
            }
        }
        return field.getValidationResult() == null ? null : field.getValidationResult().check(part);
    }

    @Override
    public Component getDateTimeItemRendererComponent(JDateTimeField field, DateTimePart part, boolean isSelected, boolean hasFocus, int index) {
        option.field = field;
        option.violation = getViolation(field, part);
        option.isSelected = isSelected;
        option.hasFocus = hasFocus;
        option.part = part;
        option.index = index;

        DateTimeRenderContext context = new DateTimeRenderContext(field, part, field.getHandler().getDateTime(), field.isValid(part), isSelected, hasFocus, index);
        String display = field.getDisplayText(context);
        Color foreground;
        if (context.isValueRender()) {
            if (isSelected && field.getSelectionStyle() != JDateTimeField.SelectionStyle.DASHED) {
                foreground = field.getSelectedTextColor();
            } else {
                foreground = field.getForeground();
            }
        } else {
            foreground = option.placeholderForeground;
        }
        setFont(field.getFont());
        setBackground(field.getBackground());
        setForeground(foreground);
        if (part.isSeparator()) {
            setBorder(null);
        } else {
            setBorder(defaultBorder);
        }
        isUseSpaceWidth = isUseSpaceWidth(display);

        setText(isUseSpaceWidth ? "" : display);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        FlatUIUtils.setRenderingHints(g2);
        int x = 0;
        int y = 0;
        int width = getWidth();
        int height = getHeight();
        int itemArc = scale(option.field.getItemArc() == -1 ? option.arc : option.field.getItemArc());
        boolean paintBg = option.field.getSelectionStyle() != JDateTimeField.SelectionStyle.DASHED;
        boolean paintDashed = option.field.getSelectionStyle() != JDateTimeField.SelectionStyle.BACKGROUND;
        Color bg = null;
        if (paintBg) {
            if (option.isSelected) {
                bg = option.field.getSelectionColor();
            }
        }
        // paint selection
        if (bg != null) {
            bg = FlatUIUtils.deriveColor(bg, getBackground());
            g2.setColor(bg);
            FlatUIUtils.paintComponentBackground(g2, x, y, width, height, 0, itemArc);
        }

        // paint violation validation
        if (option.violation != null) {
            Color violationBg = option.violation.getSeverity() == ValidationResult.Severity.ERROR ? option.errorForeground : option.warningForeground;
            if (bg != null) {
                violationBg = ColorFunctions.mix(violationBg, bg, 0.6f);
            }
            g2.setColor(violationBg);
            FlatUIUtils.paintComponentBackground(g2, x, y, width, height, 0, itemArc);
        }

        // paint dashed selection indicator
        if (option.isSelected && paintDashed) {
            int rgb = getBackground().getRGB();
            Color inverted = new Color(~rgb | 0xFF000000, true);
            g2.setColor(inverted);
            BasicGraphicsUtils.drawDashedRect(g2, x, y, width, height);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        if (isUseSpaceWidth) {
            dim.width = scale(option.field.getSpaceWidth());
            return dim;
        }
        return dim;
    }

    protected boolean isUseSpaceWidth(String text) {
        return option.field.getSpaceWidth() >= 0 && option.part.isSeparator() && text.trim().isEmpty();
    }

    protected int scale(int value) {
        return UIScale.scale(value);
    }

    protected static class Option {

        public JDateTimeField field;
        public ValidationResult.Violation violation;
        public boolean isSelected;
        public boolean hasFocus;
        public DateTimePart part;
        public int index;

        protected Color placeholderForeground;
        protected Color errorForeground;
        private Color warningForeground;
        protected int arc;
    }
}
