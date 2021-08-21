package studio.craftory.core.blocks.components.fluid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import lombok.NonNull;
import studio.craftory.core.utils.Reflections;

public interface FluidInput extends FluidStorage {

  default long receiveFluid(@NonNull CraftoryFluid fluid, final long availableFluid) {
    Optional<CraftoryFluid> fluidOptional = getStoredFluidType();
    if (fluidOptional.isPresent()) {
      if (!fluidOptional.get().equals(fluid))
        return 0;

      long amountReceived = Math.min(getFreeSpace(), Math.min(getMaxFluidReceive(), availableFluid));
      increaseStoredFluid(fluid, amountReceived);
      return amountReceived;
    }
    return 0;
  }

  default long getMaxFluidReceive() {
    return Reflections.getClassAnnotation(this, FluidInputData.class).maxReceive();
  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface FluidInputData {
    int maxReceive();
  }
}