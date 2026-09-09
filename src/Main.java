import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Setup setup = askSetup();

            if (setup == null) {
                return;
            }

            Market market = new Market(setup.cash);

            market.addStock(
                    new Stock(
                            "JOON",
                            setup.joonPrice
                    )
            );

            market.addStock(
                    new Stock(
                            "HXMZA",
                            setup.hxmzaPrice
                    )
            );

            JFrame frame =
                    new JFrame(
                            "Stock Market Simulator"
                    );

            frame.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            frame.setSize(1360, 780);

            frame.setLayout(
                    new BorderLayout()
            );

            MarketPanel marketPanel =
                    new MarketPanel(market);

            JScrollPane scrollPane =
                    new JScrollPane(
                            marketPanel
                    );

            scrollPane
                    .getViewport()
                    .setBackground(
                            new Color(13, 16, 20)
                    );

            JButton startButton =
                    new JButton("Start");

            JButton pauseButton =
                    new JButton("Pause");

            JButton stepButton =
                    new JButton("Go one Step");

            JLabel tickLabel =
                    new JLabel("Tick: 0");

            tickLabel.setForeground(
                    new Color(210, 216, 224)
            );

            JPanel controls =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.LEFT
                            )
                    );

            controls.setBackground(
                    new Color(17, 21, 26)
            );

            controls.add(startButton);
            controls.add(pauseButton);
            controls.add(stepButton);
            controls.add(tickLabel);

            TradesPanel[] tradesPanelHolder =
                    new TradesPanel[1];

            Runnable repaintAll = () -> {

                marketPanel.repaint();

                tradesPanelHolder[0]
                        .refreshAccount();

                tradesPanelHolder[0]
                        .repaint();

                tickLabel.setText(
                        "Tick: " + market.getTick()
                );
            };

            tradesPanelHolder[0] =
                    new TradesPanel(
                            market,
                            repaintAll
                    );

            TradesPanel tradesPanel =
                    tradesPanelHolder[0];

            Timer timer =
                    new Timer(
                            500,
                            e -> runOneTick(
                                    market,
                                    repaintAll
                            )
                    );

            startButton.addActionListener(
                    e -> timer.start()
            );

            pauseButton.addActionListener(
                    e -> timer.stop()
            );

            stepButton.addActionListener(e -> {

                if (!timer.isRunning()) {

                    runOneTick(
                            market,
                            repaintAll
                    );
                }
            });

            frame.add(
                    controls,
                    BorderLayout.NORTH
            );

            frame.add(
                    scrollPane,
                    BorderLayout.CENTER
            );

            frame.add(
                    tradesPanel,
                    BorderLayout.EAST
            );

            frame.setVisible(true);
        });
    }

    private static void runOneTick(
            Market market,
            Runnable repaintAll
    ) {

        market.update();

        repaintAll.run();
    }

    private static Setup askSetup() {

        JTextField cashField =
                new JTextField("10000");

        JTextField joonField =
                new JTextField("50");

        JTextField hxmzaField =
                new JTextField("25");

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                0,
                                2,
                                8,
                                8
                        )
                );

        panel.add(
                new JLabel(
                        "Starting cash $"
                )
        );

        panel.add(cashField);

        panel.add(
                new JLabel(
                        "JOON (TICKER) starting price in $"
                )
        );

        panel.add(joonField);

        panel.add(
                new JLabel(
                        "HXMZA (TICKER) starting price in $"
                )
        );

        panel.add(hxmzaField);

        while (true) {

            int result =
                    JOptionPane.showConfirmDialog(
                            null,
                            panel,
                            "Set up the market",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (result != JOptionPane.OK_OPTION) {
                return null;
            }

            try {

                Setup setup =
                        new Setup();

                setup.cash =
                        parsePositive(
                                cashField.getText(),
                                "starting cash"
                        );

                setup.joonPrice =
                        parsePositive(
                                joonField.getText(),
                                "JOON price"
                        );

                setup.hxmzaPrice =
                        parsePositive(
                                hxmzaField.getText(),
                                "HXMZA price"
                        );

                return setup;

            } catch (OrderException e) {

                JOptionPane.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Invalid setup",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private static double parsePositive(
            String text,
            String label
    ) throws OrderException {

        try {

            double value =
                    Double.parseDouble(
                            text
                                    .trim()
                                    .replace("$", "")
                                    .replace(",", "")
                    );

            if (
                    value <= 0
                    || Double.isNaN(value)
                    || Double.isInfinite(value)
            ) {

                throw new OrderException(
                        label
                                + " must be greater than 0"
                );
            }

            return value;

        } catch (NumberFormatException e) {

            throw new OrderException(
                    "Enter a valid number for "
                            + label
            );
        }
    }

    private static class Setup {

        double cash;
        double joonPrice;
        double hxmzaPrice;
    }
}