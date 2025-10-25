package raven.swingpack.multiselect;

import raven.swingpack.JMultiSelectComboBox;

import java.awt.*;

/**
 * @author Raven
 */
public interface MultiSelectItemRenderer {

    Component getMultiSelectItemRendererComponent(JMultiSelectComboBox<?> multiSelect, Object value, boolean isPressed, boolean hasFocus, boolean removableFocus, int index);
}
