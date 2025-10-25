package raven.swingpack.pagination;

import com.formdev.flatlaf.FlatClientProperties;
import raven.swingpack.JPagination;

import javax.swing.*;
import java.awt.*;

/**
 * @author Raven
 */
public class DefaultPaginationItemRenderer extends JButton implements PaginationItemRenderer {

    protected boolean isSelected;

    @Override
    public Component getPaginationItemRendererComponent(JPagination pagination, Page page, boolean isSelected, boolean isPressed, boolean hasFocus, int index) {
        this.isSelected = isSelected;
        setEnabled(true);
        if (page.getType() == Page.Type.PAGE) {
            setIcon(null);
            setText(page.getValue() + "");
        } else if (page.getType() == Page.Type.ELLIPSIS) {
            setIcon(null);
            setText("...");
        } else if (page.getType() == Page.Type.PREVIOUS) {
            setIcon(pagination.getPreviousIcon());
            setText("");
            if (!pagination.hasPrevious() && !pagination.isNavigationAlwaysEnabled() && !pagination.isLoop()) {
                setEnabled(false);
            }
        } else if (page.getType() == Page.Type.NEXT) {
            setIcon(pagination.getNextIcon());
            setText("");
            if (!pagination.hasNext() && !pagination.isNavigationAlwaysEnabled() && !pagination.isLoop()) {
                setEnabled(false);
            }
        }

        putClientProperty(FlatClientProperties.STYLE, "margin:3,3,3,3;");
        getModel().setRollover(hasFocus);
        getModel().setPressed(isPressed);
        return this;
    }

    @Override
    public boolean isDefaultButton() {
        return isSelected;
    }
}
