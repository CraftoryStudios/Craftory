package studio.craftory.core.executors;

import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.annotations.SyncTickable;
import studio.craftory.core.executors.interfaces.Tickable;

public class SyncOldExecutionManager extends BukkitRunnable {
  //Custom Block in future
  private final HashMap<Class<? extends Tickable>, HashMap<Method, Integer>> classCache = new HashMap<>();
  private final Set<Tickable> trackedBlocks;
  private long tick = 0;

  public SyncOldExecutionManager() {
    trackedBlocks = ConcurrentHashMap.newKeySet();
  }

  @Override
  @Synchronized
  public void run() {
    tick++;
    for (Tickable tickable : trackedBlocks) {
      HashMap<Method, Integer> tickMap = classCache.get(tickable.getClass());
      tickMap.forEach(((method, current) -> {
        if (tick % current == 0) {
          try {
            method.invoke(tickable);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }));
    }
  }

  @Synchronized
  public void addTickingBlock(@NonNull Tickable tickable) {
    if (classCache.containsKey(tickable.getClass())) {
      trackedBlocks.add(tickable);
    }
  }

  @Synchronized
  public void removeTickingBlock(@NonNull Tickable tickable) {
    trackedBlocks.remove(tickable);
  }

  @Synchronized
  public void registerCustomBlockClass(@NonNull Class<? extends Tickable> clazz) {
    if (!classCache.containsKey(clazz)) {
      Collection<Method> methods = getMethodsRecursively(clazz, Object.class);
      HashMap<Method, Integer> tickingMethods = new HashMap<>();
      methods.forEach(method -> {
        SyncTickable ticking = method.getAnnotation(SyncTickable.class);
        if (ticking != null && method.getParameterCount() == 0) {
          tickingMethods.put(method, ticking.ticks());
        }
      });
      if (tickingMethods.size() > 0) {
        classCache.put(clazz, tickingMethods);
      }
    }
  }

  public static Collection<Method> getMethodsRecursively(@NonNull Class<?> startClass,
      @NonNull Class<?> exclusiveParent) {
    Collection<Method> methods = Lists.newArrayList(startClass.getDeclaredMethods());
    Class<?> parentClass = startClass.getSuperclass();

    if (parentClass != null && !(parentClass.equals(exclusiveParent))) {
      methods.addAll(getMethodsRecursively(parentClass, exclusiveParent));
    }

    return methods;
  }

}
