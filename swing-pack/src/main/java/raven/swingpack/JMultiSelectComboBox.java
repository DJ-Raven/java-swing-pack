package raven.swingpack;

import com.formdev.flatlaf.util.UIScale;
import raven.swingpack.multiselect.*;
import raven.swingpack.multiselect.event.MultiSelectEvent;
import raven.swingpack.multiselect.event.MultiSelectListener;
import raven.swingpack.multiselect.icons.DefaultRemovableIcon;
import raven.swingpack.multiselect.icons.ItemActionIcon;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.Vector;

/**
 * @author Raven
 */
public class JMultiSelectComboBox<E> extends JComboBox<E> implements MultiSelectListener {

    private MultiSelectModel<E> multiSelectModel;
    private MultiSelectItemEditable itemEditable;
    private MultiSelectItemRenderer itemRenderer;
    private MultiSelectEditor multiSelectEditor;
    private boolean noVisualPadding;
    private boolean showItemRemovableIcon = true;
    private int itemRemovableTextGap = 3;
    private int itemGap = 5;
    private int itemArc = -1;
    private int itemAlignment = SwingConstants.LEADING;
    private ItemActionIcon removableIcon = new DefaultRemovableIcon();
    private Insets itemContainerInsets = new Insets(5, 5, 5, 5);
    private Insets itemInsets = new Insets(3, 7, 3, 7);
    private int row;

    private boolean overflowPopupEnable = true;
    private int overflowPopupItemRow = 3;
    private Dimension overflowPopupSize = new Dimension(250, 100);
    private OverflowPopup overflowPopup;

    public JMultiSelectComboBox() {
        init();
    }

    public JMultiSelectComboBox(ComboBoxModel<E> model) {
        super(model);
        init();
    }

    private void init() {
        multiSelectEditor = new MultiSelectEditor(this, new MultiSelectView(this));
        setEditor(new MultiSelectComboBoxEditor(multiSelectEditor));
        setRenderer(new MultiSelectCellRenderer());
        setItemRenderer(new DefaultMultiSelectItemRenderer());
        addActionListener(e -> {
            if ((e.getModifiers() & InputEvent.MOUSE_EVENT_MASK) != 0) {
                Object object = getSelectedItem();
                if (object != null) {
                    if (multiSelectModel.isSelectedItem(object)) {
                        removeSelectedItem(object);
                    } else {
                        addSelectedItem(object);
                        multiSelectEditor.multiSelectView.scrollTo(getSelectedItemCount() - 1);
                    }
                }
            }
        });
    }

    public MultiSelectModel<E> getMultiSelectModel() {
        return multiSelectModel;
    }

    @Override
    public void setModel(ComboBoxModel<E> model) {
        ComboBoxModel<E> newModel = copyModel(model);
        if (multiSelectModel == null) {
            multiSelectModel = new MultiSelectModel<>(newModel);
            multiSelectModel.addEventListener(this);
        } else {
            multiSelectModel.setModel(newModel);
        }
        super.setModel(newModel);
    }

    private ComboBoxModel<E> copyModel(ComboBoxModel<E> model) {
        Vector<E> vector = new Vector<>();
        for (int i = 0; i < model.getSize(); i++) {
            vector.addElement(model.getElementAt(i));
        }
        return new DefaultComboBoxModel<>(vector);
    }

    @Override
    public void setRenderer(ListCellRenderer<? super E> renderer) {
        ListCellRenderer<? super E> oldRenderer = getRenderer();
        if (renderer instanceof MultiSelectCellRenderer) {
            ((MultiSelectCellRenderer) renderer).initMultiSelect(this);
        }
        if (oldRenderer instanceof MultiSelectCellRenderer) {
            ((MultiSelectCellRenderer) oldRenderer).initMultiSelect(null);
        }
        super.setRenderer(renderer);
    }

    public MultiSelectItemRenderer getItemRenderer() {
        return itemRenderer;
    }

    public void setItemRenderer(MultiSelectItemRenderer itemRenderer) {
        this.itemRenderer = itemRenderer;
        if (this.itemRenderer instanceof JComponent) {
            ((JComponent) this.itemRenderer).applyComponentOrientation(getComponentOrientation());
        }
        repaint();
    }

    public DisplayMode getDisplayMode() {
        return multiSelectEditor.getDisplayMode();
    }

