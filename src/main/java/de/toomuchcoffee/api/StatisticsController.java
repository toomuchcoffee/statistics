package de.toomuchcoffee.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    @PostMapping("transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public void postTransaction(Transaction transaction) {

    }

    @GetMapping("statistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> map = new HashMap<>();
        map.put("stat", 123.456);
        return map;
    }

}
