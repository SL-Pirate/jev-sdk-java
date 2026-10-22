package dev.isira.jev.dto.request.question;

import dev.isira.jev.dto.JevType;
import dev.isira.jev.dto.response.JevResponse;
import dev.isira.jev.dto.response.answer.JevBoxAnswer;
import lombok.NonNull;

public class JevBoxQuestion extends JevQuestion<JevBoxAnswer> {
    public JevBoxQuestion(String instructions) {
        super(JevType.BOX, instructions);
    }

    @Override
    public Class<JevBoxAnswer> getAnswerClass() {
        return JevBoxAnswer.class;
    }

    @Override
    public JevBoxAnswer getAnswerFromResponse(@NonNull JevResponse response) {
        return getAnswerClass().cast(response.answers().get(questionKey));
    }
}
