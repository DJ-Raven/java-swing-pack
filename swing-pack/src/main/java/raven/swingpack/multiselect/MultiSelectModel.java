package raven.swingpack.multiselect;

import raven.swingpack.multiselect.event.MultiSelectEvent;
import raven.swingpack.multiselect.event.MultiSelectListener;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.util.Vector;

/**
 * @author Raven
 */
public class MultiSelectModel<E> {

    protected EventListenerList listenerList = new EventListenerList();
    private ComboBoxModel<E> model;
    private final Vector<Object> selectedObject;

    public MultiSelectModel(ComboBoxModel<E> model) {
        this.model = model;
        selectedObject = new Vector<>();
    }

    public ComboBoxModel<E> getModel() {
        return model;
    }

    public void setModel(ComboBoxModel<E> model) {
        this.model = model;
    }

    /**
     * PERFORMANCE OPTIMIZED: Single item operations (keeps animations)
     */
    public synchronized void addSelectedItem(Object object) {
        if (selectedObject.contains(object)) return;
        selectedObject.addElement(object);
        int index = selectedObject.indexOf(object);
        fireItemAdded(new MultiSelectEvent(this, object, index));
    }

    public synchronized void removeSelectedItem(Object object) {
        int index = selectedObject.indexOf(object);
        boolean act = selectedObject.removeElement(object);
        if (act) {
            fireItemRemoved(new MultiSelectEvent(this, object, index));
        }
    }


    /**
     * BATCH OPERATIONS: For bulk data - maximum performance
     */
    public synchronized void addSelectedItems(Object[] objects) {
        if (objects == null || objects.length == 0) return;

        Vector<Object> addedObjects = new Vector<>();
        Vector<Integer> addedIndexes = new Vector<>();

        for (Object object : objects) {
            if (selectedObject.contains(object)) continue;
            selectedObject.addElement(object);
            int index = selectedObject.indexOf(object);
            addedObjects.addElement(object);
            addedIndexes.addElement(index);
        }

        if (!addedObjects.isEmpty()) {
            int[] indexes = new int[addedIndexes.size()];
            for (int i = 0; i < addedIndexes.size(); i++) {
                indexes[i] = addedIndexes.get(i);
            }
            fireItemsAdded(new MultiSelectEvent(this, addedObjects.toArray(), indexes));
        }
    }




    /**
     * SILENT BATCH: No UI updates - fastest performance
     * Use for initial data loading or bulk operations
     */
    public synchronized void addSelectedItemsSilent(Object[] objects) {
        if (objects == null || objects.length == 0) return;

        for (Object object : objects) {
            if (!selectedObject.contains(object)) {
                selectedObject.addElement(object);
            }
        }
        // No events fired - prevents UI repaint storms
    }

    public synchronized void removeSelectedItems(Object[] objects) {
        Vector<Object> itemRemove = new Vector<>();
        Vector<Integer> indexRemove = new Vector<>();

        for (int i = objects.length - 1; i >= 0; i--) {
            Object item = objects[i];
            int index = selectedObject.indexOf(item);
            if (selectedObject.removeElement(item)) {
                indexRemove.insertElementAt(index, 0);
                itemRemove.insertElementAt(item, 0);
            }
        }

        if (!itemRemove.isEmpty()) {
            int[] indexes = new int[indexRemove.size()];
            for (int i = 0; i < indexRemove.size(); i++) {
                indexes[i] = indexRemove.get(i);
            }
            fireItemsRemoved(new MultiSelectEvent(this, itemRemove.toArray(), indexes));
        }
    }

    /**
     * FORCE OPERATIONS: When you need to bypass validation
     */
    public synchronized void clearSelectedItemsForce() {
        selectedObject.clear();
        // No events fired - for testing/performance
    }

