package raven.swingpack.testing.pagination;

import com.formdev.flatlaf.util.CubicBezierEasing;
import com.formdev.flatlaf.util.ScaledEmptyBorder;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * Pagination with a liquid indicator: the leading edge stretches toward the new page first
 * and the trailing edge follows, and page text covered by the indicator is repainted in the
 * selected foreground color.
 */
public class PaginationLiquid extends AbstractPaginationAnimation {

    public PaginationLiquid() {
        init();
    }

    public PaginationLiquid(int maxItem) {
        super(maxItem);
        init();
    }

    public PaginationLiquid(int selectedPage, int pageSize) {
        super(selectedPage, pageSize);
        init();
    }

    public PaginationLiquid(int maxItem, int selectedPage, int pageSize) {
        super(maxItem, selectedPage, pageSize);
        init();
    }

    private void init() {
        // linear timing, easing is applied per edge in getCurrentRec()
        getAnimator().setDuration(450);
        setItemRenderer(new ItemRenderer("" +
                "background:$Panel.background;" +
                "arc:999;" +
                "borderWidth:0;" +
                "focusWidth:0;"));
        setItemGap(2);
        setBorder(new ScaledEmptyBorder(5, 5, 5, 5));
    }

    @Override
    protected void paintIndicator(Graphics2D g2, Rectangle2D from, Rectangle2D to, float fraction) {
        Rectangle2D rec = getCurrentRec(from, to, fraction);
        boolean moveRight = to.getCenterX() >= from.getCenterX();

        // 0 when resting, grows while the indicator is stretched
        double stretch = Math.max(0, rec.getWidth() - to.getWidth()) / rec.getHeight();

        // the head keeps its size, the tail shrinks like a drop being pulled apart
        double radius = rec.getHeight() / 2;
        double tailRadius = radius * (1 - Math.min(0.3, stretch * 0.15));
        double neckRadius = Math.min(tailRadius, radius * (1 - Math.min(0.45, stretch * 0.25)));
        Shape shape = moveRight
                ? createLiquidShape(rec, tailRadius, radius, neckRadius)
                : createLiquidShape(rec, radius, tailRadius, neckRadius);

        // gradient across the (stretched) indicator, the tail fades a little to show the direction
        float tailAlpha = (float) (1 - Math.min(0.35, stretch * 0.2));
        g2.setPaint(moveRight
                ? createIndicatorPaint(rec, tailAlpha, 1f)
                : createIndicatorPaint(rec, 1f, tailAlpha));
        g2.fill(shape);
        paintIndicatorText(g2, shape);
    }

    /**
     * Create two circles (left and right end) joined by a bridge that pinches to a neck in the middle.
     * With no stretch this is a regular pill.
     */
    private Shape createLiquidShape(Rectangle2D rec, double leftRadius, double rightRadius, double neckRadius) {
        // while the two end circles overlap, fade the tail shrink and the pinch out,
        // otherwise a notch appears where the circles meet
        double maxRadius = Math.max(leftRadius, rightRadius);
        double overlap = Math.max(0, Math.min(1, (rec.getWidth() - maxRadius * 2) / (maxRadius * 2)));
        leftRadius = maxRadius - (maxRadius - leftRadius) * overlap;
        rightRadius = maxRadius - (maxRadius - rightRadius) * overlap;
        neckRadius = maxRadius - (maxRadius - neckRadius) * overlap;

        double cy = rec.getCenterY();
        double leftCx = rec.getMinX() + leftRadius;
        double rightCx = rec.getMaxX() - rightRadius;
        if (rightCx < leftCx) {
            rightCx = leftCx;
        }
        double midX = (leftCx + rightCx) / 2;
        double d = (rightCx - leftCx) / 4;

        // bridge between the circle centers: left circle -> neck -> right circle
        Path2D bridge = new Path2D.Double();
        bridge.moveTo(leftCx, cy - leftRadius);
        bridge.curveTo(leftCx + d, cy - leftRadius, midX - d, cy - neckRadius, midX, cy - neckRadius);
        bridge.curveTo(midX + d, cy - neckRadius, rightCx - d, cy - rightRadius, rightCx, cy - rightRadius);
        bridge.lineTo(rightCx, cy + rightRadius);
        bridge.curveTo(rightCx - d, cy + rightRadius, midX + d, cy + neckRadius, midX, cy + neckRadius);
        bridge.curveTo(midX - d, cy + neckRadius, leftCx + d, cy + leftRadius, leftCx, cy + leftRadius);
        bridge.closePath();

        Area area = new Area(bridge);
        area.add(new Area(new Ellipse2D.Double(leftCx - leftRadius, cy - leftRadius, leftRadius * 2, leftRadius * 2)));
        area.add(new Area(new Ellipse2D.Double(rightCx - rightRadius, cy - rightRadius, rightRadius * 2, rightRadius * 2)));
        return area;
    }

    /**
     * Interpolate the left and right edges separately: the leading edge moves first
     * and the trailing edge catches up, so the indicator stretches then contracts.
     */
    @Override
    protected Rectangle2D getCurrentRec(Rectangle2D from, Rectangle2D to, float fraction) {
        boolean moveRight = to.getCenterX() >= from.getCenterX();
        // the leading edge overshoots the target a little and settles back
        float lead = easeOutBack(fraction / 0.65f);
        float trail = ease((fraction - 0.35f) / 0.65f);

        float leftFraction = moveRight ? trail : lead;
        float rightFraction = moveRight ? lead : trail;

        double left = from.getMinX() + (to.getMinX() - from.getMinX()) * leftFraction;
        double right = from.getMaxX() + (to.getMaxX() - from.getMaxX()) * rightFraction;
        return new Rectangle2D.Double(left, to.getY(), right - left, to.getHeight());
    }

    private float ease(float x) {
        x = Math.max(0f, Math.min(1f, x));
        return CubicBezierEasing.STANDARD_EASING.interpolate(x);
    }

    private float easeOutBack(float x) {
        x = Math.max(0f, Math.min(1f, x));
        float c1 = 1.2f;
        float c3 = c1 + 1;
        float t = x - 1;
        return 1 + c3 * t * t * t + c1 * t * t;
    }
}
