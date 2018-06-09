package de.toomuchcoffee.api;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    public void postTransactionsSucceeds() throws Exception {
        when(statisticsService.add(any())).thenReturn(true);

        String json = new ObjectMapper().writeValueAsString(new Transaction(0d, 0));

        mvc.perform(post("/transactions")
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void postTransactionsFails() throws Exception {
        when(statisticsService.add(any())).thenReturn(false);

        String json = new ObjectMapper().writeValueAsString(new Transaction(0d, 0));

        mvc.perform(post("/transactions")
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getStatistics() throws Exception {
        Statistics statistics = new Statistics(123, 123.45, 0.12, 999.87, 456.12);

        when(statisticsService.get()).thenReturn(statistics);

        mvc.perform(get("/statistics")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("sum", is(123.45)))
                .andExpect(jsonPath("count", is(123)))
                .andExpect(jsonPath("avg", is(456.12)))
                .andExpect(jsonPath("max", is(999.87)))
                .andExpect(jsonPath("min", is(0.12)))
        ;
    }
}