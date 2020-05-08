package name.zemon.david.ccpointscalculator.service;

import name.zemon.david.ccpointscalculator.pojo.Customer;
import name.zemon.david.ccpointscalculator.pojo.PointsAggregation;
import name.zemon.david.ccpointscalculator.pojo.Transaction;
import name.zemon.david.ccpointscalculator.pojo.Transactions;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class PointsAggregator {

    private static final BigDecimal FIFTY = BigDecimal.valueOf(50);

    @Nonnull
    public PointsAggregation aggregate(@Nonnull final Transactions transactions) {
        final Map<String, Collection<Transaction>> transactionMap = new HashMap<>();
        transactions.getTransactions().forEach(tx -> {
            final Collection<Transaction> existingTransactions = transactionMap.get(tx.getId());
            if (null == existingTransactions) {
                transactionMap.put(tx.getId(), new ArrayList<>(Collections.singletonList(tx)));
            } else {
                existingTransactions.add(tx);
            }
        });

        final long[]                pointsTotal = {0};
        final Map<String, Customer> customers   = new HashMap<>();
        transactionMap.forEach((id, customerTransactions) -> {
            final long points = customerTransactions.stream().mapToLong(tx -> {
                final boolean    isReturn = BigDecimal.ZERO.max(tx.getValue()).equals(BigDecimal.ZERO);
                final BigDecimal absolute = tx.getValue().abs();

                if (FIFTY.max(absolute).equals(absolute)) {
                    final BigDecimal rounded  = absolute.setScale(2, RoundingMode.HALF_EVEN);
                    final long       txPoints = rounded.longValue() - 50;
                    return isReturn ? txPoints * -1 : txPoints;
                } else {
                    return 0;
                }
            }).sum();
            pointsTotal[0] += points;
            customers.put(id, Customer.builder()
                                  .id(id)
                                  .transactions(customerTransactions.size())
                                  .points(points)
                                  .build());
        });

        return PointsAggregation.builder()
                   .customerCount(customers.size())
                   .pointsTotal(pointsTotal[0])
                   .customers(customers)
                   .build();
    }
}
