import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TradesPanel extends JPanel {

    private static final Color BG = new Color(13, 16, 20);
    private static final Color HEADER_BG = new Color(17, 21, 26);
    private static final Color GRID = new Color(38, 44, 52);
    private static final Color TEXT = new Color(210, 216, 224);
    private static final Color LABEL = new Color(125, 135, 148);
    private static final Color UP = Candle.UP;
    private static final Color DOWN = Candle.DOWN;

    private final Market market;
    private final Runnable onTrade;
    private final JComboBox<String> stockSelector;
    private final JTextField amountField;
    private final JLabel cashLabel;
    private final JLabel heldLabel;
    private final JLabel statusLabel;

    public TradesPanel(Market market, Runnable onTrade) {
        this.market = market;
        this.onTrade = onTrade;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(360, 0));
        setBackground(BG);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, GRID));

        JPanel orderEntry = new JPanel();
        orderEntry.setLayout(new BoxLayout(orderEntry, BoxLayout.Y_AXIS));
        orderEntry.setBackground(HEADER_BG);
        orderEntry.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Order");
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setAlignmentX(LEFT_ALIGNMENT);

        stockSelector = new JComboBox<>();
        for (Stock stock : market.getStocks()) {
            stockSelector.addItem(stock.getSymbol());
        }
        stockSelector.setBackground(BG);
        stockSelector.setForeground(TEXT);
        stockSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        stockSelector.setAlignmentX(LEFT_ALIGNMENT);
        stockSelector.addActionListener(e -> refreshAccount());

        cashLabel = mutedLabel("");
        heldLabel = mutedLabel("");
        statusLabel = mutedLabel("Enter an amount, then Buy or Sell.");

        amountField = new JTextField("500");
        amountField.setBackground(BG);
        amountField.setForeground(TEXT);
        amountField.setCaretColor(TEXT);
        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRID),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        amountField.setAlignmentX(LEFT_ALIGNMENT);

        JPanel amountRow = new JPanel(new BorderLayout(6, 0));
        amountRow.setBackground(HEADER_BG);
        amountRow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel amountCaption = mutedLabel("Amount $");
        amountRow.add(amountCaption, BorderLayout.WEST);
        amountRow.add(amountField, BorderLayout.CENTER);
        amountRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel buySellRow = new JPanel(new GridLayout(1, 2, 8, 0));
        buySellRow.setBackground(HEADER_BG);
        buySellRow.setAlignmentX(LEFT_ALIGNMENT);
        buySellRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JButton buyButton = makeOrderButton("Buy", UP);
        JButton sellButton = makeOrderButton("Sell", DOWN);
        buyButton.addActionListener(e -> placeUsdOrder(true));
        sellButton.addActionListener(e -> placeUsdOrder(false));
        buySellRow.add(buyButton);
        buySellRow.add(sellButton);

        JLabel sellCaption = mutedLabel("Sell position");
        JPanel percentRow = new JPanel(new GridLayout(1, 4, 6, 0));
        percentRow.setBackground(HEADER_BG);
        percentRow.setAlignmentX(LEFT_ALIGNMENT);
        percentRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        percentRow.add(makePercentButton("10%", 0.10));
        percentRow.add(makePercentButton("20%", 0.20));
        percentRow.add(makePercentButton("50%", 0.50));
        percentRow.add(makePercentButton("100%", 1.00));

        JPanel presetRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        presetRow.setBackground(HEADER_BG);
        presetRow.setAlignmentX(LEFT_ALIGNMENT);
        presetRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        int[] presets = {100, 250, 500, 1000};
        for (int preset : presets) {
            JButton chip = makeChip("$" + preset);
            final int value = preset;
            chip.addActionListener(e -> amountField.setText(String.valueOf(value)));
            presetRow.add(chip);
        }

        orderEntry.add(title);
        orderEntry.add(Box.createVerticalStrut(6));
        orderEntry.add(stockSelector);
        orderEntry.add(Box.createVerticalStrut(6));
        orderEntry.add(cashLabel);
        orderEntry.add(heldLabel);
        orderEntry.add(Box.createVerticalStrut(8));
        orderEntry.add(amountRow);
        orderEntry.add(Box.createVerticalStrut(4));
        orderEntry.add(presetRow);
        orderEntry.add(Box.createVerticalStrut(8));
        orderEntry.add(buySellRow);
        orderEntry.add(Box.createVerticalStrut(8));
        orderEntry.add(sellCaption);
        orderEntry.add(Box.createVerticalStrut(4));
        orderEntry.add(percentRow);
        orderEntry.add(Box.createVerticalStrut(6));
        orderEntry.add(statusLabel);

        add(orderEntry, BorderLayout.NORTH);
        add(new FeedPanel(), BorderLayout.CENTER);

        refreshAccount();
    }

    public void refreshAccount() {
        Stock stock = selectedStock();
        Player player = market.getPlayer();
        cashLabel.setText("Cash  $" + String.format("%.2f", player.getCash()));
        if (stock == null) {
            heldLabel.setText("Held  --");
            return;
        }
        double shares = player.getPortfolio().getShares(stock);
        double value = shares * stock.getPrice();
        heldLabel.setText("Held  " + String.format("%.2f", shares)
                + "  ($" + String.format("%.2f", value) + ")");
    }

    private JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(LABEL);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JButton makeOrderButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color.darker());
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return button;
    }

    private JButton makePercentButton(String text, double percent) {
        JButton button = makeChip(text);
        button.addActionListener(e -> placePercentSell(percent));
        return button;
    }

    private JButton makeChip(String text) {
        JButton button = new JButton(text);
        button.setBackground(BG);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRID),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        return button;
    }

    private void placeUsdOrder(boolean isBuy) {
        Stock stock = selectedStock();
        if (stock == null) {
            return;
        }
        try {
            double usd = readAmount();
            if (isBuy) {
                market.playerBuy(stock, usd);
            } else {
                market.playerSellUsd(stock, usd);
            }
            showStatus(isBuy ? "Bought $" + String.format("%.2f", usd) : "Sold $" + String.format("%.2f", usd), false);
            onTrade.run();
        } catch (OrderException | NumberFormatException e) {
            showStatus(e.getMessage(), true);
        }
    }

    private void placePercentSell(double percent) {
        Stock stock = selectedStock();
        if (stock == null) {
            return;
        }
        try {
            market.playerSellPercent(stock, percent);
            showStatus("Sold " + (int) (percent * 100) + "% of " + stock.getSymbol(), false);
            onTrade.run();
        } catch (OrderException e) {
            showStatus(e.getMessage(), true);
        }
    }

    private double readAmount() throws OrderException {
        String text = amountField.getText().trim().replace("$", "").replace(",", "");
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new OrderException("Enter a valid dollar amount");
        }
    }

    private Stock selectedStock() {
        String symbol = (String) stockSelector.getSelectedItem();
        if (symbol == null) {
            return null;
        }
        for (Stock stock : market.getStocks()) {
            if (stock.getSymbol().equals(symbol)) {
                return stock;
            }
        }
        return null;
    }

    private void showStatus(String message, boolean error) {
        statusLabel.setForeground(error ? DOWN : LABEL);
        statusLabel.setText(message);
    }

    private class FeedPanel extends JPanel {

        private final Font headerFont = new Font("SansSerif", Font.BOLD, 11);
        private final Font rowFont = new Font("Monospaced", Font.PLAIN, 11);

        private static final int COL_AGE = 8;
        private static final int COL_TYPE = 44;
        private static final int COL_MC = 88;
        private static final int COL_AMOUNT = 152;
        private static final int COL_TOTAL = 216;
        private static final int COL_TRADER = 292;

        FeedPanel() {
            setBackground(BG);
        }

        @Override
        protected void paintComponent(Graphics Graph) {
            super.paintComponent(Graph);
            Graphics2D Graph2D = (Graphics2D) Graph;
            Graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Graph2D.setColor(HEADER_BG);
            Graph2D.fillRect(0, 0, getWidth(), 22);
            Graph2D.setColor(GRID);
            Graph2D.drawLine(0, 22, getWidth(), 22);

            Graph2D.setFont(headerFont);
            Graph2D.setColor(LABEL);
            Graph2D.drawString("Age", COL_AGE, 15);
            Graph2D.drawString("Type", COL_TYPE, 15);
            Graph2D.drawString("MC", COL_MC, 15);
            Graph2D.drawString("Amount", COL_AMOUNT, 15);
            Graph2D.drawString("Total USD", COL_TOTAL, 15);
            Graph2D.drawString("Trader", COL_TRADER, 15);

            ArrayList<Trade> trades = market.getTrades();
            Graph2D.setFont(rowFont);
            int rowHeight = 22;
            int y = 22 + rowHeight;

            for (int i = trades.size() - 1; i >= 0; i--) {
                if (y > getHeight() + rowHeight) {
                    break;
                }
                Trade trade = trades.get(i);
                Color sideColor = trade.isBuy() ? UP : DOWN;

                String total = "$" + compact(trade.getTotalUsd());
                int totalWidth = Graph2D.getFontMetrics().stringWidth(total);
                Graph2D.setColor(new Color(sideColor.getRed(), sideColor.getGreen(),
                        sideColor.getBlue(), 45));
                Graph2D.fillRect(COL_TOTAL - 3, y - 13, totalWidth + 6, 16);

                Graph2D.setColor(LABEL);
                Graph2D.drawString(age(trade), COL_AGE, y);

                Graph2D.setColor(sideColor);
                Graph2D.drawString(trade.isBuy() ? "Buy" : "Sell", COL_TYPE, y);

                Graph2D.setColor(TEXT);
                Graph2D.drawString("$" + compact(trade.getMarketCap()), COL_MC, y);
                Graph2D.drawString(compact(trade.getQuantity()), COL_AMOUNT, y);

                Graph2D.setColor(sideColor);
                Graph2D.drawString(total, COL_TOTAL, y);

                boolean isPlayer = trade.getTraderTag().equals(market.getPlayer().getName());
                Graph2D.setColor(isPlayer ? UP : LABEL);
                Graph2D.drawString(trade.getTraderTag(), COL_TRADER, y);

                Graph2D.setColor(new Color(GRID.getRed(), GRID.getGreen(), GRID.getBlue(), 90));
                Graph2D.drawLine(0, y + 6, getWidth(), y + 6);

                y += rowHeight;
            }
        }

        private String age(Trade trade) {
            int ticksAgo = market.getTick() - trade.getTick();
            return (ticksAgo / 2) + "s";
        }

        private String compact(double value) {
            if (value >= 1_000_000_000) {
                return String.format("%.2fB", value / 1_000_000_000);
            }
            if (value >= 1_000_000) {
                return String.format("%.2fM", value / 1_000_000);
            }
            if (value >= 1_000) {
                return String.format("%.1fK", value / 1_000);
            }
            return String.format("%.2f", value);
        }
    }
}
