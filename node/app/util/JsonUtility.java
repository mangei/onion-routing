package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author Mihai Lepadat
 *         Date: 11/2/14
 */
public class JsonUtility {

    public static JsonNode convertToJson(String payload) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(payload);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResource(JsonNode json, String resourceName) {
        JsonNode node = json;

        String[] pathElements = resourceName.split("/");
        for (String element : pathElements) {
            node = node.findPath(element);
        }
        return node.asText();
    }

}
