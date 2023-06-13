package io.github.navid1981.jsonschemagenerator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasquatch.jsonschemainferrer.IntegerTypePreference;
import com.saasquatch.jsonschemainferrer.JsonSchemaInferrer;
import com.saasquatch.jsonschemainferrer.SpecVersion;
import org.springframework.stereotype.Service;


@Service
public class JsonToSchemaService {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonSchemaInferrer inferrer = JsonSchemaInferrer.newBuilder()
            .setSpecVersion(SpecVersion.DRAFT_07)
            .setIntegerTypePreference(IntegerTypePreference.NEVER)
            .build();

    public String convertor(String json)  {
        try {
            final JsonNode jsonNode = inferrer.inferForSample(mapper.readTree(json));
            String schema=mapper.writeValueAsString(jsonNode);
            System.out.println(schema);
            GeneratorService.schema=schema;
            return schema;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
