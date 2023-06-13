package io.github.navid1981.jsonschemagenerator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.navid1981.jsonschemagenerator.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class AppController {

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private RequiredService requiredService;

    @Autowired
    private RequiredAnnotationService requiredAnnotationService;

    @Autowired
    private JsonToSchemaService jsonToSchemaService;

    @PostMapping(value = "/schema",consumes = {"application/json"})
    public ResponseEntity<String> getSchema(@RequestBody String payload){
        String schema= generatorService.convertJsonToSchema(payload);
        return new ResponseEntity<>(schema, HttpStatus.OK);
    }

    @PostMapping(value = "/schema/req",consumes = {"application/json"})
    public ResponseEntity<String> getSchemaWithRequiredFields(@RequestBody String payload) throws JsonProcessingException, ClassNotFoundException, MalformedURLException {
        Map<String,Object> map = new ObjectMapper().readValue(payload, HashMap.class);
        Set<String> set=map.keySet();
        for (String key:set) {
            requiredAnnotationService.addRequiredAnnotation(key,map.get(key).toString());
        }
        String result= schemaService.generateSchema(false);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/schema2",consumes = {"application/json"})
    public ResponseEntity<String> getSchema2(@RequestBody String payload){
        String schema= jsonToSchemaService.convertor(payload);
        return new ResponseEntity<>(schema, HttpStatus.OK);
    }

    @PostMapping(value = "/schema/req2")
    public ResponseEntity<String> getSchemaWithRequiredFields2(@RequestBody String payload) throws JsonProcessingException, ClassNotFoundException, MalformedURLException {

        String[] payloads=payload.split("=");
        String result=requiredService.getSchema(payloads[0],payloads[1]);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
