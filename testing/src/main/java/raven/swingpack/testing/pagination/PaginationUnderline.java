package raven.swingpack.testing.pagination;

import com.formdev.flatlaf.util.CubicBezierEasing;
import com.formdev.flatlaf.util.ScaledEmptyBorder;
import com.formdev.flatlaf.util.UIScale;
import raven.swingpack.JPagination;
import raven.swingpack.pagination.Page;
import raven.swingpack.testing.utils.FlatLafStyleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Tab style pagination: a thin underline slides to the selected page over a hairline track,
 * and the selected page text turns bold in the accent color once the underline arrives.
 */
public class PaginationUnderline extends AbstractPaginationAnimation {

    public PaginationUnderline() {
        init();
    }

    public PaginationUnderline(int maxItem) {
        super(maxItem);
        init();
    }

    public PaginationUnderline(int selectedPage, int pageSize) {
        super(selectedPage, pageSize);
        init();
    }

    public PaginationUnderline(int maxItem, int selectedPage, int pageSize) {
        super(maxItem, selectedPage, pageSize);
        init();
    }

    private void init() {
        getAnimator().setDuration(300);
        getAnimator().setInterpolator(CubicBezierEasing.STANDARD_EASING);
        setItemRenderer(new ItemRenderer("" +
                "background:$Panel.background;" +
                "arc:10;" +
                "borderWidth:0;" +
                "focusWidth:0;") {
            @Override
            protected void customizeItem(JPagination pagination, Page page, boolean isSelected) {
                if (isSelected && !isAnimating()) {
                    FlatLafStyleUtils.appendStyle(this, "" +
                            "foreground:$Component.accentColor;" +
                            "font:bold;");
                }
            }
        });
        setItemGap(4);
        setBorder(new ScaledEmptyBorder(5, 5, 5, 5));
    }

    @Override
    protected void paintIndicator(Graphics2D g2, Rectangle2D from, Rectangle2D to, float fraction) {
        float lineHeight = UIScale.scale(1f);
        float barHeight = UIScale.scale(3f);
        float barInset = UIScale.scale(4f);

        // track under all page items
        int pageCount = getModel().getPagination().length;
        if (pageCount > 0) {
            Rectangle first = getRectangleOfIndex(0);
            Rectangle last = getRectangleOfIndex(pageCount - 1);
            g2.setColor(UIManager.getColor("Component.borderColor"));
            g2.fill(new Rectangle2D.Float(first.x, (float) (first.getMaxY() - lineHeight), (float) (last.getMaxX() - first.x), lineHeight));
        }

        Rectangle2D rec = getCurrentRec(from, to, fraction);
        Rectangle2D bar = new Rectangle2D.Double(rec.getX() + barInset, rec.getMaxY() - barHeight,
                rec.getWidth() - barInset * 2, barHeight);
        g2.setPaint(createIndicatorPaint(bar));
        g2.fill(new RoundRectangle2D.Double(bar.getX(), bar.getY(), bar.getWidth(), bar.getHeight(), barHeight, barHeight));
    }
}
