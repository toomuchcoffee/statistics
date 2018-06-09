package de.toomuchcoffee.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PostMapping(value = "transactions")
    public ResponseEntity<Void> postTransaction(@RequestBody Transaction transaction) {
        boolean success = statisticsService.add(transaction);
        return new ResponseEntity<>(success ? CREATED : NO_CONTENT);
    }

    @GetMapping("statistics")
    public Statistics getStatistics() {
        return statisticsService.get();
    }

}
