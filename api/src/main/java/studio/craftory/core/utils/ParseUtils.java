package studio.craftory.core.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ParseUtils {

  public static int parseOrDefault(String string, int def) {
    try {
      return Integer.parseInt(string);
    } catch (Exception ignored) {
      return def;
    }
  }

  public static long parseOrDefault(String string, long def) {
    try {
      return Long.parseLong(string);
    } catch (Exception ignored) {
      return def;
    }
  }

  public static double parseOrDefault(String string, double def) {
    try {
      return Double.parseDouble(string);
    } catch (Exception ignored) {
      return def;
    }
  }

  public static float parseOrDefault(String string, float def) {
    try {
      return Float.parseFloat(string);
    } catch (Exception ignored) {
      return def;
    }
  }
}