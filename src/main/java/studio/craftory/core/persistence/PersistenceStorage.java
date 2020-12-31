package studio.craftory.core.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.NonNull;
import studio.craftory.core.persistence.adapters.DataAdapter;

public class PersistenceStorage {

  private Map<Class<?>, DataAdapter<?>> converters;
  private Map<Class<?>, DataAdapter<?>> interfaceConverters;

  public PersistenceStorage() {
    converters = new HashMap<>();
    interfaceConverters = new HashMap<>();
  }

  public <T> void registerInterfaceConverter(@NonNull Class<T> clazz, @NonNull DataAdapter<? extends T> converter) {
    interfaceConverters.putIfAbsent(clazz, converter);
  }

  public <T> void registerDataConverter(@NonNull Class<T> clazz, @NonNull DataAdapter<? extends T> converter) {
    converters.putIfAbsent(clazz, converter);
  }

  public void saveFields(@NonNull Object object, @NonNull JsonNode node) {
//    Reflections.getFieldsRecursively(object.getClass(), Object.class).stream()
//               .filter(field -> field.getAnnotation(Persistent.class) != null).forEach(field -> {
//      field.setAccessible(true);
//      try {
//        if (field.get(object) != null) {
//          saveObject(field.get(object), nbtCompound.addCompound(field.getName()));
//        }
//      } catch (IllegalAccessException e) {
//        throw new IllegalStateException(
//            "Unable to save field " + object.getClass().getSimpleName() + "." + field.getName(), e);
//      }
//    });
  }

  public Class<?> saveObject(@NonNull Object data, @NonNull JsonNode node) {
    Class<?> clazz = data.getClass();

    if (converters.containsKey(data.getClass())) {
      ((DataAdapter<Object>) converters.get(clazz)).store(this, data, node);
      return clazz;
    }

    for (Entry<Class<?>, DataAdapter<?>> entry : interfaceConverters.entrySet()) {
      if (entry.getKey().isInstance(data)) {
        ((DataAdapter<Object>) entry.getValue()).store(this, data, node);
        return entry.getKey();
      }
    }

    //Fallback
    return null;
  }
}
