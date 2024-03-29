package studio.craftory.core.executors;

import static studio.craftory.core.executors.ExecutorUtils.runMethods;

import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.blocks.CustomBlock;

public class SyncExecutionManager extends BukkitRunnable {

  private final Set<TickGroup> tickGroups;
  private final Map<Integer, TickGroup> tickGroupsMap;
  private final Map<Class<? extends CustomBlock>, Map<Integer, List<Method>>> tickableMethods;
  private final Map<Integer, Set<CustomBlock>> removeBacklog;
  private final int maxTick;

  private int tick;


  public SyncExecutionManager() {
    tickGroups = new HashSet<>();
    tickGroupsMap = new HashMap<>();
    tickableMethods = new HashMap<>();
    removeBacklog = new HashMap<>();
    tick = 0;
    maxTick = 0;
  }

  @Override
  public void run() {
    tick++;
    for (TickGroup tickGroup : tickGroups) {
      if (tick % tickGroup.tick == 0) {
        runMethods(tickGroups, tick, tickableMethods);
      }
    }
    cleanUpTickableObjects();

    if (tick == maxTick) {
      tick = 0;
    }
  }

  public void registerTickableClass(@NonNull Class<? extends CustomBlock> clazz) {
    ExecutorUtils.registerTickableClass(clazz, tickableMethods, false);
  }

  public void removeTickableObject(@NonNull CustomBlock tickableObject) {
    Optional<Set<Integer>> tickKeys = getTickKeys(tickableObject);
    if (tickKeys.isEmpty()) {
      return;
    }

    for (Integer key : tickKeys.get()) {
      removeBacklog.computeIfAbsent(key, a -> new HashSet<>())
                   .add(tickableObject);
    }
  }

  private void cleanUpTickableObjects() {
    for (Iterator<Entry<Integer, Set<CustomBlock>>> it = removeBacklog.entrySet().iterator(); it.hasNext();) {
      Entry<Integer, Set<CustomBlock>> entry = it.next();
        TickGroup tickGroup = tickGroupsMap.get(entry.getKey());

        for (Iterator<CustomBlock> iterator = entry.getValue().iterator(); iterator.hasNext();) {
          CustomBlock customBlock = iterator.next();
          tickGroup.getTickables().remove(customBlock);
          iterator.remove();
        }

      if (entry.getValue().isEmpty()) {
        it.remove();
      }

      if (tickGroup.getTickables().isEmpty()) {
        tickGroups.remove(tickGroup);
        tickGroupsMap.remove(entry.getKey());
      }
    }
  }

  private Optional<Set<Integer>> getTickKeys(@NonNull CustomBlock tickableObject) {
    if (!tickableMethods.containsKey(tickableObject.getClass())) return Optional.empty();
    Set<Integer> tickKeys = tickableMethods.get(tickableObject.getClass()).keySet();
    if (tickKeys.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(tickKeys);
  }

  public void addTickableObject(@NonNull CustomBlock tickableObject) {
    if (tickableMethods.containsKey(tickableObject.getClass())) {
      Set<Integer> tickKeys = tickableMethods.get(tickableObject.getClass()).keySet();
      for (Integer integer : tickKeys) {
        TickGroup tickGroup;
        if (tickGroupsMap.containsKey(integer)) {
          tickGroup = tickGroupsMap.get(integer);
        } else {
          tickGroup = new TickGroup(integer);
        }

        tickGroup.getTickables().add(tickableObject);
        tickGroups.add(tickGroup);
        tickGroupsMap.put(integer, tickGroup);
      }
    }
  }


  private Collection<Method> getMethodsRecursively(@NonNull Class<?> startClass, @NonNull Class<?> exclusiveParent) {
    Collection<Method> methods = Lists.newArrayList(startClass.getDeclaredMethods());
    Class<?> parentClass = startClass.getSuperclass();

    if (parentClass != null && !(parentClass.equals(exclusiveParent))) {
      methods.addAll(getMethodsRecursively(parentClass, exclusiveParent));
    }
    return methods;
  }

}
