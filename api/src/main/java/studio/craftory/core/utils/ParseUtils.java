package studio.craftory.core.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ParseUtils {

  public static int parseOrDefault(String string, int def) {
    try {
      return Integer.parseInt(string);
    } catch (NumberFormatException ignored) {
      return def;
    }
  }

}