public class RetailTrader extends Trader {

    public RetailTrader(String name, double cash) {
        super(name, cash);
        setStrategy(new MomentumStrategy());
    }
}
