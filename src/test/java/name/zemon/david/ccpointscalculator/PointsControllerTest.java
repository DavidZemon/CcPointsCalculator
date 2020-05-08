package name.zemon.david.ccpointscalculator;

import name.zemon.david.ccpointscalculator.pojo.PointsAggregation;
import name.zemon.david.ccpointscalculator.pojo.Transactions;
import name.zemon.david.ccpointscalculator.service.PointsAggregator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PointsControllerTest {
    @Mock
    private PointsAggregator mockPointsAggregator;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                           .standaloneSetup(new PointsController(this.mockPointsAggregator))
                           .build();
    }

    @Test
    void test_noContentHeader_shouldFail() throws Exception {
        this.mockMvc.perform(post("/calculate")
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()));
    }

    @Test
    void test_noRequestBody_shouldFail() throws Exception {
        this.mockMvc.perform(post("/calculate")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void test_emptyRequestBody_shouldFail() throws Exception {
        this.mockMvc.perform(post("/calculate")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content("{}")
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void test_emptyTransactionsList_shouldReturnSimpleResult() throws Exception {
        final Transactions expectedTransactions = Transactions.builder()
                                                      .transactions(Collections.emptyList())
                                                      .build();

        when(this.mockPointsAggregator.aggregate(eq(expectedTransactions)))
            .thenReturn(PointsAggregation.builder()
                            .customerCount(0)
                            .pointsTotal(0)
                            .customers(Collections.emptyMap())
                            .build());

        this.mockMvc.perform(post("/calculate")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(
                                     // language=JSON
                                     "{\n" +
                                         "  \"transactions\": []\n" +
                                         "}"
                                 )
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.OK.value()));
    }
}
