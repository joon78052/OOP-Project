import java.util.List;

public final class OrderExecutionReport {

    private final OrderSnapshot order;
    private final List<Trade> executions;

    public OrderExecutionReport(Order order, List<Trade> executions) {
        this.order = new OrderSnapshot(order);
        this.executions = List.copyOf(executions);
    }

    public OrderSnapshot getOrder() {
        return order;
    }

    public List<Trade> getExecutions() {
        return executions;
    }

    public boolean isPartial() {
        return order.getStatus() == OrderStatus.PARTIAL
                || (order.getFilledQuantity() > 0 && order.getRemainingQuantity() > 0);
    }
}
