package raven.swingpack.multiselect;

import com.formdev.flatlaf.util.UIScale;
import raven.swingpack.JMultiSelectComboBox;
import raven.swingpack.util.SwingPackUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Raven
 */
public class MultiSelectView extends JComponent implements Scrollable {

    private final JMultiSelectComboBox<?> multiSelect;
    private final WrapLayoutSize wrapLayout;
    private final List<ShapeWithIndex> listRectangle;
    private final CellRendererPane rendererPane;

    private int pressedIndex = -2;
    private int focusIndex = -2;
    private int removablePressedIndex = -2;
    private int removableFocusIndex = -2;

    private final boolean forOverflow;

    public MultiSelectView(JMultiSelectComboBox<?> multiSelect) {
        this(multiSelect, false);
    }

    public MultiSelectView(JMultiSelectComboBox<?> multiSelect, boolean forOverflow) {
        this.multiSelect = multiSelect;
        this.wrapLayout = new WrapLayoutSize();
        this.listRectangle = new ArrayList<>();
        this.rendererPane = new CellRendererPane();
        this.forOverflow = forOverflow;
        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    multiSelect.grabFocus();
                    onPressed(e.getPoint());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Point point = e.getPoint();
                    int index = getRemovableIndexAt(point);
                    boolean checkPressed = true;
                    if (multiSelect.isShowItemRemovableIcon()) {
                        if (index >= 0) {
                            if (index == removableFocusIndex) {
                                multiSelect.removeSelectedItemAt(index);
                            }
                            checkPressed = false;
                        } else {
                            checkPressed = removableFocusIndex < 0;
                        }
                        removableFocusIndex = index;
                    }
                    index = getIndexAt(point);
                    if (index != -2) {
                        if (checkPressed) {
                            if (index == pressedIndex) {
                                if (index == -1) {
                                    int overflowCount = getOverflowItemCount();
                                    if (overflowCount > 0) {
                                        multiSelect.getMultiSelectModel().fireOverflowSelected(overflowCount);
                                    }
                                } else {
                                    multiSelect.getMultiSelectModel().fireItemSelected(index);
                                }
                            }
                        }
                    }

                    focusIndex = -2;
                    pressedIndex = -2;
                    removableFocusIndex = -2;
                    removablePressedIndex = -2;

                    SwingUtilities.invokeLater(() -> {
                        onFocus(point);
                        repaint();
                    });
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                onFocus(e.getPoint());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boolean paint = false;
                if (pressedIndex == -2) {
                    focusIndex = -2;
                    paint = true;
                }
                if (removablePressedIndex == -2) {
                    removableFocusIndex = -2;
                    paint = true;
                }
                if (paint) {
                    repaint();
                }
            }

            private void onFocus(Point point) {
                int index = getIndexAt(point);
                boolean paint = false;
                if (focusIndex != index) {
                    focusIndex = index;
                    paint = true;
                }
                if (multiSelect.isShowItemRemovableIcon()) {
                    if (index >= 0) {
                        index = getRemovableIndexAt(point);
                        if (index != removableFocusIndex) {
                            removableFocusIndex = index;
                            paint = true;
                        }
                    }
                } else {
                    removableFocusIndex = -2;
                }
                if (paint) {
                    repaint();
                }
            }

