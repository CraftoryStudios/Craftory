package studio.craftory.core.utils;

import java.text.DecimalFormat;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
  private static final String UNIT_ENERGY = "Re";
  private static final String UNIT_FLUID = "B";
  private static final DecimalFormat df = new DecimalFormat("###.###");

  public static String rawEnergyToPrefixed(int energy) {
    String s = Integer.toString(energy);
    int length = s.length();

    if (length < 6) {
      return s + " " + UNIT_ENERGY;
    }
    if (length < 7) {
      return df.format(energy / 1000f) + " K" + UNIT_ENERGY;
    }
    if (length < 10) {
      return df.format(energy / 1000000f) + " M" + UNIT_ENERGY;
    }
    if (length < 13) {
      return df.format(energy / 1000000000f) + " G" + UNIT_ENERGY;
    }
    if (length < 16) {
      return df.format(energy / 1000000000000f) + " T" + UNIT_ENERGY;
    }
    if (length < 19) {
      return df.format(energy / 1000000000000000f) + " P" + UNIT_ENERGY;
    }
    if (length < 22) {
      return df.format(energy / 1000000000000000000f) + " E" + UNIT_ENERGY;
    }
    return "A bukkit load";
  }

  public static String rawFluidToPrefixed(int amount) {
    String s = Integer.toString(amount);
    int length = s.length();
    if (length < 6) {
      return s + " m" + UNIT_FLUID;
    }
    if (length < 7) {
      return s + " " + UNIT_FLUID;
    }
    if (length < 10) {
      return df.format(amount / 1000000f) + " K" + UNIT_FLUID;
    }
    if (length < 13) {
      return df.format(amount / 1000000000f) + " M" + UNIT_FLUID;
    }
    if (length < 16) {
      return df.format(amount / 1000000f) + " G" + UNIT_FLUID;
    }
    if (length < 19) {
      return df.format(amount / 1000000f) + " T" + UNIT_FLUID;
    }
    if (length < 22) {
      return df.format(amount / 1000000f) + " P" + UNIT_FLUID;
    }
    return "A bukkit load";
  }
}
