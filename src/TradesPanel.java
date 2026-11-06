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
    private final JComboBox<String> typeSelector;
    private final JTextField quantityField;
    private final JTextField limitPriceField;
    private final JLabel cashLabel;
    private final JLabel heldLabel;
    private final JLabel bookLabel;
    private final JLabel statusLabel;
    private final JLabel reportLabel;
    private long lastPlayerOrderId;

    public TradesPanel(Market market, Runnable onTrade) {
        this.market = market;
        this.onTrade = onTrade;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(380, 0));
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

        stockSelector = comboBox();
        for (Stock stock : market.getStocks()) {
            stockSelector.addItem(stock.getSymbol());
        }
        stockSelector.addActionListener(e -> refreshAccount());

        typeSelector = comboBox();
        typeSelector.addItem("MARKET");
        typeSelector.addItem("LIMIT");
        typeSelector.addActionListener(e -> updateLimitFieldState());

        cashLabel = mutedLabel("");
        heldLabel = mutedLabel("");
        bookLabel = mutedLabel("");
        statusLabel = mutedLabel("Enter shares, then Buy or Sell.");
        reportLabel = mutedLabel("No order report yet.");
        reportLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        quantityField = textField("25");
        limitPriceField = textField("50.00");

        JPanel quantityRow = labeledRow("Shares", quantityField);
        JPanel limitRow = labeledRow("Limit $", limitPriceField);

        JPanel buySellRow = new JPanel(new GridLayout(1, 2, 8, 0));
        buySellRow.setBackground(HEADER_BG);
        buySellRow.setAlignmentX(LEFT_ALIGNMENT);
        buySellRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JButton buyButton = makeOrderButton("Buy", UP);
        JButton sellButton = makeOrderButton("Sell", DOWN);
        buyButton.addActionListener(e -> placeOrder(OrderSide.BUY));
        sellButton.addActionListener(e -> placeOrder(OrderSide.SELL));
        buySellRow.add(buyButton);
        buySellRow.add(sellButton);

        JPanel actionRow = new JPanel(new GridLayout(1, 2, 8, 0));
        actionRow.setBackground(HEADER_BG);
        actionRow.setAlignmentX(LEFT_ALIGNMENT);
        actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JButton cancelButton = makeChip("Cancel last");
        cancelButton.addActionListener(e -> cancelLastOrder());
        JButton percentButton = makeChip("Sell 50%");
        percentButton.addActionListener(e -> placePercentSell(0.50));
        actionRow.add(cancelButton);
        actionRow.add(percentButton);

        JPanel presetRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        presetRow.setBackground(HEADER_BG);
        presetRow.setAlignmentX(LEFT_ALIGNMENT);
        presetRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        int[] presets = {10, 25, 50, 100};
        for (int preset : presets) {
            JButton chip = makeChip(String.valueOf(preset));
            final int value = preset;
            chip.addActionListener(e -> quantityField.setText(String.valueOf(value)));
            presetRow.add(chip);
        }

        orderEntry.add(title);
        orderEntry.add(Box.createVerticalStrut(6));
        orderEntry.add(stockSelector);
        orderEntry.add(Box.createVerticalStrut(4));
        orderEntry.add(typeSelector);
        orderEntry.add(Box.createVerticalStrut(6));
        orderEntry.add(cashLabel);
        orderEntry.add(heldLabel);
        orderEntry.add(bookLabel);
        orderEntry.add(Box.createVerticalStrut(8));
        orderEntry.add(quantityRow);
        orderEntry.add(Box.createVerticalStrut(4));
        orderEntry.add(presetRow);
        orderEntry.add(Box.createVerticalStrut(4));
        orderEntry.add(limitRow);
        orderEntry.add(Box.createVerticalStrut(8));
        orderEntry.add(buySellRow);
        orderEntry.add(Box.createVerticalStrut(6));
        orderEntry.add(actionRow);
        orderEntry.add(Box.createVerticalStrut(8));
        orderEntry.add(statusLabel);
        orderEntry.add(Box.createVerticalStrut(4));
        orderEntry.add(reportLabel);

        add(orderEntry, BorderLayout.NORTH);
        add(new FeedPanel(), BorderLayout.CENTER);

        updateLimitFieldState();
        refreshAccount();
    }

    public void refreshAccount() {
        Stock stock = selectedStock();
        Player player = market.getPlayer();
        cashLabel.setText("Cash  $" + String.format("%.2f", player.getAvailableCash())
                + " available");
        if (stock == null) {
            heldLabel.setText("Held  --");
            bookLabel.setText("Book  --");
            return;
        }
        double shares = player.getPortfolio().getAvailableShares(stock);
        double value = shares * stock.getPrice();
        heldLabel.setText("Held  " + String.format("%.2f", shares)
                + "  ($" + String.format("%.2f", value) + ")");

        LiquiditySnapshot liquidity = market.getLiquiditySnapshot(stock.getSymbol());
        bookLabel.setText("Book  bid "
                + money(liquidity.getBestBid())
                + " / ask "
                + money(liquidity.getBestAsk())
                + "  spr "
                + money(liquidity.getSpread()));

        if (limitPriceField.isEnabled()) {
            limitPriceField.setText(String.format("%.2f", stock.getPrice()));
        }
    }

    private void placeOrder(OrderSide side) {
        Stock stock = selectedStock();
        if (stock == null) {
            return;
        }
        try {
            double quantity = readPositive(quantityField.getText(), "share quantity");
            OrderType type = selectedType();
            double limitPrice = type == OrderType.LIMIT
                    ? readPositive(limitPriceField.getText(), "limit price")
                    : Double.NaN;
            OrderExecutionReport report = market.submitPlayerOrder(
                    stock,
                    side,
                    type,
                    quantity,
                    limitPrice,
                    12);
            showReport(report);
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
            double quantity = market.getPlayer().getPortfolio().getAvailableShares(stock) * percent;
            if (quantity <= 0) {
                throw new OrderException("No available shares to sell");
            }
            quantityField.setText(String.format("%.2f", quantity));
            placeOrder(OrderSide.SELL);
        } catch (OrderException e) {
            showStatus(e.getMessage(), true);
        }
    }

    private void cancelLastOrder() {
        Stock stock = selectedStock();
        if (stock == null || lastPlayerOrderId == 0) {
            showStatus("No resting player order to cancel", true);
            return;
        }
        OrderExecutionReport report = market.cancelPlayerOrder(
                lastPlayerOrderId,
                stock.getSymbol());
        if (report == null) {
            showStatus("Order already finished or not found", true);
            return;
        }
        showReport(report);
        onTrade.run();
    }

    private void showReport(OrderExecutionReport report) {
        OrderSnapshot order = report.getOrder();
        lastPlayerOrderId = order.isOpen() ? order.getOrderId() : 0;

        String headline = order.getSide() + " ORDER " + order.getStatus();
        if (report.isPartial() || order.getStatus() == OrderStatus.CANCELLED) {
            headline = order.getSide() + " ORDER "
                    + (order.getFilledQuantity() > 0 ? "PARTIALLY FILLED" : order.getStatus());
        }
        showStatus(headline, order.getStatus() == OrderStatus.REJECTED);

        reportLabel.setText("<html>Requested: " + format(order.getOriginalQuantity())
                + " shares<br>Executed: " + format(order.getFilledQuantity())
                + " shares<br>Unfilled: " + format(order.getRemainingQuantity())
                + " shares<br>Avg price: " + money(order.getAverageExecutionPrice())
                + "<br>Reason: " + order.getStatusReason() + "</html>");
    }

    private void updateLimitFieldState() {
        boolean limit = selectedType() == OrderType.LIMIT;
        limitPriceField.setEnabled(limit);
        limitPriceField.setBackground(limit ? BG : new Color(28, 32, 38));
        if (limit) {
            Stock stock = selectedStock();
            if (stock != null) {
                limitPriceField.setText(String.format("%.2f", stock.getPrice()));
            }
        }
    }

    private OrderType selectedType() {
        return "LIMIT".equals(typeSelector.getSelectedItem())
                ? OrderType.LIMIT
                : OrderType.MARKET;
    }

    private JComboBox<String> comboBox() {
        JComboBox<String> box = new JComboBox<>();
        box.setBackground(BG);
        box.setForeground(TEXT);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        box.setAlignmentX(LEFT_ALIGNMENT);
        return box;
    }

    private JTextField textField(String value) {
        JTextField field = new JTextField(value);
        field.setBackground(BG);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRID),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(LEFT_ALIGNMENT);
        return field;
    }

    private JPanel labeledRow(String caption, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(HEADER_BG);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.add(mutedLabel(caption), BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return row;
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

    private double readPositive(String text, String label) throws OrderException {
        try {
            double value = Double.parseDouble(text.trim().replace("$", "").replace(",", ""));
            if (value <= 0 || Double.isNaN(value) || Double.isInfinite(value)) {
                throw new OrderException("Enter a " + label + " greater than 0");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new OrderException("Enter a valid " + label);
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

    private String money(double value) {
        return "$" + String.format("%.2f", value);
    }

    private String format(double value) {
        return String.format("%.2f", value);
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

                boolean isPlayer =
                        trade.getBuyer() == market.getPlayer()
                        || trade.getSeller() == market.getPlayer();
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
