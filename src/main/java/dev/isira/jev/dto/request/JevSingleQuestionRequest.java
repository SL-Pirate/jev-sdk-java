package dev.isira.jev.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.isira.jev.dto.request.question.JevQuestion;
import dev.isira.jev.dto.response.answer.JevAnswer;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class JevSingleQuestionRequest<T extends JevAnswer> extends JevRequest {
    public final static String DEFAULT_QUESTION_KEY = "question";

    @Getter
    @JsonIgnore
    private final JevQuestion<T> question;

    @Builder(builderMethodName = "singleQuestionBuilder")
    public JevSingleQuestionRequest(
            String state,
            String model,
            String questionKey,
            JevQuestion<T> question,
            List<JevAsset> assets
    ) {
        super(state, model, Map.of(questionKey == null ? DEFAULT_QUESTION_KEY : questionKey, question), assets);
        this.question = question;
    }

    public JevSingleQuestionRequest(String state, String model, String questionKey, JevQuestion<T> question) {
        super(state, model, Map.of(questionKey == null ? DEFAULT_QUESTION_KEY : questionKey, question));
        this.question = question;
    }
}
