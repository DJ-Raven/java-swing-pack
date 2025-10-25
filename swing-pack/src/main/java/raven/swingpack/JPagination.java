package raven.swingpack;

import com.formdev.flatlaf.util.UIScale;
import raven.swingpack.pagination.*;
import raven.swingpack.pagination.event.PaginationModelEvent;
import raven.swingpack.pagination.event.PaginationModelListener;
import raven.swingpack.pagination.icons.PageArrowIcon;
import raven.swingpack.util.SwingPackUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Raven
 */
public class JPagination extends JPanel implements PaginationModelListener {

    protected transient ChangeEvent changeEvent = null;
    protected CellRendererPane rendererPane;
    private PaginationModel paginationModel;
    private PaginationItemRenderer itemRenderer;
    private int maxItem;
    private boolean showNavigationButton = true;
    private boolean navigationAlwaysEnabled = true;
    private boolean hideWhenNoPage = true;
    private boolean noVisualPadding;
    private boolean loop;
    private Dimension itemSize = new Dimension(28, 28);
    private int itemGap = 7;

    private Icon previousIcon = new PageArrowIcon(true);
    private Icon nextIcon = new PageArrowIcon(false);

    private int focusIndex = -1;
    private int pressedIndex = -1;

    public JPagination() {
        this(7);
    }

    public JPagination(int maxItem) {
        this(maxItem, 0, 0);
    }

    public JPagination(int selectedPage, int pageSize) {
        this(7, selectedPage, pageSize);
    }

    public JPagination(int maxItem, int selectedPage, int pageSize) {
        this(new DefaultPaginationModel(maxItem, selectedPage, pageSize));
        this.maxItem = maxItem;
    }

    public JPagination(PaginationModel model) {
        setLayout(new BorderLayout());
        setModel(model);
        rendererPane = new CellRendererPane();
        itemRenderer = new DefaultPaginationItemRenderer();

        installListener();
    }

    private void installListener() {
        MouseAdapter mouseListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    setPressedIndex(getIndexAt(e.getX(), e.getY()));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int index = getIndexAt(e.getX(), e.getY());
                    boolean action = index != -1 && index == focusIndex;
                    setPressedIndex(-1);
                    setFocusIndex(index);
                    if (action) {
                        Page page = getPageAt(index);
                        if (page.getType() == Page.Type.PAGE || page.getType() == Page.Type.ELLIPSIS) {
                            setSelectedPage(page.getValue());
                        } else if (page.getType() == Page.Type.PREVIOUS) {
                            if (getSelectedPage() == 1 && isLoop()) {
                                setSelectedPage(getModel().getPageSize());
                                return;
                            }
                            setSelectedPage(getSelectedPage() - 1);
                        } else if (page.getType() == Page.Type.NEXT) {
                            if (getSelectedPage() == getModel().getPageSize() && isLoop()) {
                                setSelectedPage(1);
                                return;
                            }
                            setSelectedPage(getSelectedPage() + 1);
                        }
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                setFocusIndex(getIndexAt(e.getX(), e.getY()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setFocusIndex(-1);
            }

            private void setFocusIndex(int index) {
                if (focusIndex != index) {
                    focusIndex = index;
                    repaint();
                }
            }

            private void setPressedIndex(int index) {
                if (pressedIndex != index) {
                    pressedIndex = index;
                    repaint();
                }
            }
        };

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
    }

    public PaginationModel getModel() {
        return paginationModel;
    }

    public void setModel(PaginationModel model) {
        PaginationModel oldValue = getModel();
        if (oldValue != null) {
            oldValue.removePaginationModelListener(this);
        }
        this.paginationModel = model;
        if (model != null) {
            model.addPaginationModelListener(this);
        }
        revalidate();
        repaint();
        firePropertyChange("model", oldValue, model);
    }

    public PaginationItemRenderer getItemRenderer() {
        return itemRenderer;
    }

    public void setItemRenderer(PaginationItemRenderer itemRenderer) {
        this.itemRenderer = itemRenderer;
        repaint();
    }

    public boolean isShowNavigationButton() {
        return showNavigationButton;
    }

    public void setShowNavigationButton(boolean showNavigationButton) {
        if (this.showNavigationButton != showNavigationButton) {
            this.showNavigationButton = showNavigationButton;
            repaint();
            revalidate();
        }
    }

    public boolean isNavigationAlwaysEnabled() {
        return navigationAlwaysEnabled;
    }

    public void setNavigationAlwaysEnabled(boolean navigationAlwaysEnabled) {
        if (this.navigationAlwaysEnabled != navigationAlwaysEnabled) {
            this.navigationAlwaysEnabled = navigationAlwaysEnabled;
            repaint();
        }
    }

    public boolean isHideWhenNoPage() {
        return hideWhenNoPage;
    }

    public void setHideWhenNoPage(boolean hideWhenNoPage) {
        if (this.hideWhenNoPage != hideWhenNoPage) {
            this.hideWhenNoPage = hideWhenNoPage;
            repaint();
            revalidate();
        }
    }

    public boolean isNoVisualPadding() {
        return noVisualPadding;
    }

    public void setNoVisualPadding(boolean noVisualPadding) {
        if (this.noVisualPadding != noVisualPadding) {
            this.noVisualPadding = noVisualPadding;
            repaint();
            revalidate();
        }
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        if (this.loop != loop) {
            this.loop = loop;
            repaint();
        }
    }

    public Dimension getItemSize() {
        return itemSize;
    }

    public void setItemSize(Dimension itemSize) {
        this.itemSize = itemSize;
        repaint();
        revalidate();
    }

    public int getItemGap() {
        return itemGap;
    }

    public void setItemGap(int itemGap) {
        if (this.itemGap != itemGap) {
            this.itemGap = itemGap;
            repaint();
            revalidate();
        }
    }

    public Icon getPreviousIcon() {
        return previousIcon;
    }

    public void setPreviousIcon(Icon previousIcon) {
        this.previousIcon = previousIcon;
        repaint();
        revalidate();
    }

    public Icon getNextIcon() {
        return nextIcon;
    }

    public void setNextIcon(Icon nextIcon) {
        this.nextIcon = nextIcon;
        repaint();
        revalidate();
    }

    public int getSelectedPage() {
        return getModel().getSelectedPage();
    }

    public void setSelectedPage(int selectedPage) {
        getModel().setSelectedPage(selectedPage);
    }

    public int getPageSize() {
        return getModel().getPageSize();
    }

    public void setPageSize(int pageSize) {
        getModel().setPageSize(pageSize);
    }

    public void setPageRange(int selectedPage, int pageSize) {
        getModel().setPageRange(selectedPage, pageSize);
    }

    public boolean hasPrevious() {
        return getModel().hasPrevious();
    }

    public boolean hasNext() {
        return getModel().hasNext();
    }

    public int getMaxItem() {
        return maxItem;
    }

    public void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }

    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

