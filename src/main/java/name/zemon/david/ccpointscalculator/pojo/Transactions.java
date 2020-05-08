package name.zemon.david.ccpointscalculator.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;
import java.util.Collection;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Transactions {
    @Nonnull
    private final Collection<Transaction> transactions;
}
