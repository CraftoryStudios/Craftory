package studio.craftory.core.executors;

import java.util.HashMap;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncExecutionManager extends BukkitRunnable {
  //Each array of tasks are grouped by tick interval
  private HashMap<Integer, Runnable[]> tasks = new HashMap<>();
  private int tick = 0;
  private int maxTick = 0;

  @Override
  public void run() {
    tick++;

    if (tick == maxTick) {
      tick = 0;
    }
  }
}
