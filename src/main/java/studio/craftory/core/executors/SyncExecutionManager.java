package studio.craftory.core.executors;

import com.google.common.collect.Lists;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.annotations.SyncTickable;
import studio.craftory.core.executors.interfaces.Tickable;

public class SyncExecutionManager extends BukkitRunnable {

  private HashSet<TickGroup> tickGroups;
  private HashMap<Integer, TickGroup> tickGroupsMap;
  private HashMap<Class<? extends Tickable>, HashMap<Integer, ArrayList<Method>>> tickableMethods;
  private int tick;
  private int maxTick;
  private int x;
  private int length;

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

        for (Tickable tickable : tickGroup.tickables) {
          ArrayList<Method> tickMethods = tickableMethods.get(tickable.getClass()).get(tickGroup.tick);
          length = tickMethods.size();

          for (x = 0; x < length; x++) {
            try {
              tickMethods.get(x).invoke(tickable);
            } catch (IllegalAccessException | InvocationTargetException e) {
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

    tickableMethods.put(clazz, tickMethods);

  }

  public void addTickableObject(@NonNull Tickable object) {
    if (tickableMethods.containsKey(object.getClass())) {
      Set<Integer> tickKeys = tickableMethods.get(object.getClass()).keySet();
      for (Integer integer : tickKeys) {
        TickGroup tickGroup;
        if (tickGroupsMap.containsKey(integer)) {
          tickGroup = tickGroupsMap.get(integer);
        } else {
          tickGroup = new TickGroup(integer);
        }

        tickGroup.tickables.add(object);
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
