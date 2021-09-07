package studio.craftory.core.blocks.components.fluid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import lombok.NonNull;
import studio.craftory.core.containers.keys.CraftoryDataKey;
import studio.craftory.core.containers.persitanceholders.PersistentDataHolder;
import studio.craftory.core.utils.Reflections;

public interface FluidStorage extends PersistentDataHolder {

  //Data Storage Keys
  CraftoryDataKey STORED_FLUID_TYPE = new CraftoryDataKey("CraftoryCore", "storedFluidType", CraftoryFluid.class);
  CraftoryDataKey STORED_FLUID_AMOUNT = new CraftoryDataKey("CraftoryCore", "storedFluidAmount", Long.class);

  /**
   * @return Maximum amount of fluid object can store
   */
  default long getMaxFluidStored() {
    return Reflections.getClassAnnotation(this, EnergyStorageData.class).capacity();
  }

  /**
   * @return Current type of fluid object has stored
   */
  default Optional<CraftoryFluid> getStoredFluidType() {
    return getPersistentData().get(STORED_FLUID_TYPE);
  }

  /**
   * @return Current amount of fluid object has stored
   */
  default long getStoredFluidAmount() {
    return (long) getPersistentData().get(STORED_FLUID_AMOUNT).orElse(0L);
  }

  /**
   * Sets amount of
   *
   * @param amount of field stored
   */
  default void setStoredFluidAmount(final long amount) {
    if (getStoredFluidType().isEmpty()) {
      throw new IllegalStateException("Tried to set the amount of fluid in an empty storage!");
    }
    setStoredFluidInternal(amount);
  }

  default long getFreeSpace() {
    return getMaxFluidStored() - getStoredFluidAmount();
  }

  default void increaseStoredFluidAmount(final long amount) {
    setStoredFluidAmount(getStoredFluidAmount() + amount);
  }

  default void decreaseStoredFluidAmount(final long amount) {
    setStoredFluidAmount(getStoredFluidAmount() - amount);
  }

  default void setStoredFluid(@NonNull final CraftoryFluid fluidType, final long amount) {
    if (!setStoredFluidInternal(amount)) {
      return;
    }
    getPersistentData().set(STORED_FLUID_TYPE, fluidType);
  }

  default boolean setStoredFluidInternal(long amount) {
    long newAmount;
    if (amount > getMaxFluidStored()) {
      newAmount = getMaxFluidStored();
    } else if (amount < 0) {
      newAmount = 0;
    } else {
      newAmount = amount;
    }

    if (newAmount == 0) {
      getPersistentData().remove(STORED_FLUID_TYPE);
      getPersistentData().remove(STORED_FLUID_AMOUNT);
      return false;
    }
    getPersistentData().set(STORED_FLUID_AMOUNT, newAmount);
    return true;
  }

  default void increaseStoredFluid(@NonNull final CraftoryFluid fluidType, final long amount) {
    setStoredFluid(fluidType, getStoredFluidAmount() + amount);
  }


  default void decreaseStoredFluid(@NonNull final CraftoryFluid fluidType, final long amount) {
    setStoredFluid(fluidType, getStoredFluidAmount() - amount);
  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface EnergyStorageData {

    int capacity();
  }
}
