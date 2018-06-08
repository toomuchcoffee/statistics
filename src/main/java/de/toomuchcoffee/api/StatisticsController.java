package de.toomuchcoffee.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(final StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping(value = "transactions")
    public ResponseEntity<Void> postTransaction(@RequestBody Transaction transaction) {
        boolean success = statisticsService.add(transaction);
        return new ResponseEntity<>(success ? CREATED : NO_CONTENT);
    }

    @GetMapping("statistics")
    public Statistics getStatistics() {
        Statistics statistics = new Statistics();
        statistics.setSum(123.45);
        return statistics;
    }

}
