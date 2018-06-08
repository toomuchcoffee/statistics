package de.toomuchcoffee.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    @GetMapping("statistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> map = new HashMap<>();
        map.put("stat", 123.456);
        return map;
    }

}
