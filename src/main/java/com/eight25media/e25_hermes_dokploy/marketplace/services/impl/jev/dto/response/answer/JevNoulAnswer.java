package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.JevType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JevNoulAnswer extends JevAnswer {
    public final double noul;

    // Jackson 3 reads a lone unannotated constructor arg as a delegating
    // creator (a bare number), not as the "noul" property.
    @JsonCreator
    public JevNoulAnswer(@JsonProperty("noul") double noul) {
        super(JevType.NOUL);
        this.noul = noul;
    }
}
