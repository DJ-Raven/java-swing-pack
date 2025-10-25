package raven.swingpack.pagination;

import raven.swingpack.pagination.event.PaginationModelListener;

/**
 * @author Raven
 */
public interface PaginationModel {

    int getSelectedPage();

    void setSelectedPage(int selectedPage);

    int getPageSize();

    void setPageSize(int pageSize);

    void setPageRange(int selectedPage, int pageSize);

    boolean hasPrevious();

    boolean hasNext();

    Page[] getPagination();

    void addPaginationModelListener(PaginationModelListener listener);

    void removePaginationModelListener(PaginationModelListener listener);
}
