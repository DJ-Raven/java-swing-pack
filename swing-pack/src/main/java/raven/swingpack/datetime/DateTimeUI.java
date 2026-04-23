package raven.swingpack.datetime;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatTextFieldUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import raven.swingpack.JDateTimeField;
import raven.swingpack.datetime.validation.ValidationResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Raven
 */
public class DateTimeUI extends FlatTextFieldUI {

    protected JDateTimeField dateField;
    protected CellRendererPane rendererPane;
    protected FlatSVGIcon defaultSuccessIcon;
    protected FlatSVGIcon defaultErrorIcon;
    protected FlatSVGIcon defaultWarningIcon;
    protected Color successColor;
    protected Color errorColor;
    protected Color warningColor;

    protected List<ItemRec> itemRecs;
    protected ItemRec validationIconRec = new ItemRec();
    protected ValidationPopup validationPopup;
    protected MouseAdapter mouseListener;
    protected Cursor oldCursor;
    protected boolean mouseOver;

    public DateTimeUI(JDateTimeField dateField) {
        this.dateField = dateField;
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.rendererPane = new CellRendererPane();
        this.defaultSuccessIcon = new FlatSVGIcon("raven/swingpack/icon/success.svg", 0.9f);
        this.defaultErrorIcon = new FlatSVGIcon("raven/swingpack/icon/error.svg", 0.9f);
        this.defaultWarningIcon = new FlatSVGIcon("raven/swingpack/icon/warning.svg", 0.9f);

        successColor = UIManager.getColor("Component.success.focusedBorderColor");
        errorColor = UIManager.getColor("Component.error.focusedBorderColor");
        warningColor = UIManager.getColor("Component.warning.focusedBorderColor");
        defaultSuccessIcon.setColorFilter(new FlatSVGIcon.ColorFilter((component, color) -> successColor));
        defaultErrorIcon.setColorFilter(new FlatSVGIcon.ColorFilter((component, color) -> errorColor));
        defaultWarningIcon.setColorFilter(new FlatSVGIcon.ColorFilter((component, color) -> warningColor));

        itemRecs = new ArrayList<>();
        mouseListener = new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                if (!validationIconRec.isEmpty() && validationIconRec.contain(e.getX(), e.getY())) {
                    if (!mouseOver) {
                        mouseOver = true;
                        if (oldCursor == null) {
                            oldCursor = dateField.getCursor();
                            dateField.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                    if (dateField.isValidationPopupEnabled() && !isValidationPopupVisible()) {
                        ValidationResult result = dateField.getValidationResult();
                        if (result != null) {
                            getValidationPopup().showResult(result);
                        }
                    }
                } else {
                    if (mouseOver) {
                        mouseOver = false;
                        dateField.setCursor(oldCursor);
                        oldCursor = null;
                        if (dateField.isValidationPopupEnabled() && isValidationPopupVisible()) {
                            getValidationPopup().hide();
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int index = getIndex(e.getX(), e.getY());
                    dateField.getHandler().setSelectedIndex(index);
                }
            }
        };

        dateField.addMouseListener(mouseListener);
        dateField.addMouseMotionListener(mouseListener);
    }

    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        rendererPane.removeAll();
        itemRecs.clear();
        dateField.removeMouseListener(mouseListener);
        dateField.removeMouseMotionListener(mouseListener);
        if (oldCursor != null) {
            dateField.setCursor(oldCursor);
        }
        mouseListener = null;
        itemRecs = null;
        validationIconRec = null;
        validationPopup = null;
        oldCursor = null;

        dateField = null;
        rendererPane = null;
        defaultSuccessIcon = null;
        defaultErrorIcon = null;
        defaultWarningIcon = null;
        successColor = null;
        errorColor = null;
        warningColor = null;

    }

    private int getIndex(int x, int y) {
        for (int i = 0; i < itemRecs.size(); i++) {
            if (itemRecs.get(i).contain(x, y)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void paintSafely(Graphics g) {
        super.paintSafely(g);
        Rectangle bounds = getVisibleEditorRect();
        // adjust the clip to fit bound size
        Rectangle clip = g.getClipBounds();
        if (clip != null) {
            g.setClip(FlatUIUtils.addInsets(clip, dateField.getInsets()));
        }
        paintImpl(g, bounds);
    }

    @Override
    protected void updateClearButton() {
        if (clearButton == null) {
            return;
        }
        if (!clearButton.isVisible()) {
            clearButton.setVisible(true);
        }
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return applyRenderSize(super.getMinimumSize(c));
    }

    private Dimension applyRenderSize(Dimension size) {
        Dimension renderSize = getRenderComponentSize();
        int width = 0;
        if (renderSize != null) {
            width += renderSize.width;
        }
        width += getLeadingIconWidth() + getTrailingIconWidth();
        int leading = getLeadingIconWidth();
        int trailing = getTrailingIconWidth();
        for (JComponent comp : getLeadingComponents()) {
            if (comp != null && comp.isVisible())
                leading += comp.getPreferredSize().width;
        }
        for (JComponent comp : getTrailingComponents()) {
            if (comp != null && comp.isVisible())
                trailing += comp.getPreferredSize().width;
        }
        width += leading + trailing;
        Insets insets = getComponent().getInsets();
        if (insets != null) {
            width += (insets.left + insets.right);
            if (leading > 0) {
                int newLeftInset = Math.min(insets.top, insets.left);
                if (newLeftInset < insets.left) {
                    int diff = insets.left - newLeftInset;
                    width -= diff;
                }
            }
            if (trailing > 0) {
                int newRightInset = Math.min(insets.top, insets.right);
                if (newRightInset < insets.right) {
                    int diff = insets.right - newRightInset;
                    width -= diff;
                }
            }
        }
        Icon validationIcon = getValidationIcon(dateField.getValidationResult());
        if (validationIcon != null) {
            width += validationIcon.getIconWidth() + UIScale.scale(iconTextGap);
        }
        size.width = Math.max(size.width, width);
        return size;
    }

    private void paintImpl(Graphics g, Rectangle parentRect) {
        List<DateTimePart> parts = dateField.getHandler().getParts();
        if (parts == null) {
            return;
        }
        boolean ltr = isLeftToRight();
        Rectangle rec = new Rectangle();
        int alignment = ((JTextField) getComponent()).getHorizontalAlignment();
        rec.x = parentRect.x + getAlignmentOffset(alignment, ltr, parentRect.width);
        rec.y = parentRect.y;
        int gap = UIScale.scale(dateField.getItemGap());
        itemRecs.clear();
        for (int i = 0; i < parts.size(); i++) {
            paintItem(g, parts.get(i), i, rec, parentRect);
            rec.x += gap;
        }

        // paint validation
        Icon validationIcon = getValidationIcon(dateField.getValidationResult());
        if (validationIcon != null) {
            paintValidation(g, validationIcon, parentRect, ltr);
        } else {
            validationIconRec.width = 0;
            validationIconRec.height = 0;
        }
        rendererPane.removeAll();
    }

    protected void paintItem(Graphics g, DateTimePart part, int index, Rectangle rec, Rectangle parentRec) {
        Component c = createComponent(part, index);
        Dimension size = c.getPreferredSize();
        rec.setSize(size);
        rec.y = parentRec.y + (parentRec.height - rec.height) / 2;
        if (index == 0 && c instanceof JComponent) {
            // adjust the first item position by removing left inset to align text properly
            Insets insets = ((JComponent) c).getInsets();
            if (insets != null) {
                rec.x -= insets.left;
            }
        }
        itemRecs.add(new ItemRec(rec.x, rec.y, rec.width, rec.height));
        Rectangle itemRec = new Rectangle(rec);
        rec.x += rec.width;
        rendererPane.paintComponent(g, c, dateField, itemRec);
    }

    protected void paintValidation(Graphics g, Icon icon, Rectangle parentRect, boolean ltr) {
        validationIconRec.width = icon.getIconWidth();
        validationIconRec.height = icon.getIconHeight();
        validationIconRec.x = ltr ? parentRect.x + parentRect.width - validationIconRec.width : parentRect.x;
        validationIconRec.y = parentRect.y + (parentRect.height - icon.getIconHeight()) / 2;
        icon.paintIcon(getComponent(), g, validationIconRec.x, validationIconRec.y);
    }

    protected Icon getValidationIcon(ValidationResult validationResult) {
        if (dateField.getValidator() == null || validationResult == null) {
            return null;
        }
        Icon icon = null;
        if (validationResult.isError()) {
            icon = getIcon(ValidationResult.Severity.ERROR);
        } else if (validationResult.isWarning()) {
            icon = getIcon(ValidationResult.Severity.WARNING);
        } else if (validationResult.isSuccess()) {
            icon = getIcon(ValidationResult.Severity.SUCCESS);
        }
        return icon;
    }

    private Icon getIcon(ValidationResult.Severity severity) {
        if (severity == ValidationResult.Severity.ERROR) {
            return dateField.getValidationErrorIcon() != null ? dateField.getValidationErrorIcon() : defaultErrorIcon;
        } else if (severity == ValidationResult.Severity.WARNING) {
            return dateField.getValidationWarningIcon() != null ? dateField.getValidationWarningIcon() : defaultWarningIcon;
        } else if (severity == ValidationResult.Severity.SUCCESS) {
            return dateField.getValidationSuccessIcon() != null ? dateField.getValidationSuccessIcon() : defaultSuccessIcon;
        }
        return null;
    }

    private Component createComponent(DateTimePart part, int index) {
        boolean isSelected = index == dateField.getHandler().getSelectedIndex();
        // no use hasFocus
        boolean hasFocus = false;
        return dateField.getItemRenderer().getDateTimeItemRendererComponent(dateField, part, isSelected, hasFocus, index);
    }

    private int getAlignmentOffset(int alignment, boolean ltr, int parentWidth) {
        int resolvedAlignment = alignment;
        if (alignment == SwingConstants.LEADING) {
            resolvedAlignment = ltr ? SwingConstants.LEFT : SwingConstants.RIGHT;
        } else if (alignment == SwingConstants.TRAILING) {
            resolvedAlignment = ltr ? SwingConstants.RIGHT : SwingConstants.LEFT;
        }
        if (resolvedAlignment != SwingConstants.LEFT) {
            Dimension renderSize = getRenderComponentSize();
            if (renderSize != null) {
                if (resolvedAlignment == SwingConstants.CENTER) {
                    return Math.max(0, (parentWidth - renderSize.width) / 2);
                } else {
                    return Math.max(0, parentWidth - renderSize.width);
                }
            }
        }
        return 0;
    }

    private Dimension getRenderComponentSize() {
        List<DateTimePart> parts = dateField.getHandler().getParts();
        if (parts == null) {
            return null;
        }
        int gap = UIScale.scale(dateField.getItemGap());
        int width = 0;
        int height = 0;
        int count = parts.size();
        for (int i = 0; i < count; i++) {
            DateTimePart part = parts.get(i);
            Component c = createComponent(part, i);
            Dimension size = c.getPreferredSize();
            width += size.width;
            height = Math.max(height, size.height);
            if ((i == 0 || i == count - 1) && c instanceof JComponent) {
                Insets insets = ((JComponent) c).getInsets();
                if (insets != null) {
                    width -= (i == 0 ? insets.left : insets.right);
                }
            }
        }
        if (count > 1) {
            width += (gap * (count - 1));
        }
        return new Dimension(width, height);
    }

    private boolean isLeftToRight() {
        return getComponent().getComponentOrientation().isLeftToRight();
    }

    private boolean isValidationPopupVisible() {
        return validationPopup != null && validationPopup.isVisible();
    }

    private ValidationPopup getValidationPopup() {
        if (validationPopup == null) {
            validationPopup = new ValidationPopup(dateField);
        }
        return validationPopup;
    }

    protected static class ItemRec {

        public ItemRec() {
        }

        public ItemRec(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        private int x;
        private int y;
        private int width;
        private int height;

        private boolean contain(int x, int y) {
            return x >= this.x && y >= this.y && x <= this.x + this.width && y <= this.y + this.height;
        }

        private boolean isEmpty() {
            return width == 0 || height == 0;
        }
    }

    protected class ValidationPopup {

        private final JDateTimeField field;
        private final JPopupMenu popup;

        public ValidationPopup(JDateTimeField field) {
            this.field = field;
            popup = new JPopupMenu();
            popup.setFocusable(false);
            popup.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5;");
        }

        public boolean isVisible() {
            return popup.isVisible();
        }

        public void hide() {
            popup.setVisible(false);
        }

        public void showResult(ValidationResult result) {
            popup.removeAll();
            boolean show = false;
            for (ValidationResult.Violation v : result.getViolations()) {
                Icon icon = getIcon(v.getSeverity());
                String message = v.getMessage();
                if (icon != null && message != null && !message.isEmpty()) {
                    JLabel label = new JLabel(icon);
                    label.setText(v.getMessage());
                    popup.add(label);
                    show = true;
                }
            }
            if (show) {
                Point location = field.getValidationPopupLocation(popup);
                popup.applyComponentOrientation(field.getComponentOrientation());
                popup.show(field, location.x, location.y);
            }
        }
    }
}
