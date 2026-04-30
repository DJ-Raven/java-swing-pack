package raven.swingpack.datetime.event;

import java.time.LocalDateTime;
import java.util.EventObject;

/**
 * @author Raven
 */
public class InputChangeEvent extends EventObject {

    private final LocalDateTime value;
    private final int oldIndex;
    private final int newIndex;

    public InputChangeEvent(Object source, LocalDateTime value, int oldIndex, int newIndex) {
        super(source);
        this.value = value;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    public LocalDateTime getValue() {
        return value;
    }

    public int getOldIndex() {
        return oldIndex;
    }

    public int getNewIndex() {
        return newIndex;
    }
}
