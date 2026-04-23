package raven.swingpack.datetime;

import raven.swingpack.JDateTimeField;
import raven.swingpack.datetime.event.InputChangeEvent;
import raven.swingpack.datetime.event.InputChangeListener;
import raven.swingpack.datetime.validation.ValidationResult;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Raven
 */
public class DateTimeHandler {

    protected EventListenerList listenerList = new EventListenerList();
    private final JDateTimeField dateTimeField;
    private ValidationResult validationResult;
    private List<DateTimePart> parts;
    private LocalDateTime dateTime;
    private boolean invalidDay;
    private int selectedIndex = -1;
    private int lastSelectedIndex = -1;

    public DateTimeHandler(JDateTimeField dateTimeField) {
        this.dateTimeField = dateTimeField;
        this.dateTimeField.setCaret(new NoCaretPaint());
        installListener();
        disableBeepAction();
    }

    private void installListener() {
        dateTimeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume();
                char ch = e.getKeyChar();
                if (Character.isDigit(ch)) {
                    handleInput(ch);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_LEFT) {
                    moveToPrevious();
                } else if (code == KeyEvent.VK_RIGHT) {
                    moveToNext();
                } else if (code == KeyEvent.VK_BACK_SPACE) {
                    removeInput();
                } else if (code == KeyEvent.VK_UP) {
                    adjustValue(1);
                } else if (code == KeyEvent.VK_DOWN) {
                    adjustValue(-1);
                }
            }
        });

        dateTimeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                initFocus();
            }

            @Override
            public void focusLost(FocusEvent e) {
                removeFocus();
                if (dateTimeField.getCommitMode() != JDateTimeField.CommitMode.TEMP_ON_INVALID) {
                    LocalDateTime dateTime = dateTimeField.getModel().getSelected();
                    validation(dateTime);
                    setSelectedDate(dateTime);
                    lastSelectedIndex = -1;
                }
            }
        });
    }

    private void disableBeepAction() {
        InputMap im = dateTimeField.getInputMap();
        im.put(KeyStroke.getKeyStroke("BACK_SPACE"), "none");
        im.put(KeyStroke.getKeyStroke("DELETE"), "none");
    }

    private void handleInput(char digit) {
        DateTimePart part = getSelectedPart();
        if (part != null) {
            DateTimeValue oldValue = getSelectedDate();
            int index = selectedIndex;
            if (part.isReset()) {
                part.clear();
                part.setReset(false);
            }
            int maxLength = part.getMaxLength();
            boolean next = false;
            int d = digit - '0';
            if (part.getLength() == maxLength) {
                part.clear();
            }
            int max = part.getType().getMaxValue();
            if (part.isEmpty()) {
                int maxDigit = max / (int) Math.pow(10, maxLength - 1);
                if (d > maxDigit || maxLength == 1) {
                    next = true;
                }
            } else {
                int temp = part.createTemp(digit);
                if (temp > max) {
                    part.clear();
                    next = true;
                } else if (maxLength == part.getLength() + 1) {
                    next = true;
                }
            }
            part.append(digit);
            if (next) {
                moveToNext();
            }
            dateInputChanged(oldValue, index);
            update();
        }
    }

    private void removeInput() {
        DateTimePart part = getSelectedPart();
        if (part != null) {
            DateTimeValue oldValue = getSelectedDate();
            int index = selectedIndex;
            boolean remove = part.remove();
            if (!remove) {
                move(-1);
            } else {
                dateInputChanged(oldValue, index);
            }
            fireValueChanged(selectedIndex, getSelectedDate().toLocalDateTimeOrNull());
            update();
        }
    }

    private void moveToNext() {
        move(1);
    }

    private void moveToPrevious() {
        move(-1);
    }

    private void move(int offset) {
        if (offset == 0) return;
        int nextIndex = selectedIndex + offset;
        if (nextIndex < 0 || nextIndex >= parts.size()) return;
        boolean moved = moveSelection(nextIndex, offset > 0);
        if (moved) {
            update();
        }
    }

    private boolean moveSelection(int index, boolean forward) {
        int step = forward ? 1 : -1;
        for (int i = index; i >= 0 && i < parts.size(); i += step) {
            DateTimePart part = parts.get(i);
            if (!part.isSeparator()) {
                selectIndex(i);
                return true;
            }
        }
        return false;
    }

    private DateTimePart getSelectedPart() {
        if (parts == null) return null;
        if (selectedIndex >= 0 && selectedIndex < parts.size()) {
            DateTimePart part = parts.get(selectedIndex);
            if (part.isSeparator()) {
                return null;
            }
            return part;
        }
        return null;
    }

    private void selectIndex(int index) {
        if (this.selectedIndex != index) {
            int oldIndex = this.selectedIndex;
            DateTimePart oldPart = getSelectedPart();
            this.selectedIndex = index;
            DateTimePart newPart = getSelectedPart();
            if (newPart != null) {
                newPart.setReset(true);
            }
            if (oldPart != null) {
                oldPart.format();
            }
            fireSelectionChanged(oldIndex, index);
        }
    }

    private void adjustValue(int delta) {
        DateTimePart part = getSelectedPart();
        if (part != null) {
            DateTimeValue oldValue = getSelectedDate();
            if (part.isEmpty() || !isValid(part)) {
                part.setValue(DateTimePatternParser.getDefaultValue(part.getType()));
            } else {
                int current = part.getValue();
                if (current == 0 && part.isNotAllowZeroValue()) {
                    current = DateTimePatternParser.adjustValue(current, 1, part.getType().getMaxValue());
                }
                int max = part.getType().getMaxValue();
                int min = part.getType().getMinValue();
                int newValue = current + delta;
                if (newValue < min) newValue = max;
                if (newValue > max) newValue = min;
                part.setValue(newValue);
            }
            dateInputChanged(oldValue, selectedIndex);
            update();
        }
    }

    private void initFocus() {
        if (parts == null || parts.isEmpty()) return;
        moveSelection(lastSelectedIndex == -1 ? 0 : lastSelectedIndex, true);
    }

    private void removeFocus() {
        lastSelectedIndex = selectedIndex;
        selectIndex(-1);
    }

    private void update() {
        dateTimeField.repaint();
    }

    private void dateInputChanged(DateTimeValue oldValue, int indexChanged) {
        DateTimeValue newValue = getSelectedDate();
        if (!Objects.equals(oldValue, newValue)) {
            LocalDateTime newDateTime = null;
            boolean valid;
            if (newValue.isValid()) {
                invalidDay = !newValue.isValidDay();
                if (invalidDay) {
                    setValidationResult(null);
                    valid = false;
                } else {
                    newDateTime = newValue.toLocalDateTime();
                    valid = validation(newDateTime);
                    if (valid) {
                        dateTimeField.getModel().setSelected(newDateTime, this);
                    }
                    fireValueChanged(indexChanged, newDateTime);
                }
            } else {
                validation(null);
                valid = false;
                invalidDay = false;
            }
            if (!valid && dateTimeField.getCommitMode() != JDateTimeField.CommitMode.REVERT_ON_INVALID) {
                dateTimeField.getModel().setSelected(null, this);
            }
            dateTime = newDateTime;
        }
    }

    public LocalDateTime getInputDateTime() {
        return getSelectedDate().toLocalDateTimeOrNull();
    }

    private DateTimeValue getSelectedDate() {
        List<DateTimeValue.Value> values = new ArrayList<>();
        for (DateTimePart part : parts) {
            if (part.getType() != DateTimePart.Type.SEPARATOR) {
                boolean isValue = isValid(part);
                values.add(new DateTimeValue.Value(!isValue ? null : part.getValue(), part.getType()));
            }
        }
        return new DateTimeValue(values.toArray(new DateTimeValue.Value[0]));
    }

    public List<DateTimePart> getParts() {
        return parts;
    }

    public void setParts(List<DateTimePart> parts) {
        this.parts = parts;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public boolean isValidationValid() {
        DateTimeValue value = getSelectedDate();
        return !invalidDay && (value.isNull() || value.isValid()) && (validationResult == null || validationResult.isValid());
    }

    public boolean isValidationSuccess() {
        return validationResult != null && validationResult.isSuccess();
    }

    public boolean isValidationError() {
        return validationResult != null && validationResult.isError();
    }

    public boolean isValidationWarning() {
        return validationResult != null && validationResult.isWarning();
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public void resetFocus() {
        setSelectedDate(null);
        if (selectedIndex != -1) {
            moveSelection(0, true);
        }
    }

    public void setSelectedDate(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        this.invalidDay = false;
        if (dateTime == null) {
            for (DateTimePart part : parts) {
                if (!part.isSeparator()) {
                    part.clear();
                }
            }
        } else {
            for (DateTimePart part : parts) {
                if (part.getType() == DateTimePart.Type.DAY) {
                    part.setValue(dateTime.getDayOfMonth());
                } else if (part.getType() == DateTimePart.Type.MONTH) {
                    part.setValue(dateTime.getMonthValue());
                } else if (part.getType() == DateTimePart.Type.YEAR) {
                    part.setValue(dateTime.getYear());
                } else if (part.getType() == DateTimePart.Type.HOUR_24) {
                    part.setValue(dateTime.getHour());
                } else if (part.getType() == DateTimePart.Type.HOUR_12) {
                    int hour = dateTime.getHour();
                    hour = hour % 12;
                    if (hour == 0) {
                        hour = 12;
                    }
                    part.setValue(hour);
                } else if (part.getType() == DateTimePart.Type.MINUTE) {
                    part.setValue(dateTime.getMinute());
                } else if (part.getType() == DateTimePart.Type.SECOND) {
                    part.setValue(dateTime.getSecond());
                } else if (part.getType() == DateTimePart.Type.AM_PM) {
                    part.setValue(dateTime.getHour() < 12 ? 1 : 2);
                }
            }
        }
        fireValueChanged(-1, dateTime);
    }

    public boolean isValid(DateTimePart part) {
        if (part.isEmpty()) return false;
        if (((part.getType() == DateTimePart.Type.MONTH && part.getPatternLength() > 2) || part.getType() == DateTimePart.Type.AM_PM)
                && !part.getType().isValid(part.getValue())) {
            return false;
        }
        return true;
    }

    protected LocalDateTime getDateTime() {
        return dateTime;
    }

    protected int getSelectedIndex() {
        return selectedIndex;
    }

    protected void setSelectedIndex(int selectedIndex) {
        if (this.selectedIndex != selectedIndex) {
            if (selectedIndex >= 0 && selectedIndex < parts.size()) {
                if (parts.get(selectedIndex).isSeparator()) {
                    return;
                }
                lastSelectedIndex = selectedIndex;
                selectIndex(selectedIndex);
                dateTimeField.repaint();
            }
        }
    }

    public boolean isInvalidDay() {
        return invalidDay;
    }

    public void addInputListener(InputChangeListener listener) {
        listenerList.add(InputChangeListener.class, listener);
    }

    public void removeInputListener(InputChangeListener listener) {
        listenerList.remove(InputChangeListener.class, listener);
    }

    protected void fireSelectionChanged(int oldIndex, int newIndex) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == InputChangeListener.class) {
                ((InputChangeListener) listeners[i + 1]).selectionChanged(new InputChangeEvent(this, null, oldIndex, newIndex));
            }
        }
    }

    protected void fireValueChanged(int index, LocalDateTime value) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == InputChangeListener.class) {
                ((InputChangeListener) listeners[i + 1]).valueChanged(new InputChangeEvent(this, value, index, index));
            }
        }
    }

    public void startValidation() {
        DateTimeValue value = getSelectedDate();
        boolean valid = validation(value.toLocalDateTimeOrNull());
        if (!valid && dateTimeField.getCommitMode() != JDateTimeField.CommitMode.REVERT_ON_INVALID) {
            dateTimeField.getModel().setSelected(null, this);
        }
    }

    public boolean validation(LocalDateTime dateTime) {
        boolean valid;
        ValidationResult validation = null;
        if (dateTimeField.getValidator() != null) {
            validation = dateTimeField.getValidator().validate(dateTime);
            valid = validation.isValid();
        } else {
            valid = true;
        }
        setValidationResult(validation);
        return valid;
    }

    private static class NoCaretPaint extends DefaultCaret {
        @Override
        public void paint(Graphics g) {
            // ignore paint caret
        }
    }
}
