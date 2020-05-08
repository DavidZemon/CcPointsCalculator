package name.zemon.david.ccpointscalculator;

import name.zemon.david.ccpointscalculator.pojo.Transactions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import javax.validation.Valid;

@RestController
public class PointsController {
    @PostMapping(
        value = "calculate",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void calculate(@Nonnull @Valid @RequestBody final Transactions transactions) {

    }
}
