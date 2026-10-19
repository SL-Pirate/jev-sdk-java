package dev.isira.jev.dto.request.question;

import dev.isira.jev.dto.JevType;
import dev.isira.jev.dto.response.JevResponse;
import dev.isira.jev.dto.response.answer.JevNoulAnswer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NonNull;

import java.util.Map;

public class JevNoulQuestion extends JevQuestion<JevNoulAnswer> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final Map<String, String> criteria;

    public JevNoulQuestion(String instructions, Map<String, String> criteria) {
        super(JevType.NOUL, instructions);
        this.criteria = criteria;
    }

    public JevNoulQuestion(String instructions) {
        super(JevType.NOUL, instructions);
        this.criteria = null;
    }

    @Override
    public Class<JevNoulAnswer> getAnswerClass() {
        return JevNoulAnswer.class;
    }

    @Override
    public JevNoulAnswer getAnswerFromResponse(@NonNull JevResponse response) {
        return getAnswerClass().cast(response.answers().get(questionKey));
    }
}
