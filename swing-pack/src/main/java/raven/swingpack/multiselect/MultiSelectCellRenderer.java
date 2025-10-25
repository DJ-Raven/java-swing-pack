package raven.swingpack.multiselect;

import raven.swingpack.JMultiSelectComboBox;
import raven.swingpack.multiselect.icons.CheckmarkIcon;

import javax.swing.*;
import java.awt.*;

/**
 * @author Raven
 */
public class MultiSelectCellRenderer extends DefaultListCellRenderer {

    public void initMultiSelect(JMultiSelectComboBox<?> multiSelect) {
        this.multiSelect = multiSelect;
    }

    protected JMultiSelectComboBox<?> multiSelect;
    private CheckmarkIcon checkmarkIcon;

    public MultiSelectCellRenderer() {
    }

    @Override
    public void updateUI() {
        super.updateUI();
        initUI();
    }

    private void initUI() {
        checkmarkIcon = new CheckmarkIcon();
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        boolean isItemSelected = isItemSelected(value);
        Icon icon = getSelectedIcon(value, index, isItemSelected, isSelected, cellHasFocus);
        setEnabled(isItemEditable(value));
        if (isEnabled()) {
            setIcon(icon);
        } else {
            setDisabledIcon(icon);
        }
        return this;
    }

    protected boolean isItemEditable(Object value) {
        boolean isItemSelected = isItemSelected(value);
        boolean editable;
        if (isItemSelected) {
            editable = multiSelect.isItemRemovable(value);
        } else {
            editable = multiSelect.isItemAddable(value);
        }
        return editable;
    }

    protected Icon getSelectedIcon(Object value, int index, boolean isItemSelected, boolean isSelected, boolean cellHasFocus) {
        checkmarkIcon.setSelected(isItemSelected);
        checkmarkIcon.setHasFocus(isSelected);
        return checkmarkIcon;
    }

    protected boolean isItemSelected(Object value) {
        if (multiSelect == null) return false;

        return multiSelect.isSelectedItem(value);
    }
}
