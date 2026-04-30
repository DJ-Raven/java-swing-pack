package raven.swingpack;

import com.formdev.flatlaf.FlatClientProperties;
import raven.swingpack.datetime.*;
import raven.swingpack.datetime.event.DateTimeSelectionEvent;
import raven.swingpack.datetime.event.DateTimeSelectionListener;
import raven.swingpack.datetime.validation.DateTimeValidator;
import raven.swingpack.datetime.validation.ValidationResult;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Raven
 */
public class JDateTimeField extends JTextField implements ChangeListener {

    protected transient DateTimeSelectionEvent selectionEvent = null;
    protected DateTimeHandler handler;
    private DateTimeModel model;
    private DateTimeItemRenderer itemRenderer;
    private String pattern = "DD/MM/yyyy hh:mm a";
    private CommitMode commitMode = CommitMode.TEMP_ON_INVALID;
    private CaseStyle placeholderCaseStyle = CaseStyle.ORIGINAL;
    private SelectionStyle selectionStyle = SelectionStyle.BACKGROUND;
    private Function<DateTimeRenderContext, String> placeholderFormatter;
    private Function<DateTimeRenderContext, String> valueFormatter;
    private DateTimeValidator validator = null;
    private boolean showClearButton;
    private boolean validationPopupEnabled = true;
    private int spaceWidth;
    private int itemGap = 1;
    private int itemArc = 5;
    private Icon validationErrorIcon;
    private Icon validationWarningIcon;
    private Icon validationSuccessIcon;

    private Consumer<?> clearCallback;

    public JDateTimeField() {
        this((DateTimeModel) null);
    }

    public JDateTimeField(LocalDateTime dateTime) {
        this(new DateTimeModel(dateTime));
    }

    public JDateTimeField(DateTimeModel model) {
        init();
        updatePattern();
        if (model == null) {
            setModel(new DateTimeModel());
        } else {
            setModel(model);
        }
    }

    private void init() {
        itemRenderer = new DefaultDateTimeItemRenderer();
        handler = new DateTimeHandler(this);
    }

    public LocalDateTime getSelectedDateTime() {
        return this.model.getSelected();
    }

    public LocalDateTime getInputDateTime() {
        return this.handler.getInputDateTime();
    }

    public void setSelectedDateTime(LocalDateTime dateTime) {
        this.model.setSelected(dateTime);
        this.handler.startValidation();
    }

    public void setSelectedDate(LocalDate date) {
        LocalDateTime current = model.getSelected();
        if (current != null) {
            setSelectedDateTime(current.with(date));
        } else {
            setSelectedDateTime(date.atStartOfDay());
        }
    }

    public void setSelectedTime(LocalTime time) {
        LocalDateTime current = model.getSelected();
        if (current != null) {
            setSelectedDateTime(current.with(time));
        } else {
            setSelectedDateTime(time.atDate(LocalDate.now()));
        }
    }

    public void now() {
        setSelectedDateTime(LocalDateTime.now());
    }

    public void clearSelectedDateTime() {
        this.model.setSelected(null);
    }

    public void clearInput() {
        handler.validation(null);
        handler.resetFocus();
        repaint();
    }

    public DateTimeModel getModel() {
        return model;
    }

    public void setModel(DateTimeModel model) {
        DateTimeModel oldValue = getModel();
        if (oldValue != null) {
            oldValue.removeChangeListener(this);
        }
        this.model = model;
        if (model != null) {
            model.addChangeListener(this);
            LocalDateTime dateTime = model.getSelected();
            handler.validation(dateTime);
            handler.setSelectedDate(dateTime);
        }
        repaint();
        firePropertyChange("model", oldValue, model);
    }

    public DateTimeItemRenderer getItemRenderer() {
        return itemRenderer;
    }

    public void setItemRenderer(DateTimeItemRenderer itemRenderer) {
        this.itemRenderer = itemRenderer;
        repaint();
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        if (!Objects.equals(this.pattern, pattern)) {
            checkPatternValid(pattern);
            this.pattern = pattern;
            updatePattern();
            repaint();
        }
    }

