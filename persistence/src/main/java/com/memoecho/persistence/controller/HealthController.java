package com.memoecho.persistence.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("service", "persistence");
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now().toString());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }
}
