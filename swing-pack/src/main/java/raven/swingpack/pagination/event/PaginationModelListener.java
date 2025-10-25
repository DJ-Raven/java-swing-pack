package raven.swingpack.pagination.event;

import java.util.EventListener;

/**
 * @author Raven
 */
public interface PaginationModelListener extends EventListener {

    void paginationModelChanged(PaginationModelEvent event);
}
