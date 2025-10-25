package raven.swingpack.multiselect.event;

import java.util.EventListener;

/**
 * @author Raven
 */
public interface MultiSelectListener extends EventListener {

    void itemAdded(MultiSelectEvent event);

    void itemRemoved(MultiSelectEvent event);

    void itemSelected(MultiSelectEvent event);

    void overflowSelected(MultiSelectEvent event);
}
