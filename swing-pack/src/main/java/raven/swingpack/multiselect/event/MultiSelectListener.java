package raven.swingpack.multiselect.event;

import java.util.EventListener;

/**
 * @author Raven
 */
public interface MultiSelectListener extends EventListener {
    // SINGLE ITEM EVENTS - For user interactions (keep animations)
    void itemAdded(MultiSelectEvent event);
    void itemRemoved(MultiSelectEvent event);
    void itemSelected(MultiSelectEvent event);
    void overflowSelected(MultiSelectEvent event);

    // BATCH EVENTS - For programmatic operations
    void itemsAdded(MultiSelectEvent event);
    void itemsRemoved(MultiSelectEvent event);

    // SILENT EVENTS - Maximum performance, no UI updates
    void itemsAddedSilent(MultiSelectEvent event);
    void itemsRemovedSilent(MultiSelectEvent event);
}