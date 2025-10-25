package raven.swingpack.multiselect.event;

import java.util.EventObject;

/**
 * @author Raven
 */
public class MultiSelectEvent extends EventObject {

    protected Object[] items;
    protected int[] indexes;

    public MultiSelectEvent(Object source, Object item, int index) {
        super(source);
        this.items = new Object[]{item};
        this.indexes = new int[]{index};
    }

    public MultiSelectEvent(Object source, Object[] items, int[] indexes) {
        super(source);
        this.items = items;
        this.indexes = indexes;
    }

    public Object[] getItems() {
        return items;
    }

    public Object getItem() {
        if (items.length == 0) {
            return null;
        }
        return items[0];
    }

    public int[] getIndexes() {
        return indexes;
    }

    public int getIndex() {
        if (indexes.length == 0) {
            return -1;
        }
        return indexes[0];
    }
}
