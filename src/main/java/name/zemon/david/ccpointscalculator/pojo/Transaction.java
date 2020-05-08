package name.zemon.david.ccpointscalculator.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Transaction {
    @Nonnull
    @NotNull
    private final String     id;
    // Always use BigDecimal for currency
    @Nonnull
    @NotNull
    private final BigDecimal value;
    @Nonnull
    @NotNull
    private final Instant    date;
}
