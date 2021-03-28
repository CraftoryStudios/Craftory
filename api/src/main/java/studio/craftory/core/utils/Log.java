package studio.craftory.core.utils;

import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import studio.craftory.core.Craftory;

@UtilityClass
public final class Log {

  private final ChatColor INFO_COLOR = ChatColor.GREEN;
  private final ChatColor ERROR_COLOR = ChatColor.RED;
  private final ChatColor DEBUG_COLOR = ChatColor.AQUA;
  private final String PREFIX = "[" + Craftory.getInstance().getDescription().getPrefix() + "] ";
  private final String DEBUG_PREFIX = "[" + Craftory.getInstance().getDescription().getPrefix() + " Debug] ";

  @Getter
  @Setter
  private boolean debug = false;
  @Setter
  private Logger logger;

  public static void info(@NonNull String... logMessages) {
    for (String logMessage : logMessages) {
      logger.info(INFO_COLOR + logMessage);
    }
  }

  public static void infoDiscrete(@NonNull String... logMessages) {
    for (String logMessage : logMessages) {
      logger.info(PREFIX  + logMessage);
    }
  }

  public static void debug(@NonNull String... logMessages) {
    if (debug) {
      for (String logMessage : logMessages) {
        logger.info(DEBUG_PREFIX + DEBUG_COLOR + logMessage);
      }
    }
  }

  public static void warn(@NonNull String... logMessages) {
    for (String logMessage : logMessages) {
      logger.warning(PREFIX + logMessage);
    }
  }

  public static void error(@NonNull String... logMessages) {
    for (String logMessage : logMessages) {
      logger.severe(ERROR_COLOR +logMessage);
    }
  }

}
