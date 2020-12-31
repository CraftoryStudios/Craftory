package studio.craftory.core.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Data;
import lombok.NonNull;
import studio.craftory.core.annotations.Persistent;
import studio.craftory.core.persistence.adapters.DataAdapter;
import studio.craftory.core.utils.Reflections;

public class PersistenceStorage {

  private Map<Class<?>, DataAdapter<?>> converters;
  private Map<Class<?>, DataAdapter<?>> interfaceConverters;

  public PersistenceStorage() {
    converters = new HashMap<>();
    interfaceConverters = new HashMap<>();
  }

  @NonNull
  public <T> void registerInterfaceConverter(Class<T> clazz, DataAdapter<? extends T> converter) {
    interfaceConverters.putIfAbsent(clazz, converter);
  }

  @NonNull
  public <T> void registerDataConverter(Class<T> clazz, DataAdapter<? extends T> converter) {
    converters.putIfAbsent(clazz, converter);
  }

  @NonNull
  public void saveFields(Object object, JsonNode node) {
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

  @NonNull
  public Class<?> saveObject(Object data, JsonNode node) {
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
