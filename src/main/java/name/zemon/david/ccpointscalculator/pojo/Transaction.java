package name.zemon.david.ccpointscalculator.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Transaction {
    @Nonnull
    private final String     id;
    // Always use BigDecimal for currency
    @Nonnull
    private final BigDecimal value;
    @Nonnull
    private final Instant    date;
}
