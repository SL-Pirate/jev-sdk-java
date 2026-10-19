package dev.isira.jev.dto.request.question;

import dev.isira.jev.dto.JevType;
import dev.isira.jev.dto.response.JevResponse;
import dev.isira.jev.dto.response.answer.JevScoreAnswer;
import lombok.NonNull;

import java.util.List;

public class JevScoreQuestion extends JevQuestion<JevScoreAnswer> {
    public final List<Object> criteria;

    public JevScoreQuestion(String instructions, List<Object> criteria) {
        super(JevType.SCORE, instructions);
        this.criteria = criteria;
    }

    @Override
    public Class<JevScoreAnswer> getAnswerClass() {
        return JevScoreAnswer.class;
    }

    @Override
    public JevScoreAnswer getAnswerFromResponse(@NonNull JevResponse response) {
        return getAnswerClass().cast(response.answers().get(questionKey));
    }
}
