package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.JevType;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.JevRequest;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevChoiceAnswer;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevNoulAnswer;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevScoreAnswer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.STATE;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.animalQuestion;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.energyQuestion;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.foxFastQuestion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JevQuestionTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private static void assertSerializesTo(String expectedJson, JevQuestion<?> question) {
        var mapper = new ObjectMapper();
        assertEquals(mapper.readTree(expectedJson), mapper.readTree(mapper.writeValueAsString(question)));
    }

    @Test
    void choiceQuestionSerializesChoicesUnderCriteriaAsTheApiExpects() {
        assertSerializesTo("""
                {
                  "type": "choice",
                  "instructions": "Which animal is described as fast?",
                  "criteria": {"fox": "the fox", "dog": "the dog"}
                }
                """, animalQuestion());
    }

    @Test
    void scoreQuestionSerializesCriteriaList() {
        assertSerializesTo("""
                {
                  "type": "score",
                  "instructions": "How energetic is the dog?",
                  "criteria": ["1: asleep", "2: lazy", "3: neutral", "4: active", "5: hyper"]
                }
                """, energyQuestion());
    }

    @Test
    void noulQuestionOmitsCriteriaWhenNotGiven() {
        assertSerializesTo("""
                {"type": "noul", "instructions": "Is the fox fast?"}
                """, foxFastQuestion());
    }

    @Test
    void noulQuestionSerializesCriteriaWhenGiven() {
        assertSerializesTo("""
                {"type": "noul", "instructions": "Is the fox fast?", "criteria": {"fast": "moves quickly"}}
                """, new JevNoulQuestion("Is the fox fast?", Map.of("fast", "moves quickly")));
    }

    @Test
    void questionKeyIsNeverSerialized() {
        var question = animalQuestion();
        question.setQuestionKey("animal");

        var json = mapper.readTree(mapper.writeValueAsString(question));

        assertFalse(json.has("questionKey"));
    }

    @Test
    void typesAndAnswerClassesMatchPerSubclass() {
        assertEquals(JevType.CHOICE, animalQuestion().type);
        assertEquals(JevChoiceAnswer.class, animalQuestion().getAnswerClass());
        assertEquals(JevType.SCORE, energyQuestion().type);
        assertEquals(JevScoreAnswer.class, energyQuestion().getAnswerClass());
        assertEquals(JevType.NOUL, foxFastQuestion().type);
        assertEquals(JevNoulAnswer.class, foxFastQuestion().getAnswerClass());
    }

    @Test
    void buildingARequestInjectsEachQuestionsMapKey() {
        var animal = animalQuestion();
        var energy = energyQuestion();
        var fox = foxFastQuestion();

        new JevRequest(STATE, Map.of("animal", animal, "energy", energy, "fox_fast", fox));

        assertEquals("animal", animal.questionKey);
        assertEquals("energy", energy.questionKey);
        assertEquals("fox_fast", fox.questionKey);
    }

    @Test
    void eachQuestionExtractsItsOwnTypedAnswer() {
        var animal = animalQuestion();
        var energy = energyQuestion();
        var fox = foxFastQuestion();
        JevRequest.builder().state(STATE)
                .questions(Map.of("animal", animal, "energy", energy, "fox_fast", fox))
                .build();
        var response = JevFixtures.response();

        assertEquals("fox", animal.getAnswerFromResponse(response).choice);
        assertEquals(0.95, energy.getAnswerFromResponse(response).score);
        assertEquals(0.99, fox.getAnswerFromResponse(response).noul);
    }

    @Test
    void answerExtractionReturnsNullWhenTheResponseHasNoAnswerForTheKey() {
        var question = foxFastQuestion();
        question.setQuestionKey("not-in-response");

        assertNull(question.getAnswerFromResponse(JevFixtures.response()));
    }

    @Test
    void answerExtractionFailsWhenTheAnswerIsOfAnotherType() {
        var question = foxFastQuestion();
        question.setQuestionKey("animal"); // the response holds a choice answer there

        assertThrows(ClassCastException.class, () -> question.getAnswerFromResponse(JevFixtures.response()));
    }
}
