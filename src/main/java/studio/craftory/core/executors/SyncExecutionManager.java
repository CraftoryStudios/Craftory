package studio.craftory.core.executors;

import com.google.common.collect.Lists;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.executors.interfaces.Tickable;

public class SyncExecutionManager extends BukkitRunnable {

  private HashSet<TickGroup> tickGroups;
  private HashMap<Integer, TickGroup> tickGroupsMap;
  private HashMap<String, HashMap<Integer, ArrayList<Method>>> tickableMethods;
  private int tick;
  private int maxTick;

  public SyncExecutionManager() {
    tickGroups = new HashSet<>();
    tickGroupsMap = new HashMap<>();
    tickableMethods = new HashMap<>();
    tick = 0;
    maxTick = 0;
  }

  @Override
  public void run() {
    tick++;
    for (TickGroup tickGroup : tickGroups) {
      if (tick % tickGroup.tick == 0) {
        for (Tickable tickable : tickGroup.getTickables()) {
          runMethods(tickable, tickableMethods.get(tickable.getClass().getName()).get(tickGroup.tick));
        }
      }
    }
    if (tick == maxTick) {
      tick = 0;
    }
  }

  private void runMethods(Tickable tickable, List<Method> methods) {
    for (Method method: methods) {
      try {
        method.invoke(tickable);
      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  public void registerTickableClass(@NonNull Class<? extends Tickable> clazz) {
    ExecutorUtils.registerTickableClass(clazz, tickableMethods);
  }

  public void addTickableObject(@NonNull Tickable object) {
    if (tickableMethods.containsKey(object.getClass().getName())) {
      Set<Integer> tickKeys = tickableMethods.get(object.getClass().getName()).keySet();
      for (Integer integer : tickKeys) {
        TickGroup tickGroup;
        if (tickGroupsMap.containsKey(integer)) {
          tickGroup = tickGroupsMap.get(integer);
        } else {
          tickGroup = new TickGroup(integer);
        }

        tickGroup.getTickables().add(object);
        tickGroups.add(tickGroup);
        tickGroupsMap.put(integer, tickGroup);
      }
    } else {
      //Error
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
