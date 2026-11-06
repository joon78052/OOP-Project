import java.util.Random;

/**
 * A trading rule swap
 */
@FunctionalInterface
public interface TradingStrategy {

    Order decide(Trader trader, Stock stock, Random random, int tick);
}
