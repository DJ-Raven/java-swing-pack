package raven.swingpack.datetime.event;

import java.util.EventListener;

/**
 * @author Raven
 */
public interface DateTimeSelectionListener extends EventListener {

    void dateTimeSelected(DateTimeSelectionEvent event);
}
