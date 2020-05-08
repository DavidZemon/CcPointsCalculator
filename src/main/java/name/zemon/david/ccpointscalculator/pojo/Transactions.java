package name.zemon.david.ccpointscalculator.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@Builder(toBuilder = true, builderClassName = "Builder")
@AllArgsConstructor
@JsonDeserialize(builder = Transactions.Builder.class)
public class Transactions {
    @Nonnull
    @NotNull
    private final Collection<Transaction> transactions;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}
