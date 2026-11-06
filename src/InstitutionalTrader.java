public class InstitutionalTrader extends Trader {

    public InstitutionalTrader(String name, double cash) {
        super(name, cash);
        setStrategy(new MeanReversionStrategy());
        setRecessionStrategy(new MeanReversionStrategy());
    }
}
