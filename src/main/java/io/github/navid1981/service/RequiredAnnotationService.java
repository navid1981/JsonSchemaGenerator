package io.github.navid1981.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.MemberValue;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class RequiredAnnotationService {

    @Value("${java.model.path}")
    private String path;

    @Value("${java.package}")
    private String packageName;

    @Autowired
    private File file;

    public void addRequiredAnnotation(String className, String fieldName) {
        try {
            addAnnotationToField(className,fieldName, JsonProperty.class,0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void addAnnotationToField(String className, String fieldName, Class<?> annotationClass, int retry) throws ClassNotFoundException {
        Class<?> clazz = SchemaService.urlClassLoader.loadClass(packageName + "." + className);
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass=null;
        try {
//            ctClass = pool.getCtClass(clazz.getName());
            ctClass = pool.makeClass(new FileInputStream(path + "/" + packageName.replace(".", "/") + "/" + className + ".class"));
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            CtField ctField = null;
            try {
                ctField = ctClass.getDeclaredField(fieldName);
            } catch (NotFoundException e) {
                if(retry<=3){
                    if(retry==0){
                        addAnnotationToField(className+"__1",fieldName, JsonProperty.class,retry+1);
                    }else{
                        className=className.substring(0,className.length()-2)+String.valueOf(retry+1);
                        addAnnotationToField(className,fieldName, JsonProperty.class,retry+1);
                    }
                }else{
                    throw e;
                }
            }

            ConstPool constPool = ctClass.getClassFile().getConstPool();

//            Annotation annotation = new Annotation(annotationClass.getName(), constPool);

            AnnotationsAttribute attr = getAnnotationsAttributeFromField(ctField);
            if (attr == null) {
                attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                ctField.getFieldInfo().addAttribute(attr);
                return;
            }
            Annotation annotation = attr.getAnnotation(JsonProperty.class.getName());
            MemberValue booleanValue = new BooleanMemberValue(true, constPool);
            annotation.addMemberValue("required", booleanValue);


            attr.addAnnotation(annotation);

            retransformClass(clazz, ctClass.toBytecode());

        } catch (NotFoundException | IOException | CannotCompileException e) {
            e.printStackTrace();
        } finally {
            ctClass.defrost();
            ctClass.detach();
            ctClass=null;
            System.gc();
        }

    }

    private AnnotationsAttribute getAnnotationsAttributeFromField(CtField ctField) {
        List<AttributeInfo> attrs = ctField.getFieldInfo().getAttributes();
        AnnotationsAttribute attr = null;
        if (attrs != null) {
            Optional<AttributeInfo> optional = attrs.stream()
                    .filter(AnnotationsAttribute.class::isInstance)
                    .findFirst();
            if (optional.isPresent()) {
                attr = (AnnotationsAttribute) optional.get();
            }
        }
        return attr;
    }

    private void retransformClass(Class<?> clazz, byte[] byteCode) {
        ClassFileTransformer cft = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                return byteCode;
            }
        };

        Instrumentation instrumentation = ByteBuddyAgent.install();
        try {
            instrumentation.addTransformer(cft, true);
            instrumentation.retransformClasses(clazz);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        } finally {
            instrumentation.removeTransformer(cft);
        }
    }
}
