package raven.swingpack.datetime;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * @author Raven
 */
public class DateTimeModel {

    protected EventListenerList listenerList = new EventListenerList();
    public LocalDateTime selected;

    public DateTimeModel() {
        this(null);
    }

    public DateTimeModel(LocalDateTime dateTime) {
        this.selected = dateTime;
    }

    public LocalDateTime getSelected() {
        return selected;
    }

    public void setSelected(LocalDateTime dateTime) {
        if (!Objects.equals(this.selected, dateTime)) {
            this.selected = dateTime;
            fireStateChanged(this);
        }
    }

    public void setSelectedDate(LocalDate date) {
        LocalDateTime current = getSelected();
        if (current != null) {
            setSelected(current.with(date));
        } else {
            setSelected(date.atStartOfDay());
        }
    }

    public void setSelectedTime(LocalTime time) {
        LocalDateTime current = getSelected();
        if (current != null) {
            setSelected(current.with(time));
        } else {
            setSelected(time.atDate(LocalDate.now()));
        }
    }

    protected void setSelected(LocalDateTime dateTime, DateTimeHandler handler) {
        if (!Objects.equals(this.selected, dateTime)) {
            this.selected = dateTime;
            fireStateChanged(handler);
        }
    }

    public void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }

    protected void fireStateChanged(Object source) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                ((ChangeListener) listeners[i + 1]).stateChanged(new ChangeEvent(source));
            }
        }
    }
}
