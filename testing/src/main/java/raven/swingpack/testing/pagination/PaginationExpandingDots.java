package raven.swingpack.testing.pagination;

import com.formdev.flatlaf.util.CubicBezierEasing;
import com.formdev.flatlaf.util.ScaledEmptyBorder;
import raven.swingpack.JPagination;
import raven.swingpack.pagination.Page;
import raven.swingpack.pagination.event.PaginationModelEvent;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Carousel style dot indicator: the selected dot expands into a wide pill while the old one
 * shrinks back, and the accent color cross fades between them.
 */
public class PaginationExpandingDots extends AbstractPaginationAnimation {

    /**
     * Extra width of the selected dot, as a multiple of the dot width.
     */
    private static final float EXPAND = 2f;

    private float[] fromWeights = new float[0];
    private int toIndex = -1;

    public PaginationExpandingDots() {
        init();
    }

    public PaginationExpandingDots(int maxItem) {
        super(maxItem);
        init();
    }

    public PaginationExpandingDots(int selectedPage, int pageSize) {
        super(selectedPage, pageSize);
        init();
    }

    public PaginationExpandingDots(int maxItem, int selectedPage, int pageSize) {
        super(maxItem, selectedPage, pageSize);
        init();
    }

    private void init() {
        getAnimator().setDuration(400);
        getAnimator().setInterpolator(CubicBezierEasing.STANDARD_EASING);
        setItemRenderer(new ItemRenderer("" +
                "background:$ProgressBar.background;" +
                "arc:999;" +
                "borderWidth:0;" +
                "focusWidth:0;") {
            @Override
            protected void customizeItem(JPagination pagination, Page page, boolean isSelected) {
                if (page.getType() == Page.Type.PAGE || page.getType() == Page.Type.ELLIPSIS) {
                    setText("");
                }
            }
        });
        setShowNavigationButton(false);
        setItemSize(new Dimension(10, 10));
        setItemGap(6);
        setBorder(new ScaledEmptyBorder(5, 5, 5, 5));
    }

    @Override
    protected void pageChanged(PaginationModelEvent event, Rectangle2D currentRec) {
        int size = getModel().getPagination().length;
        float[] weights = new float[size];
        if (currentRec != null) {
            // continue from the interrupted animation
            for (int i = 0; i < size; i++) {
                weights[i] = getAnimatedWeight(i);
            }
        } else if (event.getOldIndex() >= 0 && event.getOldIndex() < size) {
            weights[event.getOldIndex()] = 1f;
        }
        fromWeights = weights;
        toIndex = event.getNewIndex();
        super.pageChanged(event, currentRec);
    }

    /**
     * @return {@code 0} for a normal dot to {@code 1} for the selected dot
     */
    private float getWeight(int pageIndex) {
        if (isAnimating()) {
            return getAnimatedWeight(pageIndex);
        }
        return pageIndex == getSelectedIndex() ? 1f : 0f;
    }

    private float getAnimatedWeight(int pageIndex) {
        float from = pageIndex < fromWeights.length ? fromWeights[pageIndex] : 0f;
        float to = pageIndex == toIndex ? 1f : 0f;
        return from + (to - from) * getAnimate();
    }

    @Override
    protected int getItemWidth(int index) {
        int width = super.getItemWidth(index);
        int pageIndex = index - getNavigationOffset();
        if (pageIndex < 0 || pageIndex >= getModel().getPagination().length) {
            // navigation button
            return width;
        }
        return Math.round(width + width * EXPAND * getWeight(pageIndex));
    }

    @Override
    protected void paintIndicator(Graphics2D g2, Rectangle2D from, Rectangle2D to, float fraction) {
        int size = getModel().getPagination().length;
        for (int i = 0; i < size; i++) {
            float weight = getWeight(i);
            if (weight <= 0f) {
                continue;
            }
            Rectangle rec = getRectangleOfIndex(i);
            g2.setPaint(createIndicatorPaint(rec, weight, weight));
            g2.fill(new RoundRectangle2D.Double(rec.x, rec.y, rec.width, rec.height, rec.height, rec.height));
        }
    }
}
