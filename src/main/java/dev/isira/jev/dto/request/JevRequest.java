package dev.isira.jev.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.isira.jev.dto.request.question.JevQuestion;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public class JevRequest {
    public final String state;
    public final String model;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final Map<String, JevAsset> assets;

    public final Map<String, JevQuestion<?>> questions;

    @Builder
    public JevRequest(String state, String model, Map<String, JevQuestion<?>> questions, List<JevAsset> assets) {
        this.state = state;
        this.model = model == null ? "jev-latest" : model;
        this.questions = questions;
        this.assets = convertAssets(assets);
        injectQuestionKeys(questions);
    }

    public JevRequest(String state, String model, Map<String, JevQuestion<?>> questions) {
        this.state = state;
        this.model = model == null ? "jev-latest" : model;
        this.questions = questions;
        assets = null;
        injectQuestionKeys(questions);
    }

    public JevRequest(String state, Map<String, JevQuestion<?>> questions) {
        this.state = state;
        this.model = "jev-latest";
        this.questions = questions;
        assets = null;
        injectQuestionKeys(questions);
    }

    private static void injectQuestionKeys(@NonNull Map<String, JevQuestion<?>> questions) {
        for (Map.Entry<String, JevQuestion<?>> entry : questions.entrySet()) {
            entry.getValue().setQuestionKey(entry.getKey());
        }
    }

    private static Map<String, JevAsset> convertAssets(List<JevAsset> assets) {
        if (assets == null || assets.isEmpty()) {
            return null;
        }
        return assets.stream().collect(java.util.stream.Collectors.toMap(JevAsset::name, asset -> asset));
    }

    public boolean hasAssets() {
        return assets != null && !assets.isEmpty();
    }
}
