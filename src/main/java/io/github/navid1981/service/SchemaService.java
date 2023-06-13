package io.github.navid1981.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@Service
public class SchemaService {


    @Value("${java.model.path}")
    private String path;

    @Value("${java.package}")
    private String packageName;

    public static URLClassLoader urlClassLoader;

    public String generateSchema(boolean newGeneration) throws ClassNotFoundException, MalformedURLException {
        if(newGeneration) urlClassLoader=new URLClassLoader(new URL[]{new File(path).toURI().toURL()});

        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_7, OptionPreset.PLAIN_JSON);
        JacksonModule module = new JacksonModule(JacksonOption.RESPECT_JSONPROPERTY_REQUIRED);
        SchemaGeneratorConfig config = configBuilder.with(module).build();

        SchemaGenerator generator = new SchemaGenerator(config);
        Class<?> loadedClass = urlClassLoader.loadClass(packageName + ".PublisherPayload");
        JsonNode jsonSchema = generator.generateSchema(loadedClass);
        return jsonSchema.toPrettyString();
    }
}
