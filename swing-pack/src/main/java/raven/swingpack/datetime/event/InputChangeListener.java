package raven.swingpack.datetime.event;

import java.util.EventListener;

/**
 * @author Raven
 */
public interface InputChangeListener extends EventListener {

    void selectionChanged(InputChangeEvent event);

    void valueChanged(InputChangeEvent event);
}
