package studio.craftory.core.blocks.components.fluid;

import com.google.common.collect.Maps;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.NonNull;
import studio.craftory.core.utils.Reflections;

public interface FluidOutput extends FluidStorage {

  /**
   * Extracts fluid from the objects storage up to the maximum amount requested.
   *
   * @param limit Maximum amount of fluid to extract
   * @return Entry of Fluid Type extracted and amount extracted
   */
  default Optional<Entry<CraftoryFluid, Long>> extractFluid(final long limit) {
    Optional<CraftoryFluid> fluidOptional = getStoredFluidType();
    if (fluidOptional.isPresent()) {

      long amountExtracted = Math.min(getStoredFluidAmount(), Math.min(getMaxFluidExtract(), limit));
      decreaseStoredFluidAmount(amountExtracted);
      return Optional.of(Maps.immutableEntry(fluidOptional.get(), amountExtracted));
    }
    return Optional.empty();
  }

  /**
   * Extracts fluid from the objects storage up to the maximum amount requested,
   * if fluid storage contains the fluid type provided
   *
   * @param limit Maximum amount of fluid to extract
   * @return Amount of fluid extracted
   */
  default long extractFluid(@NonNull final CraftoryFluid fluidType, final long limit) {
    Optional<CraftoryFluid> fluidOptional = getStoredFluidType();
    if (!fluidOptional.isPresent() || !fluidOptional.get().equals(fluidType)) {
      return 0L;
    }

    long amountExtracted = Math.min(getStoredFluidAmount(), Math.min(getMaxFluidExtract(), limit));
    decreaseStoredFluidAmount(amountExtracted);
    return amountExtracted;
  }

  /**
   * @return Maximum amount of fluid that can be extracted each tick
   */
  default long getMaxFluidExtract() {
    return Reflections.getClassAnnotation(this, FluidOutputData.class).maxExtract();
  }

  //Annotations
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface FluidOutputData {
    int maxExtract();
  }
}
