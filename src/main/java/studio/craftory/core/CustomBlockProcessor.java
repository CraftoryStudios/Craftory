package studio.craftory.core;

import com.google.auto.service.AutoService;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import studio.craftory.core.annotations.CustomBlock;

@SupportedAnnotationTypes("studio.craftory.core.annotations.CustomBlock")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CustomBlockProcessor extends AbstractProcessor {

  private Types typeUtils;
  private Elements elementUtils;
  private Filer filer;
  private Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    typeUtils = processingEnv.getTypeUtils();
    elementUtils = processingEnv.getElementUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    messager.printMessage(Kind.NOTE, "asd");
    for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(CustomBlock.class)) {
      if (annotatedElement.getKind() == ElementKind.CLASS) {
        TypeElement typeElement = (TypeElement) annotatedElement;
        return isValidClass(typeElement);


      }
    }
    return false;
  }

  private boolean isValidClass(TypeElement blockElement) {

    //Check class is public
    if (!blockElement.getModifiers().contains(Modifier.PUBLIC)) {
      return false;
    }

    //Check class is not abstract
    if (blockElement.getModifiers().contains(Modifier.ABSTRACT)) {
      return false;
    }

    for (Element enclosed : blockElement.getEnclosedElements()) {
      if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
        ExecutableElement consturctorElement = (ExecutableElement) enclosed;
        if (consturctorElement.getParameters().size() == 2 && consturctorElement.getModifiers().contains(Modifier.PUBLIC)) {
          messager.printMessage(Kind.NOTE, "Testasda");
          messager.printMessage(Kind.NOTE, consturctorElement.getParameters().get(0).asType().toString());
          return true;
        }
      }
    }

    return false;
  }
}