    @Override
    public void paginationModelChanged(PaginationModelEvent event) {
        repaint();
        if (event.isPageChanged()) {
            fireStateChanged();
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();

        // update the page renderer when L&F changed
        if (itemRenderer != null && itemRenderer instanceof Component) {
            SwingUtilities.updateComponentTreeUI((Component) itemRenderer);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintImpl(g);
    }

    private void paintImpl(Graphics g) {
        Shape clip = g.getClip();
        Page[] pages = getModel().getPagination();
        int size = pages.length;

        boolean isCreateNavigation = checkCreateNavigationButton(size);
        int index = -1;
        if (isCreateNavigation) {
            // create previous button
            Page previousPage = new Page(-1, Page.Type.PREVIOUS);
            int pageIndex = ++index;
            Rectangle rec = rectangleAt(pageIndex);
            if (clip == null || clip.intersects(rec)) {
                paintItem(g, previousPage, pageIndex, rec);
            }
        }

        // create item page
        for (int i = 0; i < size; i++) {
            Page page = pages[i];
            int pageIndex = ++index;
            Rectangle rec = rectangleAt(pageIndex);
            if (clip == null || clip.intersects(rec)) {
                paintItem(g, page, pageIndex, rec);
            }
        }

        if (isCreateNavigation) {
            // create next button
            Page nextPage = new Page(1, Page.Type.NEXT);
            int pageIndex = ++index;
            Rectangle rec = rectangleAt(pageIndex);
            if (clip == null || clip.intersects(rec)) {
                paintItem(g, nextPage, pageIndex, rec);
            }
        }

        rendererPane.removeAll();
    }

    private void paintItem(Graphics g, Page page, int index, Rectangle rec) {
        boolean isSelected = page.getType() == Page.Type.PAGE && getSelectedPage() == page.getValue();
        Component c = itemRenderer.getPaginationItemRendererComponent(this, page, isSelected, index == pressedIndex, index == focusIndex, index);
        if (!noVisualPadding) {
            SwingPackUtils.applyVisualPadding(c, rec);
        }
        rendererPane.paintComponent(g, c, this, rec);
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        return paginationPreferredSize();
    }

    private Dimension paginationPreferredSize() {
        Insets insets = getInsets();
        int width = insets.left + insets.right;
        int height = insets.top + insets.bottom;
        int size = getModel().getPagination().length;
        if (size > 0) {
            int totalGap = (size - 1) * scale(itemGap);
            width += (scale(itemSize.width) * size) + totalGap;
            height += (scale(itemSize.height));
        }

        if (checkCreateNavigationButton(size)) {
            int addGap = 2;
            if (size == 0) {
                height += scale(itemSize.height);
                addGap = 1;
            }
            width += scale((itemSize.width * 2) + (itemGap * addGap));
        }
        return new Dimension(width, height);
    }

    protected Rectangle rectangleAt(int index) {
        Insets insets = getInsets();
        int width = scale(itemSize.width);
        int height = scale(itemSize.height);
        int gap = scale(itemGap);
        int x = insets.left + (width * index) + (gap * index);
        int y = insets.top;
        return new Rectangle(x, y, width, height);
    }

    protected int getIndexAt(int x, int y) {
        Insets insets = getInsets();
        int index = (x - insets.left) / (scale(itemSize.width + itemGap));
        if (rectangleAt(index).contains(x, y)) {
            return index;
        }
        return -1;
    }

    protected Page getPageAt(int index) {
        Page[] pages = getModel().getPagination();
        int size = pages.length;
        if (checkCreateNavigationButton(size)) {
            size += 2;
            if (index == 0) {
                return new Page(-1, Page.Type.PREVIOUS);
            } else if (index == size - 1) {
                return new Page(1, Page.Type.NEXT);
            }
            return pages[index - 1];
        } else {
            return pages[index];
        }
    }

    protected boolean checkCreateNavigationButton(int size) {
        return isShowNavigationButton() && (size > 0 || !isHideWhenNoPage());
    }

    protected int scale(int value) {
        return UIScale.scale(value);
    }
}
