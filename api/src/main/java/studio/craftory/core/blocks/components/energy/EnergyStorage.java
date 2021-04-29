package studio.craftory.core.blocks.components.energy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import studio.craftory.core.data.keys.CraftoryDataKey;
import studio.craftory.core.data.persitanceholders.PersistentDataHolder;
import studio.craftory.core.data.persitanceholders.VolatileDataHolder;
import studio.craftory.core.utils.Reflections;

/**
 * Energy Storage Component
 */
public interface EnergyStorage extends PersistentDataHolder, VolatileDataHolder {

  //Data Storage Keys
  CraftoryDataKey STORED_ENERGY_KEY = new CraftoryDataKey("CraftoryCore", "storedEnergy", Long.class);

  //Annotations
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface EnergyStorageData {
    int capacity();
  }

  //Getters & Setters

  /**
   * @return Max amount of energy object can store
   */
  default long getMaxEnergyStored() {
    return Reflections.getClassAnnotation(this, EnergyStorageData.class).capacity();
  }

  /**
   * @return Current amount of energy stored in object or zero
   */
  default long getEnergyStored() {
    synchronized (this) {
      return (long) getPersistentData().get(STORED_ENERGY_KEY).orElse(0L);
    }
  }

  /**
   * @return Amount of free space remain to store energy
   */
  default long getFreeSpace() {
    return getMaxEnergyStored() - getEnergyStored();
  }

  default void setStoredEnergy(final long energy) {
    synchronized (this) {
      final long newEnergy;

      if (energy > getMaxEnergyStored())
        newEnergy = getMaxEnergyStored();
      else if (energy < 0)
        newEnergy = 0;
      else
        newEnergy = energy;

      if (newEnergy == 0) {
        getPersistentData().remove(STORED_ENERGY_KEY);
        return;
      }

      getPersistentData().set(STORED_ENERGY_KEY, newEnergy);
    }
  }

  default void increaseStoredEnergy(final long energy) {
    setStoredEnergy(getEnergyStored() + energy);
  }

  default void decreaseStoredEnergy(final long energy) {
    setStoredEnergy(getEnergyStored() - energy);
  }

}
