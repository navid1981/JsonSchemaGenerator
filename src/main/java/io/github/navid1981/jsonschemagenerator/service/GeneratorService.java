package io.github.navid1981.jsonschemagenerator.service;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class GeneratorService {
    @Autowired
    private JavaCreatorService javaCreatorService;

    @Autowired
    private CompilerService compilerService;

    @Value("${java.model.path}")
    private String path;

    @Value("${java.package}")
    private String packageName;

    @Autowired
    private File file;

    @Autowired
    private SchemaService schemaService;

    public static String schema;

    public String convertJsonToSchema(String json){
        schema=null;
        try {
            FileUtils.cleanDirectory(file);
            javaCreatorService.convertJsonToJavaClass(json, file, packageName, "PublisherPayload");
            compilerService.compile();
            schema = schemaService.generateSchema(true);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return schema;
    }
}
