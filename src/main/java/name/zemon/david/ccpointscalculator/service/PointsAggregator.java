package name.zemon.david.ccpointscalculator.service;

import name.zemon.david.ccpointscalculator.pojo.Customer;
import name.zemon.david.ccpointscalculator.pojo.PointsAggregation;
import name.zemon.david.ccpointscalculator.pojo.Transaction;
import name.zemon.david.ccpointscalculator.pojo.Transactions;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.*;

@Component
public class PointsAggregator {
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
            final long points = customerTransactions.stream().mapToLong(tx -> 0).sum();
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
