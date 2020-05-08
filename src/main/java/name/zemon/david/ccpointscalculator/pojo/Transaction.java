package name.zemon.david.ccpointscalculator.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder(toBuilder = true, builderClassName = "Builder")
@AllArgsConstructor
@JsonDeserialize(builder = Transaction.Builder.class)
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

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}
