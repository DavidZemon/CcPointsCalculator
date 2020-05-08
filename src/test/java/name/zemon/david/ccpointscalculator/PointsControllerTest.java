package name.zemon.david.ccpointscalculator;

import name.zemon.david.ccpointscalculator.pojo.Customer;
import name.zemon.david.ccpointscalculator.pojo.PointsAggregation;
import name.zemon.david.ccpointscalculator.pojo.Transaction;
import name.zemon.david.ccpointscalculator.pojo.Transactions;
import name.zemon.david.ccpointscalculator.service.PointsAggregator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PointsController.class)
class PointsControllerTest {
    private static final String SAMPLE_ID_1      = "someId";
    private static final String SAMPLE_VALUE_1   = "12.34";
    private static final long   SAMPLE_DATE_1_MS = 12345678900L;
    private static final String SAMPLE_DATE_1_S  = "12345678.9";
    private static final String SAMPLE_ID_2      = "someId2";
    private static final String SAMPLE_VALUE_2   = "56.78";
    private static final long   SAMPLE_DATE_2_MS = 10002345678912L;
    private static final String SAMPLE_DATE_2_S  = "10002345678.912";

    @MockBean
    private PointsAggregator mockPointsAggregator;

    @Autowired
    private MockMvc mockMvc;

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
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.customer_count", is(0)))
            .andExpect(jsonPath("$.points_total", is(0)))
            .andExpect(jsonPath("$.customers").isMap())
            .andExpect(jsonPath("$.customers.length()", is(0)));
    }

    @Test
    void test_txMissingId_shouldFail() throws Exception {
        this.mockMvc.perform(post("/calculate")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(
                                     // language=JSON
                                     "{\n" +
                                         "  \"transactions\": [\n" +
                                         "    {\n" +
                                         "      \"value\": 12.34,\n" +
                                         "      \"date\": 12345678\n" +
                                         "    }\n" +
                                         "  ]\n" +
                                         "}"
                                 )
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void test_txMissingValue_shouldFail() throws Exception {
        this.mockMvc.perform(post("/calculate")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(
                                     // language=JSON
                                     "{\n" +
                                         "  \"transactions\": [\n" +
                                         "    {\n" +
                                         "      \"id\": \"some id\",\n" +
                                         "      \"date\": 12345678\n" +
                                         "    }\n" +
                                         "  ]\n" +
                                         "}"
                                 )
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void test_txMissingDate_shouldFail() throws Exception {
        this.mockMvc.perform(post("/calculate")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(
                                     // language=JSON
                                     "{\n" +
                                         "  \"transactions\": [\n" +
                                         "    {\n" +
                                         "      \"id\": \"some id\",\n" +
                                         "      \"value\": 12.34\n" +
                                         "    }\n" +
                                         "  ]\n" +
                                         "}"
                                 )
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void test_txNullId_shouldFail() throws Exception {
        this.mockMvc.perform(post("/calculate")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(
                                     // language=JSON
                                     "{\n" +
                                         "  \"transactions\": [\n" +
                                         "    {\n" +
                                         "      \"id\": null,\n" +
                                         "      \"value\": 12.34,\n" +
                                         "      \"date\": 12345678\n" +
                                         "    }\n" +
                                         "  ]\n" +
                                         "}"
                                 )
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void test_txNullValue_shouldFail() throws Exception {
        this.mockMvc.perform(post("/calculate")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(
                                     // language=JSON
                                     "{\n" +
                                         "  \"transactions\": [\n" +
                                         "    {\n" +
                                         "      \"id\": \"some id\",\n" +
                                         "      \"value\": null,\n" +
                                         "      \"date\": 12345678\n" +
                                         "    }\n" +
                                         "  ]\n" +
                                         "}"
                                 )
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void test_txNullDate_shouldFail() throws Exception {
        this.mockMvc.perform(post("/calculate")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(
                                     // language=JSON
                                     "{\n" +
                                         "  \"transactions\": [\n" +
                                         "    {\n" +
                                         "      \"id\": \"some id\",\n" +
                                         "      \"value\": 12.34,\n" +
                                         "      \"date\": null\n" +
                                         "    }\n" +
                                         "  ]\n" +
                                         "}"
                                 )
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void test_oneCompleteTransaction_shouldForwardToAggregator() throws Exception {
        final Transactions expectedTransactions = Transactions.builder()
                                                      .transactions(Collections.singletonList(
                                                          Transaction.builder()
                                                              .id(SAMPLE_ID_1)
                                                              .value(new BigDecimal(SAMPLE_VALUE_1))
                                                              .date(Instant.ofEpochMilli(SAMPLE_DATE_1_MS))
                                                              .build()
                                                      ))
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
                                         "  \"transactions\": [\n" +
                                         "    {\n" +
                                         "      \"id\": \"" + SAMPLE_ID_1 + "\",\n" +
                                         "      \"value\": " + SAMPLE_VALUE_1 + ",\n" +
                                         "      \"date\": " + SAMPLE_DATE_1_S + "\n" +
                                         "    }\n" +
                                         "  ]\n" +
                                         "}"
                                 )
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.customer_count", is(0)))
            .andExpect(jsonPath("$.points_total", is(0)))
            .andExpect(jsonPath("$.customers").isMap())
            .andExpect(jsonPath("$.customers.length()", is(0)));
    }

    @Test
    void test_multipleTransactions_canRespondWithMultipleCustomers() throws Exception {
        final Transactions expectedTransactions = Transactions.builder()
                                                      .transactions(Arrays.asList(
                                                          Transaction.builder()
                                                              .id(SAMPLE_ID_1)
                                                              .value(new BigDecimal(SAMPLE_VALUE_1))
                                                              .date(Instant.ofEpochMilli(SAMPLE_DATE_1_MS))
                                                              .build(),
                                                          Transaction.builder()
                                                              .id(SAMPLE_ID_2)
                                                              .value(new BigDecimal(SAMPLE_VALUE_2))
                                                              .date(Instant.ofEpochMilli(SAMPLE_DATE_2_MS))
                                                              .build()
                                                      ))
                                                      .build();

        when(this.mockPointsAggregator.aggregate(eq(expectedTransactions)))
            .thenReturn(PointsAggregation.builder()
                            .customerCount(2)
                            .pointsTotal(123)
                            .customers(
                                Stream.of(new Object[][]{
                                    {
                                        SAMPLE_ID_1,
                                        Customer.builder()
                                            .id(SAMPLE_ID_1)
                                            .points(123)
                                            .transactions(456)
                                            .build()
                                    },
                                    {
                                        SAMPLE_ID_2,
                                        Customer.builder()
                                            .id(SAMPLE_ID_2)
                                            .points(789)
                                            .transactions(321)
                                            .build()
                                    },
                                }).collect(Collectors.toMap(data -> (String) data[0], data -> (Customer) data[1]))
                            )
                            .build()
            );

        this.mockMvc.perform(post("/calculate")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(
                                     // language=JSON
                                     "{\n" +
                                         "  \"transactions\": [\n" +
                                         "    {\n" +
                                         "      \"id\": \"" + SAMPLE_ID_1 + "\",\n" +
                                         "      \"value\": " + SAMPLE_VALUE_1 + ",\n" +
                                         "      \"date\": " + SAMPLE_DATE_1_S + "\n" +
                                         "    },\n" +
                                         "    {\n" +
                                         "      \"id\": \"" + SAMPLE_ID_2 + "\",\n" +
                                         "      \"value\": " + SAMPLE_VALUE_2 + ",\n" +
                                         "      \"date\": " + SAMPLE_DATE_2_S + "\n" +
                                         "    }\n" +
                                         "  ]\n" +
                                         "}"
                                 )
                                 .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.customer_count", is(2)))
            .andExpect(jsonPath("$.points_total", is(123)))
            .andExpect(jsonPath("$.customers").isMap())
            .andExpect(jsonPath("$.customers.length()", is(2)))
            .andExpect(jsonPath("$.customers." + SAMPLE_ID_1).isMap())
            .andExpect(jsonPath("$.customers." + SAMPLE_ID_1 + ".id", is(SAMPLE_ID_1)))
            .andExpect(jsonPath("$.customers." + SAMPLE_ID_1 + ".points", is(123)))
            .andExpect(jsonPath("$.customers." + SAMPLE_ID_1 + ".transactions", is(456)))
            .andExpect(jsonPath("$.customers." + SAMPLE_ID_2).isMap())
            .andExpect(jsonPath("$.customers." + SAMPLE_ID_2 + ".id", is(SAMPLE_ID_2)))
            .andExpect(jsonPath("$.customers." + SAMPLE_ID_2 + ".points", is(789)))
            .andExpect(jsonPath("$.customers." + SAMPLE_ID_2 + ".transactions", is(321)));
    }
}
