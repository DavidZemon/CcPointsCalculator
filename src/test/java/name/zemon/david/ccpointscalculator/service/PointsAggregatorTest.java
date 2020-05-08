package name.zemon.david.ccpointscalculator.service;

import name.zemon.david.ccpointscalculator.pojo.Customer;
import name.zemon.david.ccpointscalculator.pojo.PointsAggregation;
import name.zemon.david.ccpointscalculator.pojo.Transaction;
import name.zemon.david.ccpointscalculator.pojo.Transactions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
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

    @Test
    void test_aggregate_oneTransaction_51Dollars() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(51))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(1)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(1)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_oneTransaction_100Dollars() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(100))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(50)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(50)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_oneTransaction_101Dollars() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(101))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(52)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(52)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_oneTransaction_120Dollars() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(120))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(90)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(90)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_oneTransaction_150Dollars() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(150))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(150)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(150)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_oneReturnTransaction_51Dollars() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(-51))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(-1)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(-1)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_oneReturnTransaction_100Dollars() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(-100))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(-50)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(-50)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_oneReturnTransaction_101Dollars() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(-101))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(-52)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(-52)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_oneReturnTransaction_120Dollars() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(-120))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(-90)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(-90)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_oneReturnTransaction_150Dollars() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(-150))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(-150)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(-150)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_oneTransaction_50_99499999() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(new BigDecimal("50.99499999999999999999"))
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

    @Test
    void test_aggregate_oneTransaction_100_99499999() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(new BigDecimal("100.99499999999999999999"))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(50)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(50)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_oneReturnTransaction_50_99499999() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(new BigDecimal("-50.99499999999999999999"))
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

    @Test
    void test_aggregate_oneReturnTransaction_100_99499999() {
        assertThat(this.testable.aggregate(
            Transactions.builder().transactions(
                Collections.singletonList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(new BigDecimal("-100.99499999999999999999"))
                        .build()
                )
            ).build()
        ))
            .isEqualTo(PointsAggregation.builder()
                           .customerCount(1)
                           .pointsTotal(-50)
                           .customers(
                               Collections.singletonMap(
                                   "customer1",
                                   Customer.builder()
                                       .id("customer1")
                                       .transactions(1)
                                       .points(-50)
                                       .build()
                               )
                           )
                           .build());
    }

    @Test
    void test_aggregate_comboTransactions() {
        final PointsAggregation actual = this.testable.aggregate(
            Transactions.builder().transactions(
                Arrays.asList(
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(55))
                        .build(),
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(90))
                        .build(),
                    Transaction.builder()
                        .id("customer1")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(120))
                        .build(),
                    Transaction.builder()
                        .id("customer2")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(55))
                        .build(),
                    Transaction.builder()
                        .id("customer2")
                        .date(Instant.now())
                        .value(BigDecimal.valueOf(-110))
                        .build()
                )
            ).build()
        );

        assertThat(actual.getCustomerCount()).isEqualTo(2);
        assertThat(actual.getPointsTotal()).isEqualTo(70);
        assertThat(actual.getCustomers())
            .hasSize(2)
            .containsKeys("customer1", "customer2");
        assertThat(actual.getCustomers().get("customer1").getId()).isEqualTo("customer1");
        assertThat(actual.getCustomers().get("customer1").getPoints()).isEqualTo(135);
        assertThat(actual.getCustomers().get("customer1").getTransactions()).isEqualTo(3);
        assertThat(actual.getCustomers().get("customer2").getId()).isEqualTo("customer2");
        assertThat(actual.getCustomers().get("customer2").getPoints()).isEqualTo(-65);
        assertThat(actual.getCustomers().get("customer2").getTransactions()).isEqualTo(2);
    }
}
