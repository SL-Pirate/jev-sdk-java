package dev.isira.jev.dto.response;

import dev.isira.jev.dto.JevUsage;
import dev.isira.jev.dto.response.answer.JevAnswer;

import java.util.Map;

public record JevResponse(String model, Map<String, JevAnswer> answers, JevUsage usage) {
}
