package studio.craftory.core.components.energy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.NonNull;
import lombok.Synchronized;
import studio.craftory.core.utils.Reflections;

/**
 * Energy Output Component
 */
public interface EnergyOutput extends EnergyStorage {

  /**
   * Extracts energy from objects storage up to the maximum amount requested.
   *
   * @param energyRequested Maximum amount of energy to extract
   * @return Amount of energy extracted
   */
  default long extractEnergy(@NonNull final long energyRequested) {
    synchronized (this) {
      long energyOutputted = Math.min(getEnergyStored(), Math.min(getMaxEnergyOutput(), energyRequested));
      decreaseStoredEnergy(energyOutputted);
      return energyOutputted;
    }
  }

  /**
   * @return Maximum amount of energy object is capable of outputting each tick
   */
  default long getMaxEnergyOutput() {
    return Reflections.getClassAnnotation(this, EnergyOutputData.class).maxExtract();
  }

  //Annotations
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface EnergyOutputData {
    int maxExtract();
  }
}
