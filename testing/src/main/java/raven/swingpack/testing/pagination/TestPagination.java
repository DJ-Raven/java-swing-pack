package raven.swingpack.testing.pagination;

import net.miginfocom.swing.MigLayout;
import raven.swingpack.JPagination;
import raven.swingpack.pagination.DefaultPaginationItemRenderer;
import raven.swingpack.pagination.Page;
import raven.swingpack.testing.BaseFrame;
import raven.swingpack.testing.utils.FlatLafStyleUtils;

import javax.swing.*;
import java.awt.*;

public class TestPagination extends BaseFrame {

    public TestPagination() {
        super("Pagination");
    }

    @Override
    protected Component createComponent() {
        JPanel panel = new JPanel(new MigLayout("wrap"));

        // default
        JPagination defaultPagination = new JPagination(10, 1, 50);

        // circle pagination
        JPagination circlePagination = new JPagination(10, 1, 50);
        circlePagination.setItemRenderer(new DefaultPaginationItemRenderer() {
            @Override
            public Component getPaginationItemRendererComponent(JPagination pagination, Page page, boolean isSelected, boolean isPressed, boolean hasFocus, int index) {
                super.getPaginationItemRendererComponent(pagination, page, isSelected, isPressed, hasFocus, index);
                FlatLafStyleUtils.appendStyle(this, "" +
                        "arc:999;");
                return this;
            }
        });

        // custom no border
        JPagination paginationNoBorder = new JPagination(10, 1, 50);
        paginationNoBorder.setItemGap(0);
        paginationNoBorder.setItemRenderer(new DefaultPaginationItemRenderer() {
            @Override
            public Component getPaginationItemRendererComponent(JPagination pagination, Page page, boolean isSelected, boolean isPressed, boolean hasFocus, int index) {
                super.getPaginationItemRendererComponent(pagination, page, isSelected, isPressed, hasFocus, index);
                FlatLafStyleUtils.appendStyle(this, "" +
                        "arc:0;" +
                        "borderWidth:0;" +
                        "focusWidth:0;");
                return this;
            }
        });

        // custom animation
        JPagination paginationAnimation = new PaginationAnimation(10, 1, 50);

        // loop animation
        JPagination paginationLoop = new PaginationAnimation(7, 1, 7);
        paginationLoop.setLoop(true);
        paginationLoop.setItemSize(new Dimension(15, 15));
        paginationLoop.setItemGap(5);
        paginationLoop.setItemRenderer(new AnimatedLoopItemRenderer(paginationLoop));

        // liquid animation
        JPagination paginationLiquid = new PaginationLiquid(10, 1, 50);

        // liquid animation loop
        JPagination paginationLiquidLoop = new PaginationLiquid(7, 1, 7);
        paginationLiquidLoop.setLoop(true);
        paginationLiquidLoop.setItemSize(new Dimension(15, 15));
        paginationLiquidLoop.setItemGap(5);
        paginationLiquidLoop.setItemRenderer(new AnimatedLoopItemRenderer(paginationLiquidLoop));

        // underline animation
        JPagination paginationUnderline = new PaginationUnderline(10, 1, 50);

        // expanding dots animation
        JPagination paginationExpandingDots = new PaginationExpandingDots(7, 1, 7);

        panel.add(new JLabel("Default:"));
        panel.add(defaultPagination, "gapy n 10");

        panel.add(new JLabel("Circle:"));
        panel.add(circlePagination, "gapy n 10");

        panel.add(new JLabel("No Border:"));
        panel.add(paginationNoBorder, "gapy n 10");

        panel.add(new JLabel("Custom with Animation:"));
        panel.add(paginationAnimation, "gapy n 10");

        panel.add(new JLabel("Liquid Animation:"));
        panel.add(paginationLiquid, "gapy n 10");

        panel.add(new JLabel("Underline Animation:"));
        panel.add(paginationUnderline, "gapy n 10");

        panel.add(new JLabel("Custom with Animation loop:"));
        panel.add(paginationLoop, "gapy n 10");

        panel.add(new JLabel("Liquid Animation loop:"));
        panel.add(paginationLiquidLoop, "gapy n 10");

        panel.add(new JLabel("Expanding Dots Animation:"));
        panel.add(paginationExpandingDots);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        return scrollPane;
    }

    public static void main(String[] args) {
        installLaf();
        EventQueue.invokeLater(() -> new TestPagination().setVisible(true));
    }
}
