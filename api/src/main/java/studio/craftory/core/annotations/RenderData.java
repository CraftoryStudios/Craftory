package studio.craftory.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface RenderData {
  String northFacingModel() default "";
  String southFacingModel() default "";
  String eastFacingModel() default "";
  String westFacingModel() default "";
  String upFacingModel() default "";
  String downFacingModel() default "";
  String headModel() default "";
}
