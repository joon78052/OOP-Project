import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarketEventGenerator {

    private final long seed;
    private final Random random;
    private final MarketEventBus bus;
    private final List<MarketEvent> events = new ArrayList<>();

    public MarketEventGenerator(long seed, MarketEventBus bus) {
        this.seed = seed;
        this.random = new Random(seed);
        this.bus = bus;
    }

    public void schedule(MarketEvent event) {
        events.add(event);
    }

    public void tick(int tick) {
        for (MarketEvent event : events) {
            if (event.getPhase() == MarketEventPhase.SCHEDULED && tick >= event.getStartTick()) {
                event.activate();
                bus.publish(event);
            }
            if (event.getPhase() == MarketEventPhase.ACTIVE && tick > event.getEndTick()) {
                event.expire();
                bus.publish(event);
            }
        }
    }

    public List<MarketEvent> getEvents() {
        return events;
    }

    public long getSeed() {
        return seed;
    }

    public Random getRandom() {
        return random;
    }
}
