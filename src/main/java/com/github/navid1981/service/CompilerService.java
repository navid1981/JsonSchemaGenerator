package com.github.navid1981.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CompilerService {
    @Value("${java.model.path}")
    private String path;

    @Value("${java.package}")
    private String packageName;

    @Autowired
    private URLClassLoader urlClassLoader;

    public void compile() throws IOException {
        StandardJavaFileManager fileManager=null;
        try {
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            // This sets up the class path that the compiler will use.
            // I've added the .jar file that contains the DoStuff interface within in it...
            List<String> optionList = new ArrayList<>();
//            optionList.add("-classpath");
//            optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "./Compile.jar");
            File file=new File(path+ "/" + packageName.replace(".","/"));
            List<File> files= Stream.of(file.listFiles()).collect(Collectors.toList());
            Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(files);
            JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                fileManager,
                diagnostics,
                optionList,
                null,
                compilationUnit);
        /********************************************************************************************* Compilation Requirements **/
        if (task.call()) {
            /** Load and execute *************************************************************************************************/
            System.out.println("Compile Successfully");
//            Class<?> loadedClass = urlClassLoader.loadClass(packageName+".PublisherPayload");
//            // Create a new instance...
//            Object obj = loadedClass.newInstance();

        } else {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.out.format("Error on line %d in %s%n",
                        diagnostic.getLineNumber(),
                        diagnostic.getSource().toUri());
            }
        }
    } catch (RuntimeException exp) {
            System.out.println(exp.getMessage());
    }finally {
            fileManager.close();
        }

    }
}
