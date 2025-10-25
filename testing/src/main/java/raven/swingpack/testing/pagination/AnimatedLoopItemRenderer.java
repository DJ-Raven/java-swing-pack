package raven.swingpack.testing.pagination;

import raven.swingpack.JPagination;
import raven.swingpack.pagination.DefaultPaginationItemRenderer;
import raven.swingpack.pagination.Page;
import raven.swingpack.pagination.PaginationItemRenderer;
import raven.swingpack.testing.utils.FlatLafStyleUtils;

import javax.swing.*;
import java.awt.*;

public class AnimatedLoopItemRenderer extends DefaultPaginationItemRenderer {

    private final PaginationItemRenderer oldRenderer;

    public AnimatedLoopItemRenderer(JPagination pagination) {
        oldRenderer = pagination.getItemRenderer();
    }

    @Override
    public Component getPaginationItemRendererComponent(JPagination pagination, Page page, boolean isSelected, boolean isPressed, boolean hasFocus, int index) {
        JButton button = (JButton) oldRenderer.getPaginationItemRendererComponent(pagination, page, isSelected, isPressed, hasFocus, index);

        button.setText("");
        FlatLafStyleUtils.appendStyle(button, "" +
                "background:$ProgressBar.background;" +
                "arc:999;");
        return button;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (oldRenderer instanceof JComponent) {
            SwingUtilities.updateComponentTreeUI((JComponent) oldRenderer);
        }
    }
}