    public CommitMode getCommitMode() {
        return commitMode;
    }

    public void setCommitMode(CommitMode commitMode) {
        if (this.commitMode != commitMode) {
            this.commitMode = commitMode;
        }
    }

    public CaseStyle getPlaceholderCaseStyle() {
        return placeholderCaseStyle;
    }

    public void setPlaceholderCaseStyle(CaseStyle caseStyle) {
        if (this.placeholderCaseStyle != caseStyle) {
            this.placeholderCaseStyle = caseStyle;
            repaint();
            revalidate();
        }
    }

    public DateTimeValidator getValidator() {
        return validator;
    }

    public void setValidator(DateTimeValidator validator) {
        this.validator = validator;
        handler.startValidation();
    }

    public Function<DateTimeRenderContext, String> getPlaceholderFormatter() {
        return placeholderFormatter;
    }

    public void setPlaceholderFormatter(Function<DateTimeRenderContext, String> placeholderFormatter) {
        this.placeholderFormatter = placeholderFormatter;
        repaint();
        revalidate();
    }

    public Function<DateTimeRenderContext, String> getValueFormatter() {
        return valueFormatter;
    }

    public void setValueFormatter(Function<DateTimeRenderContext, String> valueFormatter) {
        this.valueFormatter = valueFormatter;
        repaint();
        revalidate();
    }

    public SelectionStyle getSelectionStyle() {
        return selectionStyle;
    }

    public void setSelectionStyle(SelectionStyle selectionStyle) {
        if (this.selectionStyle != selectionStyle) {
            this.selectionStyle = selectionStyle;
            repaint();
        }
    }

    public boolean isShowClearButton() {
        return showClearButton;
    }

