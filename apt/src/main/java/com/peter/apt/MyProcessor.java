package com.peter.apt;

import com.google.auto.service.AutoService;
import com.peter.anno.BindView;
import com.peter.anno.model.RouteMeta;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    private Filer mFiler;
    private Elements mElementUtils;
    private Messager mMessager;
    private String moduleName;
    private Types types;
    // Options of processor
    public static final String KEY_MODULE_NAME = "MY_MODULE_NAME";
    public static final String ACTIVITY = "android.app.Activity";
    private Map<String, RouteMeta> map = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportType = new LinkedHashSet<>();
        supportType.add(BindView.class.getCanonicalName());
        return supportType;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        types = processingEnv.getTypeUtils();

        // Attempt to get user configuration [moduleName]
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {
            System.out.println("We arrive processorTest81, moduleName : " + moduleName);
            mMessager.printMessage(Diagnostic.Kind.NOTE,
                    "We get processorTest, module name : " + moduleName);

            TypeMirror type_Activity = mElementUtils.getTypeElement(ACTIVITY).asType();

            // Map<String, RouteMeta>
            ParameterizedTypeName inputMapType = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouteMeta.class)
            );

            ParameterSpec paraSpec = ParameterSpec.builder(inputMapType, "customMap")
                    .build();


            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
            ClassName routeMetaCn = ClassName.get(RouteMeta.class);

            for (Element element : elements) {
                TypeMirror tm = element.asType();
                BindView bindView = element.getAnnotation(BindView.class);

                if (types.isSubtype(tm, type_Activity)) {
                    RouteMeta meta = new RouteMeta(bindView, element);
//                    ClassName className = ClassName.get((TypeElement) meta.getElement());
                    map.put(bindView.value(), meta);
                }
            }

            // 拿到了数据 map, 接下来需要将数据写成文件
            MethodSpec.Builder builder = MethodSpec.methodBuilder("loadInto")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(paraSpec);
            for (Map.Entry<String, RouteMeta> entry : map.entrySet()) {
                RouteMeta routeMeta = entry.getValue();
                ClassName className = ClassName.get((TypeElement) routeMeta.getElement());

                builder.addStatement("customMap.put($S, $T.build($S, $T.class))", entry.getKey(), routeMetaCn, entry.getKey(),className);
            }

            try {
                JavaFile.builder("com.peter.helloworld",
                        TypeSpec.classBuilder("MyMap"+moduleName)
                        .addJavadoc("这是自己生成的文件")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(builder.build())
                        .build()
                ).build().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}