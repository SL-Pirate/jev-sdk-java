package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.JevType;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevResponse;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevChoiceAnswer;
import lombok.NonNull;

import java.util.Map;

public class JevChoiceQuestion extends JevQuestion<JevChoiceAnswer> {
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
