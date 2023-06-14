package io.github.navid1981.jsonschemagenerator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasquatch.jsonschemainferrer.IntegerTypePreference;
import com.saasquatch.jsonschemainferrer.JsonSchemaInferrer;
import com.saasquatch.jsonschemainferrer.SpecVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Map;


@Service
public class JsonToSchemaService {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonSchemaInferrer inferrer = JsonSchemaInferrer.newBuilder()
            .setSpecVersion(SpecVersion.DRAFT_07)
            .setIntegerTypePreference(IntegerTypePreference.NEVER)
            .build();

    private static RequiredService requiredService=new RequiredService();

    public static String schema;

    public String convertor(String json, Map<String, String> map)  {
        try {
            synchronized (this) {
                final JsonNode jsonNode = inferrer.inferForSample(mapper.readTree(json));
                schema = mapper.writeValueAsString(jsonNode);
                System.out.println(schema);

                for (String key : map.keySet()) {
                    requiredService.getSchema(key, map.get(key));
                }
            }
            return schema;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
