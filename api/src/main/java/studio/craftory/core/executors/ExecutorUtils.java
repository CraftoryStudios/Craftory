package studio.craftory.core.executors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import studio.craftory.core.annotations.SyncTickable;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.utils.Reflections;

@UtilityClass
public class ExecutorUtils {

  public static void registerTickableClass(Class<? extends BaseCustomBlock> clazz,
      Map<Class<? extends BaseCustomBlock>, HashMap<Integer, ArrayList<Method>>> tickableMethods) {

    HashMap<Integer, ArrayList<Method>> tickMethods = new HashMap<>();

    Reflections.getMethodsRecursively(clazz, Object.class).forEach(method -> {
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
      tickableMethods.put(clazz, tickMethods);
    }
  }

  public static void runMethods(@NonNull Set<TickGroup> tickGroups, @NonNull int tick, @NonNull Map<Class<? extends BaseCustomBlock>, HashMap<Integer,
      ArrayList<Method>>> tickableMethods) {
    for (TickGroup tickGroup : tickGroups) {
      if (tick % tickGroup.tick == 0) {
        for (BaseCustomBlock tickable : tickGroup.getTickables()) {
          for (Method method: tickableMethods.get(tickable.getClass()).get(tickGroup.tick)) {
            try {
              method.invoke(tickable);
            } catch (IllegalAccessException | InvocationTargetException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

}
