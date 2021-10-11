package studio.craftory.core.executors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import studio.craftory.core.annotations.Tickable;
import studio.craftory.core.blocks.CustomBlock;
import studio.craftory.core.utils.Log;
import studio.craftory.core.utils.Reflections;

@UtilityClass
public class ExecutorUtils {

  public static void registerTickableClass(Class<? extends CustomBlock> clazz,
      Map<Class<? extends CustomBlock>, Map<Integer, List<Method>>> tickableMethods, boolean async) {

    Map<Integer, List<Method>> tickMethods = new HashMap<>();

    Reflections.getMethodsRecursively(clazz, Object.class).forEach(method -> {
      Tickable tickable = method.getAnnotation(Tickable.class);

      if (Objects.nonNull(tickable) && tickable.async() == async && method.getParameterCount() == 0) {

        List<Method> temp;
        if (tickMethods.containsKey(tickable.ticks())) {
          temp = tickMethods.get(tickable.ticks());
        } else {
          temp = new ArrayList<>();
        }

        temp.add(method);
        tickMethods.put(tickable.ticks(), temp);
      }
    });

    if (!tickMethods.isEmpty()) {
      tickableMethods.put(clazz, tickMethods);
    }
  }

  public static void runMethods(@NonNull Set<TickGroup> tickGroups, int tick, Map<Class<? extends CustomBlock>, Map<Integer, List<Method>>> tickableMethods) {
    for (TickGroup tickGroup : tickGroups) {
      if (tick % tickGroup.tick == 0) {
        for (CustomBlock tickable : tickGroup.getTickables()) {
          for (Method method: tickableMethods.get(tickable.getClass()).get(tickGroup.tick)) {
            try {
              method.invoke(tickable);
            } catch (IllegalAccessException | InvocationTargetException e) {
              Log.error(e.toString());
            }
          }
        }
      }
    }
  }

}
