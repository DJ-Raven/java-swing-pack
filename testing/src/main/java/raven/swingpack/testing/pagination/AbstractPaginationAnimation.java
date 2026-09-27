package raven.swingpack.testing.pagination;

import com.formdev.flatlaf.util.Animator;
import raven.swingpack.JPagination;
import raven.swingpack.pagination.DefaultPaginationItemRenderer;
import raven.swingpack.pagination.Page;
import raven.swingpack.pagination.PaginationModel;
import raven.swingpack.pagination.event.PaginationModelEvent;
import raven.swingpack.testing.utils.FlatLafStyleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Base class for paginations that paint an animated selection indicator on top of the items.
 * <p>
 * Subclasses implement {@link #paintIndicator(Graphics2D, Rectangle2D, Rectangle2D, float)}, and may
 * override {@link #getItemWidth(int)} to lay out items with different widths.
 */
public abstract class AbstractPaginationAnimation extends JPagination {

    /**
     * Renderer index used to paint the page text on top of the indicator.
     */
    protected static final int INDICATOR_INDEX = -2;

    private Animator animator;
    private float animate;

    protected Rectangle2D fromRec;
    protected Rectangle2D toRec;

    private boolean colorSet;
    private Color color1;
    private Color color2;

    public AbstractPaginationAnimation() {
        init();
    }

    public AbstractPaginationAnimation(int maxItem) {
        super(maxItem);
        init();
    }

    public AbstractPaginationAnimation(int selectedPage, int pageSize) {
        super(selectedPage, pageSize);
        init();
    }

    public AbstractPaginationAnimation(int maxItem, int selectedPage, int pageSize) {
        super(maxItem, selectedPage, pageSize);
        init();
    }

    public AbstractPaginationAnimation(PaginationModel model) {
        super(model);
        init();
    }

    private void init() {
        animator = new Animator(400, new Animator.TimingTarget() {
            @Override
            public void timingEvent(float fraction) {
                animate = fraction;
                repaint();
            }

            @Override
            public void end() {
                // repaint the idle state
                repaint();
            }
        });
    }

    /**
     * Paint the selection indicator.
     *
     * @param from     indicator bounds of the old page
     * @param to       indicator bounds of the new page
     * @param fraction animation fraction, {@code 1} when not animating (then {@code from == to})
     */
    protected abstract void paintIndicator(Graphics2D g2, Rectangle2D from, Rectangle2D to, float fraction);

    protected Animator getAnimator() {
        return animator;
    }

    protected boolean isAnimating() {
        return animator != null && animator.isRunning();
    }

    protected float getAnimate() {
        return animate;
    }

    /**
     * Set the indicator gradient colors. If both colors are {@code null} the default colors
     * {@code Button.default.background} and {@code Component.accentColor} are used.
     * If only one color is set, or both are equal, the indicator uses a solid color.
     */
    public void setColor(Color color1, Color color2) {
        colorSet = (color1 != null || color2 != null);
        if (color1 == null) color1 = color2;
        if (color2 == null) color2 = color1;
        this.color1 = color1;
        this.color2 = color2;
        repaint();
    }

    public Color getColor1() {
        return colorSet ? color1 : UIManager.getColor("Button.default.background");
    }

    public Color getColor2() {
        return colorSet ? color2 : UIManager.getColor("Component.accentColor");
    }

    /**
     * Create the indicator paint: a diagonal gradient from color1 to color2 across the bounds,
     * or a solid color if both colors are equal.
     */
    protected Paint createIndicatorPaint(Rectangle2D rec) {
        return createIndicatorPaint(rec, 1f, 1f);
    }

    /**
     * @param alpha1 opacity of color1 (left/top side)
     * @param alpha2 opacity of color2 (right/bottom side)
     */
    protected Paint createIndicatorPaint(Rectangle2D rec, float alpha1, float alpha2) {
        Color c1 = getColor1();
        Color c2 = getColor2();
        if (c1 == null) c1 = c2;
        if (c2 == null) c2 = c1;
        if (c1 == null) c1 = c2 = Color.GRAY;
        c1 = withAlpha(c1, alpha1);
        c2 = withAlpha(c2, alpha2);
        if (c1.equals(c2)) {
            return c1;
        }
        return new GradientPaint((float) rec.getMinX(), (float) rec.getMinY(), c1,
                (float) rec.getMaxX(), (float) rec.getMaxY(), c2);
    }

    private static Color withAlpha(Color color, float alpha) {
        if (alpha >= 1f) {
            return color;
        }
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.round(color.getAlpha() * Math.max(0f, alpha)));
    }

    @Override
    public void paginationModelChanged(PaginationModelEvent event) {
        super.paginationModelChanged(event);
        if (animator == null || !event.isPageChanged()) {
            return;
        }
        Rectangle2D currentRec = animator.isRunning() && fromRec != null && toRec != null
                ? getCurrentRec(fromRec, toRec, animate) : null;
        animator.stop();
        pageChanged(event, currentRec);
        animate = 0f;
        animator.start();
    }

    /**
     * Called when the selected page changed, before the animation starts.
     *
     * @param currentRec the indicator bounds if the previous animation was interrupted, otherwise {@code null}
     */
    protected void pageChanged(PaginationModelEvent event, Rectangle2D currentRec) {
        toRec = event.getNewIndex() >= 0 ? getRectangleOfIndex(event.getNewIndex()) : null;
        if (currentRec != null) {
            fromRec = currentRec;
        } else {
            fromRec = event.getOldIndex() >= 0 ? getRectangleOfIndex(event.getOldIndex()) : toRec;
        }
    }

    /**
     * The indicator bounds at the given animation fraction, used to continue from an interrupted animation.
     */
    protected Rectangle2D getCurrentRec(Rectangle2D from, Rectangle2D to, float fraction) {
        double x = from.getX() + (to.getX() - from.getX()) * fraction;
        double width = from.getWidth() + (to.getWidth() - from.getWidth()) * fraction;
        return new Rectangle2D.Double(x, to.getY(), width, to.getHeight());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getModel().getPageSize() == 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (animator.isRunning() && fromRec != null && toRec != null) {
            paintIndicator(g2, fromRec, toRec, animate);
        } else {
            int index = getSelectedIndex();
            if (index >= 0) {
                Rectangle rec = getRectangleOfIndex(index);
                paintIndicator(g2, rec, rec, 1f);
            }
        }
        rendererPane.removeAll();
        g2.dispose();
    }

    /**
     * Paint the text of every page item inside the clip shape using the indicator (selected) foreground.
     */
    protected void paintIndicatorText(Graphics2D g, Shape clip) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.clip(clip);
        Page[] pages = getModel().getPagination();
        int offset = getNavigationOffset();
        for (int i = 0; i < pages.length; i++) {
            Page page = pages[i];
            if (page.getType() != Page.Type.PAGE && page.getType() != Page.Type.ELLIPSIS) {
                continue;
            }
            Rectangle itemRec = rectangleAt(i + offset);
            if (clip.intersects(itemRec)) {
                paintIndicatorText(g2, page, itemRec);
            }
        }
        g2.dispose();
    }

    protected void paintIndicatorText(Graphics2D g2, Page page, Rectangle rec) {
        Component com = getItemRenderer().getPaginationItemRendererComponent(this, page, false, false, false, INDICATOR_INDEX);
        rendererPane.paintComponent(g2, com, this, rec);
    }

    /**
     * @return index of the selected page in {@code getModel().getPagination()}, or {@code -1}
     */
    protected int getSelectedIndex() {
        Page[] pages = getModel().getPagination();
        int selectedPage = getSelectedPage();
        for (int i = 0; i < pages.length; i++) {
            if (pages[i].getType() == Page.Type.PAGE && pages[i].getValue() == selectedPage) {
                return i;
            }
        }
        return -1;
    }

    protected int getNavigationOffset() {
        return checkCreateNavigationButton(getModel().getPagination().length) ? 1 : 0;
    }

    /**
     * @param index index in {@code getModel().getPagination()}
     */
    protected Rectangle getRectangleOfIndex(int index) {
        return rectangleAt(index + getNavigationOffset());
    }

    protected int getItemCount() {
        int size = getModel().getPagination().length;
        return size + (checkCreateNavigationButton(size) ? 2 : 0);
    }

    /**
     * @param index item index, including the navigation buttons
     */
    protected int getItemWidth(int index) {
        return scale(getItemSize().width);
    }

    @Override
    protected Rectangle rectangleAt(int index) {
        Insets insets = getInsets();
        int gap = scale(getItemGap());
        int x = insets.left;
        for (int i = 0; i < index; i++) {
            x += getItemWidth(i) + gap;
        }
        return new Rectangle(x, insets.top, getItemWidth(index), scale(getItemSize().height));
    }

    @Override
    protected int getIndexAt(int x, int y) {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            if (rectangleAt(i).contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Dimension getPreferredSize() {
        int count = getItemCount();
        if (isPreferredSizeSet() || count == 0) {
            return super.getPreferredSize();
        }
        Insets insets = getInsets();
        int width = insets.left + insets.right + scale(getItemGap()) * (count - 1);
        for (int i = 0; i < count; i++) {
            width += getItemWidth(i);
        }
        int height = insets.top + insets.bottom + scale(getItemSize().height);
        return new Dimension(width, height);
    }

    /**
     * Item renderer that paints page text only (no background) for {@link #INDICATOR_INDEX},
     * and applies the given FlatLaf style to the other items.
     */
    protected static class ItemRenderer extends DefaultPaginationItemRenderer {

        private final String style;
        protected int index;

        public ItemRenderer(String style) {
            this.style = style;
        }

        @Override
        public Component getPaginationItemRendererComponent(JPagination pagination, Page page, boolean isSelected, boolean isPressed, boolean hasFocus, int index) {
            this.index = index;
            super.getPaginationItemRendererComponent(pagination, page, isSelected, isPressed, hasFocus, index);
            if (index == INDICATOR_INDEX) {
                setContentAreaFilled(false);
            } else {
                setContentAreaFilled(true);
                if (style != null) {
                    FlatLafStyleUtils.appendStyle(this, style);
                }
                customizeItem(pagination, page, isSelected);
            }
            return this;
        }

        /**
         * Hook to customize a normal (non indicator) item after the style is applied.
         */
        protected void customizeItem(JPagination pagination, Page page, boolean isSelected) {
        }

        @Override
        public boolean isDefaultButton() {
            return index == INDICATOR_INDEX;
        }
    }
}