            private void onPressed(Point point) {
                int index = getIndexAt(point);
                boolean paint = false;
                if (pressedIndex != index) {
                    pressedIndex = index;
                    paint = true;
                }
                if (index >= 0) {
                    index = getRemovableIndexAt(point);
                    if (removablePressedIndex != index) {
                        removablePressedIndex = index;
                        paint = true;
                    }
                }
                if (paint) {
                    repaint();
                }
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private int getIndexAt(Point point) {
        for (ShapeWithIndex s : listRectangle) {
            if (s.rectangle.contains(point)) {
                return s.index;
            }
        }
        return -2;
    }

    private int getRemovableIndexAt(Point point) {
        for (ShapeWithIndex s : listRectangle) {
            if (s.removableRectangle != null && s.removableRectangle.contains(point)) {
                return s.index;
            }
        }
        return -2;
    }

    public int getOverflowItemCount() {
        if (listRectangle.isEmpty()) {
            return 0;
        }
        if (listRectangle.get(0).index != -1) {
            return 0;
        }
        if (listRectangle.size() == 1) {
            return multiSelect.getSelectedItemCount();
        }
        return listRectangle.get(1).index;
    }

    public Object[] getOverflowItems() {
        return multiSelect.getMultiSelectModel().getItemTo(getOverflowItemCount());
    }

    public Rectangle getRectangleAt(int index, boolean includeSpacing) {
        if (index < -1 || index >= listRectangle.size()) {
            return null;
        }
        for (ShapeWithIndex s : listRectangle) {

            if (s.index == index) {
                Rectangle rec = new Rectangle(s.rectangle);
                if (includeSpacing) {
                    int gap = scale(multiSelect.getItemGap());
                    rec.grow(gap, gap);
                }
                return rec;
            }
        }
        return null;
    }

    public void scrollTo(int index) {
        if (multiSelect.getDisplayMode() != JMultiSelectComboBox.DisplayMode.WRAP_SCROLL) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            Rectangle rec = getRectangleAt(index, true);
            if (rec != null) {
                scrollRectToVisible(rec);
            }
        });
    }

    private int getItemAlignment() {
        int alignment = multiSelect.getItemAlignment();
        boolean ltr = multiSelect.getComponentOrientation().isLeftToRight();
        return alignment == SwingConstants.LEADING ? (ltr ? SwingConstants.LEFT : SwingConstants.RIGHT)
                : alignment == SwingConstants.TRAILING ? (ltr ? SwingConstants.RIGHT : SwingConstants.LEFT)
                : alignment;
    }

    private Object[] getItems() {
        if (forOverflow) {
            return multiSelect.getOverflowItems();
        }
        return multiSelect.getSelectedItems();
    }

    @Override
    public Dimension getPreferredSize() {
        if (forOverflow || multiSelect.getDisplayMode() == JMultiSelectComboBox.DisplayMode.WRAP_SCROLL) {
            Object[] items = getItems();
            if (items == null || items.length == 0) {
                return getMinimumLayoutSize();
            }
            Insets insets = scale(multiSelect.getItemContainerInsets());
            int gap = scale(multiSelect.getItemGap());
            wrapLayout.init(getItemAlignment(), insets, getWidth(), getHeight(), gap);
            for (int i = 0; i < items.length; i++) {
                Component com = multiSelect.getItemRenderer().getMultiSelectItemRendererComponent(multiSelect, items[i], pressedIndex == i, focusIndex == i, removableFocusIndex == i, i);
                Dimension size = com.getPreferredSize();
                wrapLayout.add(size);
            }
            return wrapLayout.getMaxSize();
        }
        return getMinimumLayoutSize();
    }

    public Dimension getMinimumLayoutSize() {
        Insets insets = scale(multiSelect.getItemContainerInsets());
        Component com = multiSelect.getItemRenderer().getMultiSelectItemRendererComponent(multiSelect, 0, false, false, false, -1);
        Dimension size = com.getPreferredSize();
        int row = getRow();
        if (row > 0) {
            int gap = scale(multiSelect.getItemGap());
            size.height = size.height * row + (row > 1 ? (row - 1) * gap : 0);
        }
        size.width += insets.left + insets.right;
        size.height += insets.top + insets.bottom;
        return size;
    }

    public int getRow() {
        return forOverflow ? multiSelect.getOverflowPopupItemRow() : multiSelect.getRow();
    }

    public Point getOverflowPopupLocation() {
        Component com = multiSelect.getItemRenderer().getMultiSelectItemRendererComponent(multiSelect, 0, false, false, false, -1);
        Dimension size = com.getPreferredSize();
        boolean isLeft = getItemAlignment() == SwingConstants.LEFT;
        Insets insets = scale(multiSelect.getItemContainerInsets());
        int x = isLeft ? insets.left : (getWidth() - insets.right);
        int y = insets.top + size.height;
        if (!isLeft) {
            x -= scale(multiSelect.getOverflowPopupSize().width);
        }
        return new Point(x, y);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintImpl(g);
    }

    private void paintImpl(Graphics g) {
        Insets insets = scale(multiSelect.getItemContainerInsets());
        int width = getWidth() - (insets.left + insets.right);
        int height = getHeight() - (insets.top + insets.bottom);
        if (width == 0 || height == 0) {
            listRectangle.clear();
            return;
        }
        int gap = scale(multiSelect.getItemGap());
        Object[] selectedItem = getItems();
        Object[] items = getItemDisplay(selectedItem, insets, gap);
        if (items == null) {
            return;
        }
        int diff = selectedItem.length - items.length;
        Shape clip = g.getClip();
        listRectangle.clear();
        wrapLayout.init(getItemAlignment(), insets, getWidth(), getHeight(), gap);
        int index = diff > 0 ? -1 : 0;
        int itemIndex = diff > 0 ? diff - 1 : 0;
        for (int i = index; i < items.length; i++) {
            Object value = index == -1 ? diff : items[index];
            int rendererIndex = index == -1 ? -1 : itemIndex;
            Component com = multiSelect.getItemRenderer().getMultiSelectItemRendererComponent(multiSelect, value, pressedIndex == rendererIndex, focusIndex == rendererIndex, removableFocusIndex == rendererIndex, rendererIndex);
            Dimension size = com.getPreferredSize();
            Rectangle rec = wrapLayout.add(size);
            Rectangle removableRec = null;
            if (index >= 0 && multiSelect.isShowItemRemovableIcon() && multiSelect.isItemRemovable(value)) {
                removableRec = multiSelect.getRemovableIcon() == null ? null :
                        multiSelect.getRemovableIcon().getIconRectangle(multiSelect, com, rec.width, rec.height);
                if (removableRec != null) {
                    removableRec.x += rec.x;
                    removableRec.y += rec.y;
                }
            }
            listRectangle.add(new ShapeWithIndex(rec, removableRec, rendererIndex));
            if (clip == null || clip.intersects(rec)) {
                if (!multiSelect.isNoVisualPadding()) {
                    SwingPackUtils.applyVisualPadding(com, rec);
                }
                rendererPane.paintComponent(g, com, this, rec);
            }
            index++;
            itemIndex++;
        }
        rendererPane.removeAll();
    }

    private Object[] getItemDisplay(Object[] items, Insets insets, int gap) {
        if (forOverflow) {
            return multiSelect.getOverflowItems();
        }

        if (multiSelect.getDisplayMode() == JMultiSelectComboBox.DisplayMode.WRAP_SCROLL) {
            return items;
        }
        List<Object> display = new ArrayList<>();
        wrapLayout.init(getItemAlignment(), insets, getWidth(), getHeight(), gap);
        for (int i = items.length - 1; i >= 0; i--) {
            Object item = items[i];
            Component com = multiSelect.getItemRenderer().getMultiSelectItemRendererComponent(multiSelect, item, false, false, false, i);
            Dimension size = com.getPreferredSize();
            Rectangle rec = wrapLayout.add(size);
            boolean isOverflow = wrapLayout.isOverflow(rec);
            if (wrapLayout.getRow() != 0 && isOverflow) {
                wrapLayout.removeLast();
                break;
            }
            display.add(0, item);
        }
        int diff = items.length - display.size();
        if (diff > 0) {
            // check for overflow label space
            while (!display.isEmpty()) {
                Component com = multiSelect.getItemRenderer().getMultiSelectItemRendererComponent(multiSelect, diff, false, false, false, -1);
                Dimension size = com.getPreferredSize();
                Rectangle rec = wrapLayout.addTemp(size);
                if (wrapLayout.isOverflow(rec)) {
                    display.remove(0);
                    wrapLayout.removeLast();
                    diff++;
                } else {
                    break;
                }
            }
        }
        return display.toArray();
    }

    protected int scale(int value) {
        return UIScale.scale(value);
    }

    protected Insets scale(Insets insets) {
        return UIScale.scale(insets);
    }

    private WrapLayoutSize.RectangleRowColumn locationToRectangle(Point point) {
        return wrapLayout.getRectangleAtPoint(point.x, point.y);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return null;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return calculateScrollIncrement(visibleRect, orientation, direction);
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return calculateScrollIncrement(visibleRect, orientation, direction);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    private int calculateScrollIncrement(Rectangle visibleRect, int orientation, int direction) {
        WrapLayoutSize.RectangleRowColumn rec = locationToRectangle(visibleRect.getLocation());
        if (rec == null) {
            return 0;
        }
        Rectangle r = rec.rectangle;
        int row = rec.row;
        int gap = scale(multiSelect.getItemGap());
        int top = scale(multiSelect.getItemContainerInsets().top);
        if (orientation == SwingConstants.VERTICAL) {
            if (direction > 0) {
                // scroll down
                return r.height - (visibleRect.y - r.y);
            } else {
                // scroll up
                if ((r.y == visibleRect.y + top) && (row == 0)) {
                    return 0;
                }
                if (r.y == visibleRect.y + gap) {
                    Point loc = r.getLocation();
                    loc.y -= gap + 1;
                    WrapLayoutSize.RectangleRowColumn prevRec = locationToRectangle(loc);
                    if (prevRec == null) {
                        return 0;
                    }
                    row = prevRec.row;
                    if (prevRec.rectangle.y >= r.y) {
                        return 0;
                    }
                    return prevRec.rectangle.height + (row == 0 ? top : gap);
                }
                return visibleRect.y - r.y + (row == 0 ? top : gap);
            }
        }
        return 0;
    }

    private static class ShapeWithIndex {

        public ShapeWithIndex(Rectangle rectangle, Rectangle removableRectangle, int index) {
            this.rectangle = rectangle;
            this.removableRectangle = removableRectangle;
            this.index = index;
        }

        final Rectangle rectangle;
        final Rectangle removableRectangle;
        final int index;
    }

    private static class WrapLayoutSize {

        private final List<RectangleRowColumn> rectangles;
        private int alignment;
        private Insets insets;
        private int width;
        private int height;
        private int gap;

        public WrapLayoutSize() {
            this.rectangles = new ArrayList<>();
        }

        public void init(int alignment, Insets insets, int width, int height, int gap) {
            rectangles.clear();
            this.alignment = alignment;
            this.insets = insets;
            this.width = width - (insets.left + insets.right);
            this.height = height - (insets.top + insets.bottom);
            this.gap = gap;
        }

        public Rectangle addTemp(Dimension size) {
            Rectangle rec = add(size);
            removeLast();
            return rec;
        }

        public Rectangle add(Dimension size) {
            return addImpl(size.width, size.height);
        }

        private Rectangle addImpl(int w, int h) {
            if (w > width) {
                w = width;
            }
            boolean isLeft = alignment == SwingConstants.LEFT;
            int x;
            int y;
            if (!rectangles.isEmpty()) {
                Rectangle rec = rectangles.get(rectangles.size() - 1).rectangle;
                x = isLeft ? (rec.x + rec.width + gap) : rec.x - (w + gap);
                y = rec.y;
            } else {
                x = isLeft ? insets.left : insets.left + width - w;
                y = insets.top;
            }
            int row = Math.max(getRow(), 0);
            int column = getColumn();
            if (column > -1 && (isLeft && x + w > width) || (!isLeft && x < insets.left)) {
                x = isLeft ? insets.left : insets.left + width - w;
                y += getMacRowHeight(row) + gap;
                row++;
                column = 0;
            } else {
                column++;
            }
            rectangles.add(new RectangleRowColumn(new Rectangle(x, y, w, h), row, column));
            return new Rectangle(x, y, w, h);
        }

        public Dimension getMaxSize() {
            int width = insets.left + insets.right;
            int height = insets.top + insets.bottom;
            if (!rectangles.isEmpty()) {
                Rectangle rec = rectangles.get(rectangles.size() - 1).rectangle;
                width = rec.x + rec.width + insets.right;
                height = rec.y + rec.height + insets.bottom;
            }
            return new Dimension(width, height);
        }

        public boolean isOverflow(Rectangle rec) {
            int maxWidth = insets.left + width;
            int maxHeight = insets.top + height;
            return rec.x + rec.width > maxWidth || rec.y + rec.height > maxHeight;
        }

        public void removeLast() {
            if (!rectangles.isEmpty()) {
                rectangles.remove(rectangles.size() - 1);
            }
        }

        public RectangleRowColumn getRectangleAtPoint(int x, int y) {
            if (rectangles.isEmpty()) {
                return null;
            }
            for (RectangleRowColumn rec : rectangles) {
                if (rec.rectangle.y >= y || rec.rectangle.y + rec.rectangle.height > y) {
                    return rec;
                }
            }
            return null;
        }

        public int getRow() {
            if (rectangles.isEmpty()) {
                return -1;
            }
            return rectangles.get(rectangles.size() - 1).row;
        }

        public int getColumn() {
            if (rectangles.isEmpty()) {
                return -1;
            }
            return rectangles.get(rectangles.size() - 1).column;
        }

        public int getMacRowHeight(int row) {
            if (rectangles.isEmpty()) {
                return 0;
            }
            boolean found = false;
            int height = 0;
            for (int i = rectangles.size() - 1; i >= 0; i--) {
                RectangleRowColumn rec = rectangles.get(i);
                if (rec.row == row) {
                    height = Math.max(height, rec.rectangle.height);
                    found = true;
                } else if (found) {
                    break;
                }
            }
            return height;
        }

        private static class RectangleRowColumn {

            public RectangleRowColumn(Rectangle rectangle, int row, int column) {
                this.rectangle = rectangle;
                this.row = row;
                this.column = column;
            }

            private final Rectangle rectangle;
            private final int row;
            private final int column;
        }
    }
}
