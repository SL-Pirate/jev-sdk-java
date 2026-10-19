package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.JevType;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevResponse;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevScoreAnswer;
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
