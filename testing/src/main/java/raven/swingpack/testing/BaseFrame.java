package raven.swingpack.testing;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.swingpack.testing.utils.MemoryBar;
import raven.swingpack.testing.utils.SVGIconUIColor;
import raven.swingpack.testing.utils.themes.PanelThemes;

import javax.swing.*;
import java.awt.*;

public abstract class BaseFrame extends JFrame {

    public static final String TEST_VERSION = "1.0.0-SNAPSHOT";

    public BaseFrame(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout("insets 0,fill,wrap 2", "[fill][grow 0,fill,230!]", "[fill]0[grow 0]0[grow 0]"));
        setSize(UIScale.scale(new Dimension(1000, 600)));
        setLocationRelativeTo(null);

        add(createComponent());
        add(new PanelThemes(), "gap 4 4 4 4");
        add(new JSeparator(), "height 2,span 2");
        add(createFooter(), "span 2");
    }

    protected abstract Component createComponent();

    private Component createFooter() {
        JPanel panel = new JPanel(new MigLayout("insets 1 n 1 n,al trailing center,gapx 10,height 30!", "[]push[][]", "fill"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:tint($Panel.background,20%);" +
                "[dark]background:tint($Panel.background,5%);");

        // demo version
        JLabel lbDemoVersion = new JLabel("Test: v" + TEST_VERSION);
        lbDemoVersion.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Label.disabledForeground;");
        lbDemoVersion.setIcon(new SVGIconUIColor("raven/swingpack/testing/other/git.svg", 1f, "Label.disabledForeground"));
        panel.add(lbDemoVersion);

        // java version
        String javaVendor = System.getProperty("java.vendor");
        if (javaVendor.equals("Oracle Corporation")) {
            javaVendor = "";
        }
        String java = javaVendor + " v" + System.getProperty("java.version").trim();
        String st = "Running on: Java %s";
        JLabel lbJava = new JLabel(String.format(st, java));
        lbJava.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Label.disabledForeground;");
        lbJava.setIcon(new SVGIconUIColor("raven/swingpack/testing/other/java.svg", 1f, "Label.disabledForeground"));
        panel.add(lbJava);

        panel.add(new JSeparator(JSeparator.VERTICAL));

        // memory
        MemoryBar memoryBar = new MemoryBar();
        panel.add(memoryBar);
        return panel;
    }

    protected static void installLaf() {
        FlatRobotoFont.install();
        FlatDarculaLaf.setup();
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
    }
}
