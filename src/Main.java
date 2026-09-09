import java.awt.BorderLayout;
import java.awt.Color;
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

            scrollPane.getViewport().setBackground(
                    new Color(13, 16, 20)
            );

            JButton startButton =
                    new JButton("Start");

            JButton pauseButton =
                    new JButton("Pause");

            JButton stepButton =
                    new JButton("Step");

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

            TradesPanel[] tradesPanelHolder = new TradesPanel[1];

            Runnable repaintAll = () -> {
                marketPanel.repaint();
                tradesPanelHolder[0].refreshAccount();
                tradesPanelHolder[0].repaint();
                tickLabel.setText("Tick: " + market.getTick());
            };

            tradesPanelHolder[0] =
                    new TradesPanel(market, repaintAll);

            TradesPanel tradesPanel = tradesPanelHolder[0];

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
}
