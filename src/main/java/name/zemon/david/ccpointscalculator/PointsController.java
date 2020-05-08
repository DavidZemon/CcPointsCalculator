package name.zemon.david.ccpointscalculator;

import name.zemon.david.ccpointscalculator.pojo.PointsAggregation;
import name.zemon.david.ccpointscalculator.pojo.Transactions;
import name.zemon.david.ccpointscalculator.service.PointsAggregator;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import javax.validation.Valid;

@RestController
public class PointsController {
    @Nonnull
    private final PointsAggregator pointsAggregator;

    public PointsController(@Nonnull final PointsAggregator pointsAggregator) {
        this.pointsAggregator = pointsAggregator;
    }

    @PostMapping(
        value = "calculate",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Nonnull
    public PointsAggregation calculate(@Nonnull @Valid @RequestBody final Transactions transactions) {
        return this.pointsAggregator.aggregate(transactions);
    }
}
