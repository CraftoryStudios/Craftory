package studio.craftory.core.utils;

import java.text.DecimalFormat;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

  private static final String UNIT_ENERGY = "Re";
  private static final String UNIT_FLUID = "B";
  private static final DecimalFormat DF = new DecimalFormat("###.###");
  private static final String[] PREFIXES = {" m", " ", " K", " M", " G", " T", " P", " E"};

  public static String rawEnergyToPrefixed(long energy) {
    return rawToPrefixed(energy, UNIT_ENERGY, 1);
  }

  public static String rawFluidToPrefixed(long amount) {
    return rawToPrefixed(amount, UNIT_FLUID, 0);
  }

  private static String rawToPrefixed(long amount, String unit, int startingPrefix) {
    String s = Long.toString(amount);
    int length = s.length();

    if (length < 6) {
      return s + PREFIXES[startingPrefix] + unit;
    }
    if (length < 7) {
      return DF.format(amount / 1000f) + PREFIXES[startingPrefix+1] + unit;
    }
    if (length < 10) {
      return DF.format(amount / 1000000f) + PREFIXES[startingPrefix+2] + unit;
    }
    if (length < 13) {
      return DF.format(amount / 1000000000f) + PREFIXES[startingPrefix+3] + unit;
    }
    if (length < 16) {
      return DF.format(amount / 1000000000000f) + PREFIXES[startingPrefix+4] + unit;
    }
    if (length < 19) {
      return DF.format(amount / 1000000000000000f) + PREFIXES[startingPrefix+5] + unit;
    }
    if (amount < Long.MAX_VALUE) {
      return DF.format(amount / 1000000000000000000f) + PREFIXES[startingPrefix+6] + unit;
    }
    return "A bukkit load";
  }

}
