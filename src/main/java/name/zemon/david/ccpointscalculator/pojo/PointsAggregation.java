package name.zemon.david.ccpointscalculator.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class PointsAggregation {
    private final long                  customerCount;
    private final long                  pointsTotal;
    @Nonnull
    private final Map<String, Customer> customers;
}
