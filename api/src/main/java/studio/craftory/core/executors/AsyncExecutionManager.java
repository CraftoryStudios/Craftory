package studio.craftory.core.executors;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;
import lombok.NonNull;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.blocks.CustomBlock;

public class AsyncExecutionManager extends BukkitRunnable {

  private final List<Set<TickGroup>> tickGroups;
  private final List<Map<Integer, TickGroup>> tickGroupsMap;
  private final Map<Class<? extends CustomBlock>, Map<Integer, List<Method>>> tickableMethods;
  private final Map<Class<? extends CustomBlock>, List<Integer>> threadTaskDistribution;
  private final Map<Integer, Set<CustomBlock>> removeBacklog;
  private final LongAdder tick;
  private final ExecutorService executor;
  private final int threadCount;

  public AsyncExecutionManager() {
    this.threadCount = 4;
    threadTaskDistribution = new HashMap<>();
    tickGroups = new ArrayList<>(threadCount);
    tickGroupsMap = new ArrayList<>(threadCount);
    removeBacklog = new HashMap<>();
    for (int i = 0; i < threadCount; i++) {
      tickGroups.add(new HashSet<>());
      tickGroupsMap.add(new HashMap<>());
    }
    tickableMethods = new HashMap<>();
    tick = new LongAdder();
    executor = Executors.newFixedThreadPool(threadCount, new ThreadFactoryBuilder().setNameFormat("Craftory-Worker-%d").build());
  }

  @Override
  public void run() {
    tick.increment();
    //TODO should occur after execution
    cleanUpTickableObjects();
    for (int threadID = 0; threadID < threadCount; threadID++) {
      Set<TickGroup> threadTasks = tickGroups.get(threadID);
      executor.execute(() -> ExecutorUtils.runMethods(threadTasks, tick.intValue(), tickableMethods));
    }
  }

  public void registerTickableClass(@NonNull Class<? extends CustomBlock> clazz) {
    ExecutorUtils.registerTickableClass(clazz, tickableMethods, true);
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
    for (Entry<Integer, Set<CustomBlock>> entry : removeBacklog.entrySet()) {
      for (int thread = 0; thread < threadCount; thread++) {
        TickGroup tickGroup = tickGroupsMap.get(thread).get(entry.getKey());
        for (CustomBlock tickable : entry.getValue()) {
          tickGroup.getTickables().remove(tickable);
        }

        if (tickGroup.getTickables().isEmpty()) {
          tickGroups.get(thread).remove(tickGroup);
          tickGroupsMap.get(thread).remove(entry.getKey());
        }
      }
    }
  }

  public void addTickableObject(@NonNull CustomBlock tickableObject) {
    Optional<Set<Integer>> tickKeys = getTickKeys(tickableObject);
    if (tickKeys.isEmpty()) {
      return;
    }

    int exectionThread = getExecutionThread(tickableObject.getClass());
    Map<Integer, TickGroup> threadTickGroupMap = tickGroupsMap.get(exectionThread);
    for (Integer integer : tickKeys.get()) {
      TickGroup tickGroup;
      if (threadTickGroupMap.containsKey(integer)) {
        tickGroup = threadTickGroupMap.get(integer);
      } else {
        tickGroup = new TickGroup(integer);
      }

      tickGroup.getTickables().add(tickableObject);
      tickGroups.get(exectionThread).add(tickGroup);
      tickGroupsMap.get(exectionThread).put(integer, tickGroup);
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

  private int getExecutionThread(@NonNull Class<? extends CustomBlock> clazz) {
    if (threadTaskDistribution.containsKey(clazz)) {
      List<Integer> threadWorkloads = threadTaskDistribution.get(clazz);
      int bestThread = 0;
      int taskCount = Integer.MAX_VALUE;
      for (int l = 0; l < threadWorkloads.size(); l++) {
        if (threadWorkloads.get(l) < taskCount) {
          taskCount = threadWorkloads.get(l);
          bestThread = l;
        }
      }
      return bestThread;
    } else {
      ArrayList<Integer> threadTasks = new ArrayList<>(threadCount);
      threadTasks.add(1);
      for (int l = 1; l < threadCount; l++) {
        threadTasks.add(0);
      }
      threadTaskDistribution.put(clazz, threadTasks);
      return 0;
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
