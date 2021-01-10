package studio.craftory.core;

import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("studio.craftory.core.annotations.CraftoryAddon")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CraftoryAddonProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {


    for (TypeElement annotation : annotations) {
      Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
      //TODO Also filter on implementing CraftoryAddon and JavaPlugin
      annotatedElements = annotatedElements.stream().filter(element -> element.getKind() == ElementKind.CLASS).collect(
          Collectors.toSet());

      
    }
    return false;
  }
}
