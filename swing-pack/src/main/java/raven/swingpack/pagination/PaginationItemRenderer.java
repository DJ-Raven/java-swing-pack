package raven.swingpack.pagination;

import raven.swingpack.JPagination;

import java.awt.*;

/**
 * @author Raven
 */
public interface PaginationItemRenderer {

    Component getPaginationItemRendererComponent(JPagination pagination, Page page, boolean isSelected, boolean isPressed, boolean hasFocus, int index);
}
