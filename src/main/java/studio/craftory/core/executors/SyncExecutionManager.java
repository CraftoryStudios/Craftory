package studio.craftory.core.executors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ForkJoinPool;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.executors.interfaces.Tickable;

public class SyncExecutionManager extends BukkitRunnable {

  private ArrayList<TickGroup> tickGroups;
  private HashMap<Class<? extends Tickable>, HashMap<Integer, ArrayList<Method>>> tickableMethods;
  private int tick;
  private int maxTick;
  private int tickGroupsLength;
  private int i;
  private int j;
  private int x;
  private int length;

  private ForkJoinPool forkJoinPool = new ForkJoinPool(2);

  public SyncExecutionManager() {
    tickGroups = new ArrayList<>();
    tickableMethods = new HashMap<>();
    tick = 0;
    maxTick = 0;
    tickGroupsLength = 0;
  }

  @Override
  public void run() {
    tick++;
    for (i = 0; i < tickGroupsLength; i++) {
      if (tick % tickGroups.get(i).tick == 0) {
        Iterator<Tickable> iterator = tickGroups.get(i).tickables.iterator();
        
        while (iterator.hasNext()) {
          Tickable tickable = iterator.next();
          ArrayList<Method> tickMethods = tickableMethods.get(tickable.getClass()).get(tickGroups.get(i));
          length = tickMethods.size();

          for (x = 0; x < length; x++) {
            try {
              tickMethods.get(x).invoke(tickable);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
    if (tick == maxTick) {
      tick = 0;
    }
  }
}
