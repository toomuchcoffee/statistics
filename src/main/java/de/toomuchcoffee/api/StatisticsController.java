package de.toomuchcoffee.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(final StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping("transactions")
    @ResponseStatus(CREATED)
    public ResponseEntity<Void> postTransaction(Transaction transaction) {
        boolean success = statisticsService.add(transaction);
        return new ResponseEntity<>(success ? CREATED : NO_CONTENT);
    }

    @GetMapping("statistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> map = new HashMap<>();
        map.put("stat", 123.456);
        return map;
    }

}
