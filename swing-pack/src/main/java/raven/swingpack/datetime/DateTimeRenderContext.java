package raven.swingpack.datetime;

import raven.swingpack.JDateTimeField;

import java.time.LocalDateTime;

/**
 * @author Raven
 */
public class DateTimeRenderContext {

    public JDateTimeField getField() {
        return field;
    }

    public DateTimePart getPart() {
        return part;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public boolean isValueRender() {
        return isValueRender;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }

    public int getIndex() {
        return index;
    }

    public DateTimeRenderContext(JDateTimeField field, DateTimePart part, LocalDateTime dateTime, boolean isValueRender, boolean selected, boolean hasFocus, int index) {
        this.field = field;
        this.part = part;
        this.dateTime = dateTime;
        this.isValueRender = isValueRender;
        this.selected = selected;
        this.hasFocus = hasFocus;
        this.index = index;
    }

    private final JDateTimeField field;
    private final DateTimePart part;
    private final LocalDateTime dateTime;
    private final boolean isValueRender;
    private final boolean selected;
    private final boolean hasFocus;
    private final int index;
}
