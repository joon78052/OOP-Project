import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Listeners hear a market event without Market calling traders or panels by name.
 * A new subscriber registers here. Market does not gain another direct call
 * when a panel or a trader starts caring about news.
 */
public class MarketEventBus {

    private final Set<MarketEventListener> listeners = new LinkedHashSet<>();

    public boolean add(MarketEventListener listener) {
        if (listener == null) {
            return false;
        }
        return listeners.add(listener);
    }

    public boolean remove(MarketEventListener listener) {
        return listeners.remove(listener);
    }

    public void clear() {
        listeners.clear();
    }

    public void publish(MarketEvent event) {
        List<MarketEventListener> snapshot = new ArrayList<>(listeners);
        for (MarketEventListener listener : snapshot) {
            listener.onMarketEvent(event);
        }
    }

    public int listenerCount() {
        return listeners.size();
    }
}
