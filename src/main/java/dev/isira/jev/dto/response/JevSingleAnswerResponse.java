package dev.isira.jev.dto.response;

import dev.isira.jev.dto.JevUsage;
import dev.isira.jev.dto.response.answer.JevAnswer;

public record JevSingleAnswerResponse<T extends JevAnswer>(String model, T answer, JevUsage usage) {
}
