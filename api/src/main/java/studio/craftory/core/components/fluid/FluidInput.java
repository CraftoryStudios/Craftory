package studio.craftory.core.components.fluid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.NonNull;
import studio.craftory.core.utils.Reflections;

public interface FluidInput extends FluidStorage {

  default long receiveFluid(@NonNull CraftoryFluid fluid, final long availableFluid) {
    if (getStoredFluidType().isPresent() && !getStoredFluidType().get().equals(fluid)) {
      return 0;
    }

    long amountReceived = Math.min(getFreeSpace(), Math.min(getMaxFluidReceive(), availableFluid));
    increaseStoredFluid(fluid, amountReceived);
    return amountReceived;
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
