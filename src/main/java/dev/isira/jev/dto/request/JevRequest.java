package dev.isira.jev.dto.request;

import dev.isira.jev.dto.request.question.JevQuestion;
import lombok.Builder;
import lombok.NonNull;

import java.util.Map;

public class JevRequest {
    public final String state;
    public final String model;
    public final Map<String, JevQuestion<?>> questions;

    @Builder
    public JevRequest(String state, String model, Map<String, JevQuestion<?>> questions) {
        this.state = state;
        this.model = model == null ? "jev-latest" : model;
        this.questions = questions;
        injectQuestionKeys(questions);
    }

    public JevRequest(String state, Map<String, JevQuestion<?>> questions) {
        this.state = state;
        this.model = "jev-latest";
        this.questions = questions;
        injectQuestionKeys(questions);
    }

    private static void injectQuestionKeys(@NonNull Map<String, JevQuestion<?>> questions) {
        for (Map.Entry<String, JevQuestion<?>> entry : questions.entrySet()) {
            entry.getValue().setQuestionKey(entry.getKey());
        }
    }
}
