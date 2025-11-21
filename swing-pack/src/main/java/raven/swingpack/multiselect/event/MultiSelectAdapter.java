package raven.swingpack.multiselect.event;

/**
 * @author Raven
 */
public abstract class MultiSelectAdapter implements MultiSelectListener {

    @Override
    public void itemAdded(MultiSelectEvent event) {
    }

    @Override
    public void itemRemoved(MultiSelectEvent event) {
    }

    @Override
    public void itemSelected(MultiSelectEvent event) {
    }


    @Override
    public void itemsAddedSilent(MultiSelectEvent event) {
        // Default: do nothing - no UI updates
    }

    @Override
    public void itemsRemovedSilent(MultiSelectEvent event) {
        // Default: do nothing - no UI updates
    }

    @Override
    public void overflowSelected(MultiSelectEvent event) {
    }

    @Override
    public void itemsAdded(MultiSelectEvent event) {
        // Default implementation: call itemAdded for each item for backward compatibility
        for (int i = 0; i < event.getItems().length; i++) {
            itemAdded(new MultiSelectEvent(event.getSource(), event.getItems()[i], event.getIndexes()[i]));
        }
    }

    @Override
    public void itemsRemoved(MultiSelectEvent event) {
        // Default implementation: call itemRemoved for each item for backward compatibility
        for (int i = 0; i < event.getItems().length; i++) {
            itemRemoved(new MultiSelectEvent(event.getSource(), event.getItems()[i], event.getIndexes()[i]));
        }
    }

}
