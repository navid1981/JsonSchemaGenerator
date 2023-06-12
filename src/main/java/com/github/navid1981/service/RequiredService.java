package com.github.navid1981.service;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

@Service
public class RequiredService {
    public static ObjectMapper mapper = new ObjectMapper();

    public String getSchema(String path, String value) throws JsonProcessingException {
        ObjectNode objectNode = (ObjectNode) new ObjectMapper().readTree(GeneratorService.schema);

        ObjectNode require =mapper.createObjectNode();
        ArrayNode arrayNode= mapper.createArrayNode();
        arrayNode.add(value);
        require.set("required",arrayNode);
        setJsonPointerValue(objectNode, JsonPointer.compile(path), arrayNode);

        return objectNode.toPrettyString();
    }

    public void setJsonPointerValue2(ObjectNode node, JsonPointer pointer, JsonNode value){
        node.set("required",value);
    }
    public void setJsonPointerValue(ObjectNode node, JsonPointer pointer, JsonNode value) {
        JsonPointer parentPointer = pointer.head();
        JsonNode parentNode = node.at(parentPointer);
        String fieldName = "required";

        if (parentNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) parentNode;
            int index = Integer.valueOf(fieldName);
            // expand array in case index is greater than array size (like JavaScript does)
            for (int i = arrayNode.size(); i <= index; i++) {
                arrayNode.addNull();
            }
            arrayNode.set(index, value);
        } else if (parentNode.isObject()) {
            ((ObjectNode) parentNode).set(fieldName, value);
        } else {
            throw new IllegalArgumentException("`" + fieldName + "` can't be set for parent node `"
                    + parentPointer + "` because parent is not a container but " + parentNode.getNodeType().name());
        }
    }
}