    public void setDisplayMode(DisplayMode displayMode) {
        multiSelectEditor.setDisplayMode(displayMode);
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

    public boolean isShowItemRemovableIcon() {
        return showItemRemovableIcon;
    }

    public void setShowItemRemovableIcon(boolean showItemRemovableIcon) {
        if (this.showItemRemovableIcon != showItemRemovableIcon) {
            this.showItemRemovableIcon = showItemRemovableIcon;
            repaint();
            revalidate();
        }
    }

    public int getItemRemovableTextGap() {
        return itemRemovableTextGap;
    }

    public void setItemRemovableTextGap(int itemRemovableTextGap) {
        if (this.itemRemovableTextGap != itemRemovableTextGap) {
            this.itemRemovableTextGap = itemRemovableTextGap;
            repaint();
            revalidate();
        }
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

    public int getItemArc() {
        return itemArc;
    }

    public void setItemArc(int itemArc) {
        if (this.itemArc != itemArc) {
            this.itemArc = itemArc;
            repaint();
        }
    }

    public int getItemAlignment() {
        return itemAlignment;
    }

    public void setItemAlignment(int itemAlignment) {
        itemAlignment = checkItemAlignment(itemAlignment);
        if (this.itemAlignment != itemAlignment) {
            this.itemAlignment = itemAlignment;
            repaint();
            revalidate();
        }
    }

    public Insets getItemInsets() {
        return itemInsets;
    }

    public void setItemInsets(Insets itemInsets) {
        this.itemInsets = itemInsets;
        repaint();
        revalidate();
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        if (this.row != row) {
            this.row = row;
            repaint();
            revalidate();
        }
    }

    public boolean isOverflowPopupEnable() {
        return overflowPopupEnable;
    }

    public void setOverflowPopupEnable(boolean overflowPopupEnable) {
        this.overflowPopupEnable = overflowPopupEnable;
    }

    public int getOverflowPopupItemRow() {
        return overflowPopupItemRow;
    }

    public void setOverflowPopupItemRow(int overflowPopupItemRow) {
        if (this.overflowPopupItemRow != overflowPopupItemRow) {
            this.overflowPopupItemRow = overflowPopupItemRow;
            if (overflowPopup != null) {
                overflowPopup.update();
            }
        }
    }

    public Dimension getOverflowPopupSize() {
        return overflowPopupSize;
    }

    public void setOverflowPopupSize(Dimension overflowPopupSize) {
        this.overflowPopupSize = overflowPopupSize;
    }

    public Insets getItemContainerInsets() {
        return itemContainerInsets;
    }

    public void setItemContainerInsets(Insets itemContainerInsets) {
        this.itemContainerInsets = itemContainerInsets;
        repaint();
        revalidate();
    }

    public ItemActionIcon getRemovableIcon() {
        return removableIcon;
    }

    public void setRemovableIcon(ItemActionIcon itemActionIcon) {
        this.removableIcon = itemActionIcon;
        repaint();
        revalidate();
    }

    public int getOverflowItemCount() {
        return multiSelectEditor.multiSelectView.getOverflowItemCount();
    }

    public Object[] getOverflowItems() {
        return multiSelectEditor.multiSelectView.getOverflowItems();
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public void setPopupVisible(boolean v) {
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (itemRenderer instanceof JComponent) {
            SwingUtilities.updateComponentTreeUI((JComponent) itemRenderer);
        }
    }

    @Override
    public void setComponentOrientation(ComponentOrientation o) {
        super.setComponentOrientation(o);
        if (itemRenderer instanceof JComponent) {
            ((JComponent) itemRenderer).applyComponentOrientation(o);
        }
        if (overflowPopup != null) {
            overflowPopup.updateComponentOrientation(o);
        }
    }

    @Override
    public void addItem(E item) {
        addItem(item, false);
    }

    @Override
    public void removeItem(Object item) {
        super.removeItem(item);
        getMultiSelectModel().removeSelectedItem(item);
    }

    @Override
    public void removeItemAt(int index) {
        Object item = getSelectedItemAt(index);
        super.removeItemAt(index);
        getMultiSelectModel().removeSelectedItem(item);
    }

    @Override
    public void removeAllItems() {
        super.removeAllItems();
        getMultiSelectModel().clearSelectedItems();
    }

    public void addItem(E item, boolean selected) {
        super.addItem(item);
        if (selected) {
            addSelectedItem(item);
        }
    }

    public MultiSelectItemEditable getItemEditable() {
        return itemEditable;
    }

    public void setItemEditable(MultiSelectItemEditable itemEditable) {
        this.itemEditable = itemEditable;
        repaint();
        revalidate();
    }

    public boolean isItemAddable(Object item) {
        if (itemEditable == null) {
            return true;
        }

        return itemEditable.isItemAddable(item);
    }

    public boolean isItemRemovable(Object item) {
        if (itemEditable == null) {
            return true;
        }

        return itemEditable.isItemRemovable(item);
    }

    public boolean isSelectedItem(Object item) {
        return multiSelectModel.isSelectedItem(item);
    }

    public void addSelectedItem(Object item) {
        if (!isItemAddable(item)) {
            return;
        }
        multiSelectModel.addSelectedItem(item);
    }

    public void removeSelectedItem(Object item) {
        if (!isItemRemovable(item)) {
            return;
        }
        multiSelectModel.removeSelectedItem(item);
    }

    public void removeSelectedItemAt(int index) {
        Object item = multiSelectModel.getSelectedItemAt(index);
        if (item == null) {
            return;
        }
        removeSelectedItem(item);
    }

    public void clearSelectedItems() {
        Object[] items = getSelectedItems();
        Vector<Object> itemRemove = new Vector<>();
        for (Object item : items) {
            if (isItemRemovable(item)) {
                itemRemove.add(item);
            }
        }
        if (items.length == itemRemove.size()) {
            multiSelectModel.clearSelectedItems();
        } else {
            multiSelectModel.removeSelectedItems(itemRemove.toArray());
        }
    }

    public void clearSelectedItemsForce() {
        multiSelectModel.clearSelectedItems();
    }

    public Object[] getSelectedItems() {
        return multiSelectModel.getSelectedItems();
    }

    public int getSelectedItemCount() {
        return multiSelectModel.getSelectedItemCount();
    }

    public Object getSelectedItemAt(int index) {
        return multiSelectModel.getSelectedItemAt(index);
    }

    public int getSelectedItemIndex(Object item) {
        return multiSelectModel.getSelectedItemIndex(item);
    }

    @Override
    public void itemAdded(MultiSelectEvent event) {
        multiSelectEditor.updateLayout();
        repaintPopup();
    }

    @Override
    public void itemRemoved(MultiSelectEvent event) {
        multiSelectEditor.updateLayout();
        if (overflowPopup != null) {
            overflowPopup.update();
        }
        repaintPopup();
    }

    @Override
    public void itemSelected(MultiSelectEvent event) {
    }

    @Override
    public void overflowSelected(MultiSelectEvent event) {
        showOverflowPopup();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
    }

    protected int checkItemAlignment(int alignment) {
        if ((alignment == SwingConstants.LEFT) ||
                (alignment == SwingConstants.CENTER) ||
                (alignment == SwingConstants.RIGHT) ||
                (alignment == SwingConstants.LEADING) ||
                (alignment == SwingConstants.TRAILING)) {
            return alignment;
        } else {
            throw new IllegalArgumentException("Invalid alignment.");
        }
    }

    private void repaintPopup() {
        if (isPopupVisible()) {
            ComboPopup comboPopup = (ComboPopup) getUI().getAccessibleChild(this, 0);
            if (comboPopup != null) {
                comboPopup.getList().repaint();
            }
        }
    }

    public void showOverflowPopup() {
        if (!overflowPopupEnable || getOverflowItemCount() == 0) return;

        Point point = multiSelectEditor.multiSelectView.getOverflowPopupLocation();
        showOverflowPopupImpl(multiSelectEditor.multiSelectView, point.x, point.y + UIScale.scale(2));
    }


    public void showOverflowPopup(Component com, int x, int y) {
        if (!overflowPopupEnable || getOverflowItemCount() == 0) return;

        showOverflowPopupImpl(com, x, y);
    }

    private void showOverflowPopupImpl(Component com, int x, int y) {
        if (overflowPopup == null) {
            overflowPopup = new OverflowPopup();
        }
        overflowPopup.show(com, x, y);
    }

    public Rectangle getRectangleAt(int index, boolean includeSpacing) {
        return multiSelectEditor.multiSelectView.getRectangleAt(index, includeSpacing);
    }

    protected JScrollPane createSelectViewScroll() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    public void addEventListener(MultiSelectListener listener) {
        if (multiSelectModel != null) {
            multiSelectModel.addEventListener(listener);
        }
    }

    public void removeEventListener(MultiSelectListener listener) {
        if (multiSelectModel != null) {
            multiSelectModel.removeEventListener(listener);
        }
    }

    private static class MultiSelectComboBoxEditor extends BasicComboBoxEditor {

        private final MultiSelectEditor editor;

        public MultiSelectComboBoxEditor(MultiSelectEditor editor) {
            this.editor = editor;
        }

        @Override
        public Component getEditorComponent() {
            return editor;
        }
    }

    private class OverflowPopup {

        private final JPopupMenu popup;
        private final MultiSelectView selectView;
        private final JScrollPane scrollPane;

        private LookAndFeel oldThemes = UIManager.getLookAndFeel();

        public OverflowPopup() {
            popup = new JPopupMenu();
            popup.setLayout(new BorderLayout());

            selectView = new MultiSelectView(JMultiSelectComboBox.this, true);

            // init scroll
            scrollPane = createSelectViewScroll();
            scrollPane.setViewportView(selectView);
            popup.add(scrollPane);
        }

        public void show(Component com, int x, int y) {
            if (UIManager.getLookAndFeel() != oldThemes) {
                // component in popup not update UI when change themes
                // so need to update when popup show
                SwingUtilities.updateComponentTreeUI(popup);
                oldThemes = UIManager.getLookAndFeel();
            }

            Dimension size = new Dimension(UIScale.scale(getOverflowPopupSize()));
            if (getOverflowPopupItemRow() > 0) {
                Dimension overflowSize = selectView.getMinimumLayoutSize();
                size.height = overflowSize.height;
            }
            scrollPane.setPreferredSize(size);
            popup.show(com, x, y);
        }

        public void update() {
            if (popup.isVisible()) {
                SwingUtilities.invokeLater(() -> {
                    selectView.revalidate();
                    if (getOverflowItemCount() == 0) {
                        popup.setVisible(false);
                    }
                });
            }
        }

        public void updateComponentOrientation(ComponentOrientation o) {
            scrollPane.setComponentOrientation(o);
        }
    }

    private static class MultiSelectEditor extends JComponent {

        private final JMultiSelectComboBox<?> multiSelect;
        private final MultiSelectView multiSelectView;
        private JScrollPane scrollPane;
        private DisplayMode displayMode = DisplayMode.AUTO_WRAP;

        public MultiSelectEditor(JMultiSelectComboBox<?> multiSelect, MultiSelectView multiSelectView) {
            this.multiSelect = multiSelect;
            this.multiSelectView = multiSelectView;
            setLayout(new BorderLayout());
            add(multiSelectView);
        }

        public DisplayMode getDisplayMode() {
            return displayMode;
        }

        public void setDisplayMode(DisplayMode displayMode) {
            if (this.displayMode != displayMode) {
                DisplayMode oldValue = this.displayMode;
                this.displayMode = displayMode;
                boolean isScroll = isScrollType(this.displayMode);
                if (isScrollType(oldValue) != isScroll) {
                    if (isScroll) {
                        remove(multiSelectView);
                        JScrollPane scrollPane = getScrollPane();
                        scrollPane.getViewport().setOpaque(false);
                        scrollPane.setViewportView(multiSelectView);
                        scrollPane.setComponentOrientation(getComponentOrientation());
                        add(scrollPane);
                    } else {
                        remove(scrollPane);
                        add(multiSelectView);
                    }
                }
                repaint();
                revalidate();
            }
        }

        public void updateLayout() {
            multiSelectView.repaint();
            multiSelectView.revalidate();
        }

        public JScrollPane getScrollPane() {
            if (scrollPane == null) {
                scrollPane = multiSelect.createSelectViewScroll();
            }
            return scrollPane;
        }

        private boolean isScrollType(DisplayMode displayMode) {
            return displayMode == DisplayMode.WRAP_SCROLL;
        }

        @Override
        public Dimension getPreferredSize() {
            if (multiSelectView == null) {
                return super.getPreferredSize();
            }
            return multiSelectView.getMinimumLayoutSize();
        }

        @Override
        public void updateUI() {
            super.updateUI();
            if (scrollPane != null) {
                SwingUtilities.updateComponentTreeUI(scrollPane);
            }
        }
    }

    public enum DisplayMode {
        AUTO_WRAP, WRAP_SCROLL
    }
}
