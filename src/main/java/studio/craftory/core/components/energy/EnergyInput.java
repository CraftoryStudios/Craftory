package studio.craftory.core.components.energy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.NonNull;
import studio.craftory.core.utils.Reflections;

/**
 * Energy Input Component
 */
public interface EnergyInput extends EnergyStorage {

  /**
   * Inputs maximum possible amount of energy into objects energy storage
   *
   * @param availableEnergy Amount of energy that is available to be insert into objects energy storage
   * @return Amount of energy that was stored
   */
  default long receiveEnergy(@NonNull final long availableEnergy) {
    synchronized (this) {
      long energyReceived = Math.min(getFreeSpace(), Math.min(getMaxEnergyInput(), availableEnergy));
      increaseStoredEnergy(energyReceived);
      return energyReceived;
    }
  }

  /**
   * @return Max energy input the object is capable of receiving
   */
  default long getMaxEnergyInput() {
    return Reflections.getClassAnnotation(this, EnergyInputData.class).maxEnergyReceive();
  }

  //Annotations
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface EnergyInputData {
    int maxEnergyReceive();
  }
}
