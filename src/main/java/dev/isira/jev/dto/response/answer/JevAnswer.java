package dev.isira.jev.dto.response.answer;

import dev.isira.jev.dto.JevType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = JevChoiceAnswer.class, name = "choice"),
        @JsonSubTypes.Type(value = JevNoulAnswer.class, name = "noul"),
        @JsonSubTypes.Type(value = JevScoreAnswer.class, name = "score")
})
public abstract class JevAnswer {
    public final JevType type;
}
