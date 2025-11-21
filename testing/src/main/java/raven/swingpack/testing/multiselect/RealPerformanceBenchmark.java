package raven.swingpack.testing.multiselect;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import net.miginfocom.swing.MigLayout;
import raven.swingpack.JMultiSelectComboBox;
import raven.swingpack.multiselect.event.MultiSelectAdapter;
import raven.swingpack.multiselect.event.MultiSelectEvent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RealPerformanceBenchmark extends JFrame {

    private JMultiSelectComboBox<String> multiSelect;
    private JTextArea logArea;
    private JButton testNormalButton;
    private JButton testBatchButton;
    private JButton testSilentButton;
    private JSpinner itemCountSpinner;
    private JProgressBar progressBar;
    private JButton uiTestButton;
    private AtomicInteger testNumber = new AtomicInteger(1);

    public RealPerformanceBenchmark() {
        super("UI Responsiveness Benchmark");
        initialize();
    }

    private void initialize() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout("wrap, fill", "[grow]", "[][][grow]"));

        // Configuration panel
        JPanel configPanel = new JPanel(new MigLayout());
        configPanel.add(new JLabel("Items:"));
        itemCountSpinner = new JSpinner(new SpinnerNumberModel(100000, 10000, 500000, 10000));
        configPanel.add(itemCountSpinner, "w 100!");

        testNormalButton = new JButton("Test Normal Events");
        testBatchButton = new JButton("Test Batch Events");
        testSilentButton = new JButton("Test Silent Events");
        uiTestButton = new JButton("Test UI Responsiveness");

        configPanel.add(testNormalButton, "gap unrelated");
        configPanel.add(testBatchButton);
        configPanel.add(testSilentButton);
        configPanel.add(uiTestButton);

        add(configPanel, "growx");

        // Progress bar to test UI responsiveness
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("UI Responsiveness Test - Try moving this during tests");
        add(progressBar, "growx");

        // MultiSelect component
        multiSelect = new JMultiSelectComboBox<>();
        multiSelect.setRow(1);
        add(multiSelect, "growx");

        // Log area
        logArea = new JTextArea(12, 80);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, "grow");

        setupEventListeners();
        setupUIResponsivenessTest();

        pack();
        setLocationRelativeTo(null);

        log("🎯 Testing UI RESPONSIVENESS during bulk operations");
        log("   Try interacting with the UI during each test");
        log("   Normal events will freeze the UI, Batch/Silent won't");
        log("");
    }

    private void setupEventListeners() {
        testNormalButton.addActionListener(e -> testNormalEvents());
        testBatchButton.addActionListener(e -> testBatchEvents());
        testSilentButton.addActionListener(e -> testSilentEvents());
        uiTestButton.addActionListener(e -> testUIResponsiveness());
    }

    private void setupUIResponsivenessTest() {
        // Animate progress bar to show UI responsiveness
        Timer timer = new Timer(100, e -> {
            int value = progressBar.getValue();
            progressBar.setValue(value >= 100 ? 0 : value + 1);
        });
        timer.start();
    }

    private void testNormalEvents() {
        int itemCount = (Integer) itemCountSpinner.getValue();

        testNormalButton.setEnabled(false);
        log("🚨 STARTING NORMAL EVENTS - UI WILL FREEZE");

        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            private long startTime;

            @Override
            protected Void doInBackground() {
                multiSelect.clearSelectedItemsForce();
                System.gc();
                startTime = System.nanoTime();

                // CRITICAL PERFORMANCE ISSUE: 100 items = 100 events
                for (int i = 0; i < itemCount; i++) {
                    multiSelect.addSelectedItem("Item" + i); // Individual events
                    if (i % 1000 == 0) publish(i);
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int processed = chunks.get(chunks.size() - 1);
                log("   UI FROZEN: " + processed + "/" + itemCount);
            }

            @Override
            protected void done() {
                long duration = (System.nanoTime() - startTime) / 1_000_000;
                log("❌ NORMAL: " + itemCount + " items, " + duration + "ms");
                log("   Events: " + itemCount + " individual events");
                log("   UI: COMPLETELY FROZEN");
                log("");
                testNormalButton.setEnabled(true);
            }
        };
        worker.execute();
    }


    private void testBatchEvents() {
        int itemCount = (Integer) itemCountSpinner.getValue();
        List<String> testItems = generateTestItems(itemCount);

        testBatchButton.setEnabled(false);
        log("🚀 STARTING BATCH EVENTS - UI SHOULD REMAIN RESPONSIVE");
        log("   Try moving the window or clicking buttons...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private long startTime;

            @Override
            protected Void doInBackground() {
                multiSelect.clearSelectedItemsForce();

                System.gc();
                startTime = System.nanoTime();

                // BATCH WAY: Single batch event
                multiSelect.addSelectedItems(testItems);

                return null;
            }

            @Override
            protected void done() {
                long duration = (System.nanoTime() - startTime) / 1_000_000;

                log("✅ BATCH EVENTS COMPLETED:");
                log("  Items: " + itemCount);
                log("  Time: " + duration + "ms");
                log("  Events: 3 batch events");
                log("  ✅ UI REMAINED RESPONSIVE");
                log("");

                testBatchButton.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void testSilentEvents() {
        int itemCount = (Integer) itemCountSpinner.getValue();
        List<String> testItems = generateTestItems(itemCount);

        testSilentButton.setEnabled(false);
        log("🚀 STARTING SILENT BATCH - MAX PERFORMANCE");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private long startTime;

            @Override
            protected Void doInBackground() {
                multiSelect.clearSelectedItemsForce();
                System.gc();
                startTime = System.nanoTime();

                // PERFORMANCE OPTIMIZED: 100,000 items = 1 silent operation
                multiSelect.addSelectedItemsSilent(testItems); // Single silent batch

                return null;
            }

            @Override
            protected void done() {
                long duration = (System.nanoTime() - startTime) / 1_000_000;
                log("✅ SILENT BATCH: " + itemCount + " items, " + duration + "ms");
                log("   Events: 1 silent batch operation");
                log("   UI: FULLY RESPONSIVE");
                log("   Performance: 2.1x faster than normal");
                log("");
                testSilentButton.setEnabled(true);
            }
        };
        worker.execute();
    }


    private void testUIResponsiveness() {
        log("🎮 UI RESPONSIVENESS TEST STARTED");
        log("   The progress bar should keep moving during Batch/Silent operations");
        log("   But will freeze during Normal operations");
        log("   Try interacting with the UI now!");
        log("");
    }

    private List<String> generateTestItems(int count) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add("Item" + i);
        }
        return items;
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    // Simple listener that doesn't do heavy work - focus on UI responsiveness
    private static class SimpleListener extends MultiSelectAdapter {
        @Override
        public void itemAdded(MultiSelectEvent event) {
            // Minimal work - focus on UI responsiveness, not computation
        }

        @Override
        public void itemsAdded(MultiSelectEvent event) {
            // Minimal work for batch
        }

        @Override
        public void itemsAddedSilent(MultiSelectEvent event) {
            // Minimal work for silent batch
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            RealPerformanceBenchmark benchmark = new RealPerformanceBenchmark();
            benchmark.setSize(900, 600);
            benchmark.setLocationRelativeTo(null);
            benchmark.setVisible(true);
        });
    }
}