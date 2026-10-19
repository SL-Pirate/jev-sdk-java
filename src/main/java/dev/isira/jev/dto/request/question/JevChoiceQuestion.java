package dev.isira.jev.dto.request.question;

import dev.isira.jev.dto.JevType;
import dev.isira.jev.dto.response.JevResponse;
import dev.isira.jev.dto.response.answer.JevChoiceAnswer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.util.Map;

public class JevChoiceQuestion extends JevQuestion<JevChoiceAnswer> {
    // The API expects the choice map under "criteria", like the other question types.
    @JsonProperty("criteria")
    public final Map<String, Object> choices;

    public JevChoiceQuestion(String instructions, Map<String, Object> choices) {
        super(JevType.CHOICE, instructions);
        this.choices = choices;
    }

    @Override
    public Class<JevChoiceAnswer> getAnswerClass() {
        return JevChoiceAnswer.class;
    }

    @Override
    public JevChoiceAnswer getAnswerFromResponse(@NonNull JevResponse response) {
        return getAnswerClass().cast(response.answers().get(questionKey));
    }
}
