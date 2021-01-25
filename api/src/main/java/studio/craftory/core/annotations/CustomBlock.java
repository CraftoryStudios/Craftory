package studio.craftory.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import studio.craftory.core.blocks.renders.Renderers;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface CustomBlock {
  Renderers[] renders() default {Renderers.BLOCK_STATE_RENDER};
}
