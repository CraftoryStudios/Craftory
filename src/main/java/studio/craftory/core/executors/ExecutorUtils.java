package studio.craftory.core.executors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import studio.craftory.core.annotations.SyncTickable;
import studio.craftory.core.executors.interfaces.Tickable;
import studio.craftory.core.utils.Reflections;

@UtilityClass
public class ExecutorUtils {

  public static void registerTickableClass(Class<? extends Tickable> clazz,
      Map<String, HashMap<Integer, ArrayList<Method>>> tickableMethods) {

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
      tickableMethods.put(clazz.getName(), tickMethods);
    }
  }
}
