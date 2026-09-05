# Week 4 - Inheritance and overriding and overloading

From week 4 handout:

- Inheritance (extends) - shared fields/methods live in a superclass
- Interfaces  - Defining contracts that classes implement with implements
- Method overriding - Providing specialised behaviour in subclasses with @Override
- Method overloading - Multiple methods with the same name but different parameter lists
- Polymorphism - Treating objects of different subclasses through a common supertype

Trader:
- name, cash, constructor Trader(name, cash) 
(Subclasses call the super(name, cash) instead of duping the setup)
- makeDecision() (trade decision, how they decide is diff)

Subc lasses:
Retail Trader.make decision()
Institutional Trader.make decision()

- if we were to put both name and cash in the same subclass, we would copy the same fields twice. Inheritance extracts that shared state.

MarketPanel also inherits the Jpanel 


Overriding
@Override
method replaces the superclass version

- RetailTrader / InstitutionalTrader override makeDecision()
- MarketPanel overrides paintComponent(Graphics g)
- add smthn later


Polymorphism
- Market does not care about if the object is RetailTrader or an InstitionalTrader
- Update can do make Decision.