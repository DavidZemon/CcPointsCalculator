package name.zemon.david.ccpointscalculator.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Transactions {
    @Nonnull
    @NotNull
    private final Collection<Transaction> transactions;
}