    // Silent event methods that don't trigger UI updates
    protected void fireItemsAddedSilent(MultiSelectEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == MultiSelectListener.class) {
                ((MultiSelectListener) listeners[i + 1]).itemsAddedSilent(event);
            }
        }
    }

    protected void fireItemsRemovedSilent(MultiSelectEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == MultiSelectListener.class) {
                ((MultiSelectListener) listeners[i + 1]).itemsRemovedSilent(event);
            }
        }
    }


    // Add to MultiSelectModel.java
    public synchronized void addItems(Object[] objects) {
        if (objects == null || objects.length == 0) return;

        // Add items to the underlying ComboBoxModel
        if (model instanceof DefaultComboBoxModel) {
            DefaultComboBoxModel<E> defaultModel = (DefaultComboBoxModel<E>) model;
            for (Object object : objects) {
                defaultModel.addElement((E) object);
            }
        }
        // Note: For other model types, you might need different implementation
    }

    public synchronized void setSelectedItems(Object[] objects) {
        // Clear current selection and set new ones in one operation
        Object[] currentItems = getSelectedItems();
        int[] currentIndexes = new int[currentItems.length];
        for (int i = 0; i < currentItems.length; i++) {
            currentIndexes[i] = i;
        }

        selectedObject.clear();

        // Add new items
        Vector<Object> addedObjects = new Vector<>();
        Vector<Integer> addedIndexes = new Vector<>();

        for (Object object : objects) {
            if (!selectedObject.contains(object)) {
                selectedObject.addElement(object);
                int index = selectedObject.indexOf(object);
                addedObjects.addElement(object);
                addedIndexes.addElement(index);
            }
        }

        // Fire events
        if (currentItems.length > 0) {
            fireItemsRemoved(new MultiSelectEvent(this, currentItems, currentIndexes));
        }
        if (!addedObjects.isEmpty()) {
            int[] indexes = new int[addedIndexes.size()];
            for (int i = 0; i < addedIndexes.size(); i++) {
                indexes[i] = addedIndexes.get(i);
            }
            fireItemsAdded(new MultiSelectEvent(this, addedObjects.toArray(), indexes));
        }
    }

    // New batch event methods
    protected void fireItemsAdded(MultiSelectEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == MultiSelectListener.class) {
                ((MultiSelectListener) listeners[i + 1]).itemsAdded(event);
            }
        }
    }

    protected void fireItemsRemoved(MultiSelectEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == MultiSelectListener.class) {
                ((MultiSelectListener) listeners[i + 1]).itemsRemoved(event);
            }
        }
    }





    public void removeSelectedItemAt(int index) {
        Object object = selectedObject.get(index);
        removeSelectedItem(object);
    }

    public synchronized void clearSelectedItems() {
        if (getSelectedItemCount() > 0) {
            Object[] items = getSelectedItems();
            int[] indexes = new int[items.length];
            for (int i = 0; i < items.length; i++) {
                indexes[i] = i;
            }
            selectedObject.clear();
            fireItemRemoved(new MultiSelectEvent(this, items, indexes));
        }
    }

    public Object[] getSelectedItems() {
        return selectedObject.toArray();
    }

    public Object getSelectedItemAt(int index) {
        return selectedObject.get(index);
    }

    public int getSelectedItemIndex(Object item) {
        return selectedObject.indexOf(item);
    }

    public int getSelectedItemCount() {
        return selectedObject.size();
    }

    public boolean isSelectedItem(Object object) {
        return selectedObject.contains(object);
    }

    public void addEventListener(MultiSelectListener listener) {
        listenerList.add(MultiSelectListener.class, listener);
    }

    public void removeEventListener(MultiSelectListener listener) {
        listenerList.remove(MultiSelectListener.class, listener);
    }

    protected void fireItemAdded(MultiSelectEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == MultiSelectListener.class) {
                ((MultiSelectListener) listeners[i + 1]).itemAdded(event);
            }
        }
    }

    protected void fireItemRemoved(MultiSelectEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == MultiSelectListener.class) {
                ((MultiSelectListener) listeners[i + 1]).itemRemoved(event);
            }
        }
    }

    protected void fireItemSelected(int index) {
        Object object = selectedObject.get(index);
        if (object != null) {
            fireItemSelected(new MultiSelectEvent(this, object, index));
        }
    }

    protected void fireOverflowSelected(int itemCount) {
        Object[] items = getItemTo(itemCount);
        int[] indexes = new int[items.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        fireOverflowSelected(new MultiSelectEvent(this, items, indexes));
    }

    protected Object[] getItemTo(int itemCount) {
        if (itemCount == 0) {
            return null;
        }
        Object[] items = new Object[itemCount];
        for (int i = 0; i < getSelectedItemCount(); i++) {
            items[i] = selectedObject.get(i);
            if (i == itemCount - 1) {
                break;
            }
        }
        return items;
    }

    protected void fireItemSelected(MultiSelectEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == MultiSelectListener.class) {
                ((MultiSelectListener) listeners[i + 1]).itemSelected(event);
            }
        }
    }

    protected void fireOverflowSelected(MultiSelectEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == MultiSelectListener.class) {
                ((MultiSelectListener) listeners[i + 1]).overflowSelected(event);
            }
        }
    }
}
