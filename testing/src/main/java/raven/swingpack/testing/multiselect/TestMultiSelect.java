package raven.swingpack.testing.multiselect;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.swingpack.JMultiSelectComboBox;
import raven.swingpack.multiselect.DefaultMultiSelectItemRenderer;
import raven.swingpack.multiselect.MultiSelectItemEditable;
import raven.swingpack.testing.BaseFrame;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TestMultiSelect extends BaseFrame {

    public TestMultiSelect() {
        super("Multi Select");
    }

    @Override
    protected Component createComponent() {
        JPanel panel = new JPanel(new MigLayout("wrap,fillx", "[fill]"));

        String[] items = new String[]{
                "Blueberry",
                "Kiwi",
                "Mango",
                "Pineapple",
                "Strawberry",
                "Watermelon",
                "Raspberry",
                "Peach",
                "Orange",
                "Apple",
                "Banana",
                "Pear",
                "Cherry",
                "Papaya",
                "Plum",
                "Apricot",
                "Lime",
                "Lychee",
                "Coconut",
                "Pomegranate",
                "Avocado",
                "Grape"
        };
        JMultiSelectComboBox<String> multiSelect = new JMultiSelectComboBox<>();
        multiSelect.setItemEditable(new MultiSelectItemEditable() {
            @Override
            public boolean isItemAddable(Object item) {
                return item != "Apple";
            }

            @Override
            public boolean isItemRemovable(Object item) {
                return item != "Pomegranate";
            }
        });
        for (String item : items) {
            multiSelect.addItem(item, true);
        }

        multiSelect.setRow(3);
        panel.add(multiSelect);

        ColorMultiSelectItemRenderer.ColorItem[] colorItems =
                new ColorMultiSelectItemRenderer.ColorItem[]{
                        new ColorMultiSelectItemRenderer.ColorItem("Blue", "#1976D2", "#64B5F6"),
                        new ColorMultiSelectItemRenderer.ColorItem("Teal", "#00796B", "#4DB6AC"),
                        new ColorMultiSelectItemRenderer.ColorItem("Green", "#388E3C", "#81C784"),
                        new ColorMultiSelectItemRenderer.ColorItem("Amber", "#FFA000", "#FFB74D"),
                        new ColorMultiSelectItemRenderer.ColorItem("Deep Orange", "#E64A19", "#FF7043"),
                        new ColorMultiSelectItemRenderer.ColorItem("Pink", "#D81B60", "#F06292"),
                        new ColorMultiSelectItemRenderer.ColorItem("Purple", "#8E24AA", "#BA68C8"),
                        new ColorMultiSelectItemRenderer.ColorItem("Gray", "#616161", "#B0BEC5")
                };

        JMultiSelectComboBox<ColorMultiSelectItemRenderer.ColorItem> multiSelectColor = new JMultiSelectComboBox<>(new DefaultComboBoxModel<>(colorItems));

        for (int i = 0; i < multiSelectColor.getItemCount(); i++) {
            multiSelectColor.addSelectedItem(multiSelectColor.getItemAt(i));
        }
        panel.add(multiSelectColor);

        multiSelect.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;" +
                "editableBackground:null;");
        multiSelectColor.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;" +
                "editableBackground:null;");
        multiSelectColor.setRemovableIcon(new CrossRemovableIcon());

        IconMultiSelectItemRenderer.apply(multiSelect);
        ColorMultiSelectItemRenderer.apply(multiSelectColor);

        // options
        JCheckBox chScroll = new JCheckBox("Enable scroll");
        JCheckBox chRightToLeft = new JCheckBox("Right to left");
        JCheckBox chShowRemovable = new JCheckBox("Show removable", true);
        JCheckBox chIconRenderer = new JCheckBox("Icon renderer", true);
        JCheckBox chFullRound = new JCheckBox("Full round");

        chScroll.addActionListener(e -> {
            if (chScroll.isSelected()) {
                multiSelect.setDisplayMode(JMultiSelectComboBox.DisplayMode.WRAP_SCROLL);
                multiSelectColor.setDisplayMode(JMultiSelectComboBox.DisplayMode.WRAP_SCROLL);
            } else {
                multiSelect.setDisplayMode(JMultiSelectComboBox.DisplayMode.AUTO_WRAP);
                multiSelectColor.setDisplayMode(JMultiSelectComboBox.DisplayMode.AUTO_WRAP);
            }
        });
        chRightToLeft.addActionListener(e -> {
            if (chRightToLeft.isSelected()) {
                applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            } else {
                applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            }
            repaint();
            revalidate();
        });
        chShowRemovable.addActionListener(e -> {
            multiSelect.setShowItemRemovableIcon(chShowRemovable.isSelected());
            multiSelectColor.setShowItemRemovableIcon(chShowRemovable.isSelected());
        });
        chIconRenderer.addActionListener(e -> {
            if (chIconRenderer.isSelected()) {
                IconMultiSelectItemRenderer.apply(multiSelect);
            } else {
                multiSelect.setItemRenderer(new DefaultMultiSelectItemRenderer());
            }
        });

        chFullRound.addActionListener(e -> {
            if (chFullRound.isSelected()) {
                multiSelect.setItemArc(999);
                multiSelectColor.setItemArc(999);
            } else {
                multiSelect.setItemArc(-1);
                multiSelectColor.setItemArc(-1);
            }
        });

        JPanel panelOption = new JPanel(new MigLayout());
        panelOption.setBorder(new TitledBorder("Options"));
        panelOption.add(chScroll);
        panelOption.add(chRightToLeft);
        panelOption.add(chShowRemovable);
        panelOption.add(chIconRenderer);
        panelOption.add(chFullRound);
        panel.add(panelOption);

        return panel;
    }

    public static void main(String[] args) {
        installLaf();
        EventQueue.invokeLater(() -> new TestMultiSelect().setVisible(true));
    }
}
