package name.zemon.david.ccpointscalculator.service;

import name.zemon.david.ccpointscalculator.pojo.Customer;
import name.zemon.david.ccpointscalculator.pojo.PointsAggregation;
import name.zemon.david.ccpointscalculator.pojo.Transaction;
import name.zemon.david.ccpointscalculator.pojo.Transactions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class PointsAggregatorTest {
    private PointsAggregator testable;

    @BeforeEach
    void setUp() {
        this.testable = new PointsAggregator();
    }

    @Test
    void test_aggregate_noTransactions_emptyAggregation() {
        assertThat(this.testable.aggregate(Transactions.builder().transactions(Collections.emptyList()).build()))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(0)
                           .pointsTotal(0)
                           .customers(Collections.emptyMap())
                           .build());
    }

    @Test
    void test_aggregate_oneTransaction_zeroDollars() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.ZERO)
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(0)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(0)
                                       .build()
                               )
                           )
                           .build());
    }
}
