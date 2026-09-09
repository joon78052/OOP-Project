public class Player extends Trader {

    private Portfolio portfolio;

    public Player(String name, double cash) {
        super(name, cash);
        portfolio = new Portfolio();
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void spend(double usd) throws OrderException {
        if (usd > cash) {
            throw new OrderException("Not enough cash");
        }
        cash -= usd;
    }

    public void receive(double usd) {
        cash += usd;
    }

    @Override
    public void makeDecision() {
       
    }
}
