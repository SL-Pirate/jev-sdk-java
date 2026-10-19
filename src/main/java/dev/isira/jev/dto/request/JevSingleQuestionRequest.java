package dev.isira.jev.dto.request;

import dev.isira.jev.dto.request.question.JevQuestion;
import dev.isira.jev.dto.response.answer.JevAnswer;
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
