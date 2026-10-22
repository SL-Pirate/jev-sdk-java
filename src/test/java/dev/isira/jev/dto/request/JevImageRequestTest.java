package dev.isira.jev.dto.request;

import dev.isira.jev.dto.request.question.JevNoulQuestion;
import dev.isira.jev.dto.response.answer.JevNoulAnswer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JevImageRequestTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesImageBytesUsingTheApiAssetSchema() {
        var asset = new JevAsset("photo-ref", "image", "png", List.of(new byte[]{0, 1, (byte) 255}));
        var request = JevRequest.builder().state("photo-ref")
                .assets(List.of(asset))
                .questions(Map.of("visible", new JevNoulQuestion("Is a mug visible?"))).build();

        assertTrue(request.hasAssets());
        assertSame(asset, request.assets.get("photo-ref"));
        assertEquals(mapper.readTree("""
                {"model":"jev-latest","state":"photo-ref",
                 "assets":{"photo-ref":{"type":"image","format":"png","data_base64":"AAH/"}},
                 "questions":{"visible":{"type":"noul","instructions":"Is a mug visible?"}}}
                """), mapper.valueToTree(request));
    }

    @Test
    void singleQuestionBuilderPreservesAlreadyEncodedImagesAndCustomKey() {
        var asset = new JevAsset("photo-ref", "image", "jpg", "AQI=");
        var request = JevSingleQuestionRequest.<JevNoulAnswer>singleQuestionBuilder()
                .state("photo-ref").model("jev-1.14.0").questionKey("visible")
                .question(new JevNoulQuestion("Is a mug visible?"))
                .assets(List.of(asset)).build();
        var json = mapper.valueToTree(request);

        assertTrue(request.hasAssets());
        assertEquals("AQI=", json.path("assets").path("photo-ref").path("data_base64").asText());
        assertTrue(json.path("questions").has("visible"));
        assertFalse(json.has("question"));
    }

    @Test
    void omitsAbsentAndEmptyAssetLists() {
        for (var assets : java.util.Arrays.<List<JevAsset>>asList(null, List.of())) {
            var request = JevRequest.builder().state("text").questions(Map.of())
                    .assets(assets).build();
            assertFalse(request.hasAssets());
            assertNull(request.assets);
            assertFalse(mapper.valueToTree(request).has("assets"));
        }
    }

    @Test
    void rejectsDuplicateReferences() {
        var asset = new JevAsset("same-ref", "image", "png", "AQI=");
        assertThrows(IllegalStateException.class, () -> JevRequest.builder()
                .state("same-ref").questions(Map.of()).assets(List.of(asset, asset)).build());
    }

    @Test
    void byteListConstructorHandlesMissingDataAndEncodesOnlyFirstImage() {
        assertNull(new JevAsset("ref", "image", "png", (List<byte[]>) null).base64());
        assertNull(new JevAsset("ref", "image", "png", List.<byte[]>of()).base64());
        assertEquals("AQ==", new JevAsset("ref", "image", "png",
                List.of(new byte[]{1}, new byte[]{2})).base64());
    }
}
