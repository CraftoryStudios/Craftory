package studio.craftory.core.executors;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;
import lombok.NonNull;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.annotations.SyncTickable;
import studio.craftory.core.executors.interfaces.Tickable;

public class AsyncExecutionManager extends BukkitRunnable {

  private ArrayList<HashSet<TickGroup>> tickGroups;
  private ArrayList<HashMap<Integer, TickGroup>> tickGroupsMap;
  private HashMap<String, HashMap<Integer, ArrayList<Method>>> tickableMethods;
  private HashMap<String, ArrayList<Integer>> threadTaskDistribution;
  private final LongAdder tick;
  private final ExecutorService executor;
  private final int threadCount;

  public AsyncExecutionManager(int threadCount) {
    this.threadCount = threadCount;
    threadTaskDistribution = new HashMap<>();
    tickGroups = new ArrayList<>(threadCount);
    tickGroupsMap = new ArrayList<>(threadCount);
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
    for (int threadID = 0; threadID < threadCount; threadID++) {
      HashSet<TickGroup> threadTasks = tickGroups.get(threadID);
      executor.execute(() -> threadTask(threadTasks, tick, tickableMethods));
    }
  }

  private void threadTask(@NonNull HashSet<TickGroup> threadTasks, @NonNull LongAdder currentTick,
      @NonNull HashMap<String, HashMap<Integer, ArrayList<Method>>> tickMethods) {
    for (TickGroup tickGroup: threadTasks) {
      if (currentTick.intValue() % tickGroup.tick == 0) {
        for (Tickable tickableObject : tickGroup.tickables) {
          final ArrayList<Method> methods = tickMethods.get(tickableObject.getClass().getName()).get(tickGroup.tick);
          int length = methods.size();
          if (length == 0) continue;

          int x;
          for (x = 0; x < length; x++) {
            final Method method = methods.get(x);
            try {
              method.invoke(tickableObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  public void registerTickableClass(@NonNull Class<? extends Tickable> clazz) {
    //if (!tickableMethods.containsKey(clazz)) return;

    HashMap<Integer, ArrayList<Method>> tickMethods = new HashMap<>();


    getMethodsRecursively(clazz, Object.class).forEach(method -> {
      SyncTickable syncTickable = method.getAnnotation(SyncTickable.class);
      if (Objects.nonNull(syncTickable) && method.getParameterCount() == 0) {

        ArrayList<Method> temp;
        if (tickMethods.containsKey(syncTickable.ticks())) {
          temp = tickMethods.get(syncTickable.ticks());
        } else {
          temp = new ArrayList<>();
        }

        temp.add(method);
        tickMethods.put(syncTickable.ticks(), temp);
      }
    });
    if (!tickMethods.isEmpty()) {
      tickableMethods.put(clazz.getName(), tickMethods);
    }
  }

  public void addTickableObject(@NonNull Tickable object) {
    if (tickableMethods.containsKey(object.getClass().getName())) {
      Set<Integer> tickKeys = tickableMethods.get(object.getClass().getName()).keySet();
      if (tickKeys.isEmpty()) return;
      int exectionThread = getExectionThread(object.getClass().getName());
      HashMap<Integer,TickGroup> threadTickGroupMap = tickGroupsMap.get(exectionThread);
      for (Integer integer : tickKeys) {
        TickGroup tickGroup;
        if (threadTickGroupMap.containsKey(integer)) {
          tickGroup = threadTickGroupMap.get(integer);
        } else {
          tickGroup = new TickGroup(integer);
        }

        tickGroup.tickables.add(object);
        tickGroups.get(exectionThread).add(tickGroup);
        tickGroupsMap.get(exectionThread).put(integer, tickGroup);
      }
    }

  }

  private int getExectionThread(@NonNull String className) {
    if (threadTaskDistribution.containsKey(className)) {
      ArrayList<Integer> threadWorkloads = threadTaskDistribution.get(className);
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
      threadTaskDistribution.put(className,threadTasks);
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
