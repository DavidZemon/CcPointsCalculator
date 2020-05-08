package name.zemon.david.ccpointscalculator.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Customer {
    @Nonnull
    private final String id;
    @Nonnull
    private final String name;
    private final long   transactions;
    private final long   points;
}
