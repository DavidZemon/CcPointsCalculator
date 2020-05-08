package name.zemon.david.ccpointscalculator.service;

import name.zemon.david.ccpointscalculator.pojo.PointsAggregation;
import name.zemon.david.ccpointscalculator.pojo.Transactions;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class PointsAggregator {
    @Nonnull
    public PointsAggregation aggregate(@Nonnull final Transactions transactions) {
        return null;
    }
}
