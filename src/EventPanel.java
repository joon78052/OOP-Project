import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class EventPanel extends JPanel {

    private static final Color BG = new Color(13, 16, 20);
    private static final Color HEADER_BG = new Color(17, 21, 26);
    private static final Color GRID = new Color(38, 44, 52);
    private static final Color TEXT = new Color(210, 216, 224);
    private static final Color LABEL = new Color(125, 135, 148);
    private static final Color DOWN = Candle.DOWN;

    private final Market market;
    private final Runnable onChange;
    private final JPanel eventList;
    private final JLabel statusLabel;

    public EventPanel(Market market, Runnable onChange) {
        this.market = market;
        this.onChange = onChange;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280, 0));
        setBackground(BG);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, GRID));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Events");
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setAlignmentX(LEFT_ALIGNMENT);

        JButton triggerButton = new JButton("Trigger recession");
        triggerButton.setAlignmentX(LEFT_ALIGNMENT);
        triggerButton.setBackground(DOWN.darker());
        triggerButton.setForeground(Color.WHITE);
        triggerButton.setFocusPainted(false);
        triggerButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        triggerButton.addActionListener(e -> triggerRecession());

        statusLabel = new JLabel("No active events");
        statusLabel.setForeground(LABEL);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(triggerButton);
        header.add(Box.createVerticalStrut(8));
        header.add(statusLabel);

        eventList = new JPanel();
        eventList.setLayout(new BoxLayout(eventList, BoxLayout.Y_AXIS));
        eventList.setBackground(BG);
        eventList.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(eventList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        List<MarketEvent> active = new ArrayList<>();
        for (MarketEvent event : market.getEventGenerator().getEvents()) {
            if (event.getPhase() == MarketEventPhase.ACTIVE) {
                active.add(event);
            }
        }

        eventList.removeAll();
        if (active.isEmpty()) {
            eventList.add(row("No active events", LABEL));
            statusLabel.setText("No active events");
        } else {
            statusLabel.setText(active.size() + " active");
            for (MarketEvent event : active) {
                int ticksLeft = event.getEndTick() - market.getTick();
                String text = event.getType().getLabel() + "  ·  " + Math.max(0, ticksLeft) + " ticks left";
                eventList.add(row(text, DOWN));
                eventList.add(Box.createVerticalStrut(6));
            }
        }
        eventList.revalidate();
        eventList.repaint();
    }

    private void triggerRecession() {
        market.getEventGenerator().triggerRecession(market.getTick());
        onChange.run();
    }

    private JLabel row(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }
}
