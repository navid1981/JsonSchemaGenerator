package io.github.navid1981.jsonschemagenerator.service;

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
        ObjectNode objectNode = (ObjectNode) new ObjectMapper().readTree(JsonToSchemaService.schema);
        ArrayNode arrayNode= mapper.createArrayNode();
        String[] values=value.split(",");
        for(int i=0;i<values.length;i++){
            arrayNode.add(values[i]);
        }

        setJsonPointerValue(objectNode, JsonPointer.compile(path), arrayNode);
        JsonToSchemaService.schema=objectNode.toPrettyString();
        return objectNode.toPrettyString();
    }

    public void setJsonPointerValue(ObjectNode node, JsonPointer pointer, JsonNode value) {
        JsonNode parentNode = node.at(pointer);
        String fieldName = "required";

        if (parentNode.isObject()) {
            ((ObjectNode) parentNode).set(fieldName, value);
        } else {
            throw new IllegalArgumentException("`" + fieldName + "` can't be set for parent node `"
                    + pointer + "` because parent is not a container but " + parentNode.getNodeType().name());
        }
    }
}