    public void setShowClearButton(boolean showClearButton) {
        if (this.showClearButton != showClearButton) {
            this.showClearButton = showClearButton;
            putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, showClearButton);
            if (showClearButton) {
                putClientProperty(FlatClientProperties.TEXT_FIELD_CLEAR_CALLBACK, getClearCallback());
            } else {
                putClientProperty(FlatClientProperties.TEXT_FIELD_CLEAR_CALLBACK, null);
                clearCallback = null;
            }
        }
    }

    public boolean isValidationPopupEnabled() {
        return validationPopupEnabled;
    }

    public void setValidationPopupEnabled(boolean validationPopupEnabled) {
        this.validationPopupEnabled = validationPopupEnabled;
    }

    public int getSpaceWidth() {
        return spaceWidth;
    }

    public void setSpaceWidth(int spaceWidth) {
        if (this.spaceWidth != spaceWidth) {
            this.spaceWidth = spaceWidth;
            repaint();
            revalidate();
        }
    }

    public int getItemGap() {
        return itemGap;
    }

    public void setItemGap(int itemGap) {
        if (this.itemGap != itemGap) {
            this.itemGap = itemGap;
            repaint();
            revalidate();
        }
    }

    public int getItemArc() {
        return itemArc;
    }

    public void setItemArc(int itemArc) {
        if (this.itemArc != itemArc) {
            this.itemArc = itemArc;
            repaint();
        }
    }

    public Icon getValidationErrorIcon() {
        return validationErrorIcon;
    }

    public void setValidationErrorIcon(Icon validationErrorIcon) {
        this.validationErrorIcon = validationErrorIcon;
        repaint();
        revalidate();
    }

    public Icon getValidationWarningIcon() {
        return validationWarningIcon;
    }

    public void setValidationWarningIcon(Icon validationWarningIcon) {
        this.validationWarningIcon = validationWarningIcon;
        repaint();
        revalidate();
    }

    public Icon getValidationSuccessIcon() {
        return validationSuccessIcon;
    }

    public void setValidationSuccessIcon(Icon validationSuccessIcon) {
        this.validationSuccessIcon = validationSuccessIcon;
        repaint();
        revalidate();
    }

    public boolean isValidationValid() {
        return handler.isValidationValid();
    }

    public boolean isValidationSuccess() {
        return handler.isValidationSuccess();
    }

    public boolean isValidationError() {
        return handler.isValidationError();
    }

    public boolean isValidationWarning() {
        return handler.isValidationWarning();
    }

    public ValidationResult getValidationResult() {
        return handler.getValidationResult();
    }

    public ValidationResult validationDateTime() {
        handler.startValidation();
        repaint();
        return getValidationResult();
    }

    public void setValidationResult(ValidationResult validationResult) {
        handler.setValidationResult(validationResult);
        repaint();
    }

    public void clearValidationField() {
        handler.setValidationResult(null);
        repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setUI(new DateTimeUI(this));
        if (itemRenderer instanceof JComponent) {
            SwingUtilities.updateComponentTreeUI((Component) itemRenderer);
        }
    }

    public Point getValidationPopupLocation(JPopupMenu popup) {
        boolean ltr = getComponentOrientation().isLeftToRight();
        return new Point(ltr ? 0 : getWidth() - popup.getPreferredSize().width, getHeight());
    }

    private void checkPatternValid(String pattern) {
        DateTimeFormatter.ofPattern(pattern);
    }

    private void updatePattern() {
        handler.setParts(DateTimePatternParser.parsePattern(pattern));
    }

    public void addDateTimeSelectionListener(DateTimeSelectionListener listener) {
        listenerList.add(DateTimeSelectionListener.class, listener);
    }

    public void removeDateTimeSelectionListener(DateTimeSelectionListener listener) {
        this.listenerList.remove(DateTimeSelectionListener.class, listener);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() != handler) {
            LocalDateTime dateTime = getModel().getSelected();
            handler.validation(dateTime);
            handler.setSelectedDate(dateTime);
        }
        repaint();
        fireDateTimeSelectionChanged();
    }

    public void fireDateTimeSelectionChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == DateTimeSelectionListener.class) {
                if (selectionEvent == null) {
                    selectionEvent = new DateTimeSelectionEvent(this);
                }
                ((DateTimeSelectionListener) listeners[i + 1]).dateTimeSelected(selectionEvent);
            }
        }
    }

    public DateTimeHandler getHandler() {
        return handler;
    }

    public boolean isValid(DateTimePart part) {
        return !part.isEmpty() && handler.isValid(part);
    }

    public String getDisplayText(DateTimeRenderContext context) {
        if (context.isValueRender()) {
            return getValue(context);
        }
        return getPlaceholder(context);
    }

    private String getPlaceholder(DateTimeRenderContext context) {
        String placeholder = null;
        if (placeholderFormatter != null) {
            placeholder = placeholderFormatter.apply(context);
        }
        if (placeholder == null) {
            DateTimePart part = context.getPart();
            if (!part.isSeparator()) {
                if (placeholderCaseStyle == CaseStyle.LOWER) {
                    return part.getPattern().toLowerCase();
                } else if (placeholderCaseStyle == CaseStyle.UPPER) {
                    return part.getPattern().toUpperCase();
                }
            }
            return part.getPattern();
        }
        return placeholder;
    }

    private String getValue(DateTimeRenderContext context) {
        String value = null;
        if (valueFormatter != null) {
            value = valueFormatter.apply(context);
        }
        if (value == null) {
            DateTimePart part = context.getPart();
            value = part.toString();
        }
        return value;
    }

    private Consumer<?> getClearCallback() {
        if (clearCallback == null) {
            clearCallback = e -> {
                clearSelectedDateTime();
                clearInput();
            };
        }
        return clearCallback;
    }

    public enum CaseStyle {
        ORIGINAL,
        LOWER,
        UPPER,
    }

    public enum SelectionStyle {
        BACKGROUND,
        DASHED,
        DASHED_WITH_BACKGROUND
    }

    public enum CommitMode {
        NULL_ON_INVALID,
        REVERT_ON_INVALID,
        TEMP_ON_INVALID
    }
}
