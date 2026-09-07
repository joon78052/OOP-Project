import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Market market = new Market();

            market.addStock(
                    new Stock(
                            "JOON",
                            100.00
                    )
            );

            market.addStock(
                    new Stock(
                            "HXMZA",
                            75.00
                    )
            );

            JFrame frame =
                    new JFrame(
                            "Stock Market Simulator"
                    );

            frame.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            frame.setSize(1020, 740);

            frame.setLayout(
                    new BorderLayout()
            );

            MarketPanel marketPanel =
                    new MarketPanel(market);

            JScrollPane scrollPane =
                    new JScrollPane(
                            marketPanel
                    );

            JButton startButton =
                    new JButton("Start");

            JButton pauseButton =
                    new JButton("Pause");

            JButton stepButton =
                    new JButton("Step");

            JLabel tickLabel =
                    new JLabel("Tick: 0");

            JPanel controls =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.LEFT
                            )
                    );

            controls.add(startButton);
            controls.add(pauseButton);
            controls.add(stepButton);
            controls.add(tickLabel);

            Timer timer =
                    new Timer(
                            500,
                            e -> runOneTick(
                                    market,
                                    marketPanel,
                                    tickLabel
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
                            marketPanel,
                            tickLabel
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

            frame.setVisible(true);
        });
    }

    private static void runOneTick(
            Market market,
            MarketPanel marketPanel,
            JLabel tickLabel
    ) {

        market.update();

        marketPanel.repaint();

        tickLabel.setText(
                "Tick: "
                        + market.getTick()
        );
    }
}