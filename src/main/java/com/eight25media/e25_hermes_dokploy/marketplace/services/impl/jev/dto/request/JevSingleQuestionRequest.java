package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question.JevQuestion;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevAnswer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

public class JevSingleQuestionRequest<T extends JevAnswer> extends JevRequest {
    public final static String DEFAULT_QUESTION_KEY = "question";

    @Getter
    @JsonIgnore
    private final JevQuestion<T> question;

    @Builder(builderMethodName = "singleQuestionBuilder")
    public JevSingleQuestionRequest(String state, String model, String questionKey, JevQuestion<T> question) {
        super(state, model, Map.of(questionKey == null ? DEFAULT_QUESTION_KEY : questionKey, question));
        this.question = question;
    }
}
