package dev.isira.jev.dto.response;

import dev.isira.jev.dto.JevType;
import dev.isira.jev.dto.request.JevSingleQuestionRequest;
import dev.isira.jev.dto.request.question.JevBoxQuestion;
import dev.isira.jev.dto.response.answer.JevBoxAnswer;
import dev.isira.jev.dto.response.answer.JevNoulAnswer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

class JevBoxTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesBoxQuestionWithoutCriteriaOrHelperProperties() {
        var question = new JevBoxQuestion("The mug in the photo");
        assertEquals(JevBoxAnswer.class, question.getAnswerClass());
        assertEquals(mapper.readTree("""
                {"type":"box","instructions":"The mug in the photo"}
                """), mapper.valueToTree(question));
    }

    @Test
    void readsBoxEdgesAndConfidenceFromMixedResponseAndFindsTypedAnswer() {
        var question = new JevBoxQuestion("The mug in the photo");
        JevSingleQuestionRequest.<JevBoxAnswer>singleQuestionBuilder()
                .state("photo-ref").questionKey("mug").question(question).build();
        var response = mapper.readValue("""
                {"model":"jev-1.14.0","answers":{
                  "mug":{"type":"box","box":{"top":0.25,"bottom":0.82,"left":0.44,"right":0.84},"confidence":0.92},
                  "visible":{"type":"noul","noul":0.99}},
                 "usage":{"input_tokens":290,"output_tokens":21}}
                """, JevResponse.class);
        var answer = assertInstanceOf(JevBoxAnswer.class, response.answers().get("mug"));

        assertEquals(JevType.BOX, answer.type);
        assertEquals(new JevBoxAnswer.Box(0.25, 0.82, 0.44, 0.84), answer.box);
        assertEquals(0.92, answer.confidence);
        assertSame(answer, question.getAnswerFromResponse(response));
        assertInstanceOf(JevNoulAnswer.class, response.answers().get("visible"));
    }
}
