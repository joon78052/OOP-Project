import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarketEventGenerator {

    private static final double RECESSION_CHANCE = 0.05;

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
        maybeScheduleRecession(tick);

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

    public MarketEvent getActiveEvent() {
        for (MarketEvent event : events) {
            if (event.getPhase() == MarketEventPhase.ACTIVE) {
                return event;
            }
        }
        return null;
    }

    public MarketEvent triggerRecession(int tick) {
        MarketEvent event = createRecession(tick, 0);
        event.activate();
        bus.publish(event);
        return event;
    }

    private void maybeScheduleRecession(int tick) {
        if (hasOpenRecession() || random.nextDouble() > RECESSION_CHANCE) {
            return;
        }
        createRecession(tick, random.nextInt(6));
    }

    private MarketEvent createRecession(int tick, int delay) {
        int duration = 8 + random.nextInt(13);
        int start = tick + delay;
        MarketEvent event = new MarketEvent(
                MarketEventType.ECONOMIC_RECESSION,
                MarketEventSeverity.SEVERE,
                null,
                -0.30,
                start,
                start + duration - 1
        );
        schedule(event);
        return event;
    }

    private boolean hasOpenRecession() {
        for (MarketEvent event : events) {
            if (event.getType() != MarketEventType.ECONOMIC_RECESSION) {
                continue;
            }
            if (event.getPhase() != MarketEventPhase.EXPIRED) {
                return true;
            }
        }
        return false;
    }
}
