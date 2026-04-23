package raven.swingpack.datetime;

import raven.swingpack.JDateTimeField;

import java.awt.*;

/**
 * @author Raven
 */
public interface DateTimeItemRenderer {

    Component getDateTimeItemRendererComponent(JDateTimeField dateTime, DateTimePart part, boolean isSelected, boolean hasFocus, int index);
}
