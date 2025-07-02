package com.quitsmoking.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitsmoking.platform.entity.InitialCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitialConditionSnapshotter {

    private final ObjectMapper objectMapper;

    public String snapshot(InitialCondition initialCondition) {
        try {
            return objectMapper.writeValueAsString(initialCondition);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to snapshot Initial Condition", e);
        }
    }
}