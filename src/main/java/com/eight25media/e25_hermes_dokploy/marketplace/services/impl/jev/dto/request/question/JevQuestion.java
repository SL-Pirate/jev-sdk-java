package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.JevType;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevResponse;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevAnswer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class JevQuestion<A extends JevAnswer> {
    public final JevType type;
    public final String instructions;

    @Setter
    @JsonIgnore
    // implicitly set by JevRequestBuilder when building the request
    protected String questionKey;

    @JsonIgnore
    public abstract Class<A> getAnswerClass();

    /// @param response the JevResponse to extract the answer from
    /// @return the answer of type A
    ///
    /// Helper method to extract the answer for the specific question from the JevResponse.
    @JsonIgnore
    public abstract A getAnswerFromResponse(JevResponse response);
}
