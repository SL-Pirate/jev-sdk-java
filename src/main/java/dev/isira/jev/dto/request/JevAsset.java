package dev.isira.jev.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Base64;
import java.util.List;

public record JevAsset(
        @JsonIgnore String name,
        String type,
        String format,
        @JsonProperty("data_base64") String base64
) {
    public JevAsset(String name, String type, String format, List<byte[]> data) {
        this(name, type, format, data == null || data.isEmpty()
                ? null
                : Base64.getEncoder().encodeToString(data.getFirst()));
    }
}
