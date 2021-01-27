package studio.craftory.core.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import studio.craftory.core.Craftory;
import studio.craftory.core.annotations.Persistent;
import studio.craftory.core.data.Pair;
import studio.craftory.core.utils.Reflections;

/** Class inspired by LogisticsCraft's Logistics-API and the PersistenceManager class **/
public class PersistenceManager {
  private Gson gson;
  private Map<Class<?>, ArrayList<Pair<String,Field>>> persistedFields;
  private Map<String, Class<?>> craftoryDataKeyMap;

  public PersistenceManager() {
    this.gson = Craftory.getInstance().getGson();
    persistedFields = new HashMap<>();
    craftoryDataKeyMap = new HashMap<>();
  }

  public void registerPersistedClass(Class<?> startClass, Class<?> endClass) {
    if (persistedFields.containsKey(startClass)) return;

    ArrayList<Pair<String,Field>> fields = new ArrayList<>();

    Reflections.getFieldsRecursively(startClass, endClass).stream()
               .filter(field -> field.getAnnotation(Persistent.class) != null)
               .forEach(field -> {
                 String name = field.getAnnotation(Persistent.class).name();
                 fields.add(new Pair<>(name.equals("") ? field.getName() : name, field));
               });

    persistedFields.put(startClass, fields);
  }

  public void registerDataKey(String key, Class<?> dataType) {
    craftoryDataKeyMap.putIfAbsent(key, dataType);
  }

  public JsonElement saveFields(@NonNull Object object) {
    if (!persistedFields.containsKey(object.getClass())) {
      registerPersistedClass(object.getClass(), Object.class);
    }

    JsonObject jsonObject = new JsonObject();

    ArrayList<Pair<String,Field>> fields = persistedFields.get(object.getClass());
    for (Pair<String,Field> fieldPair : fields) {
      fieldPair.getValue().setAccessible(true);

      try {
        if (fieldPair.getValue().get(object) != null) {
          jsonObject.add(fieldPair.getKey(), gson.toJsonTree(fieldPair.getValue().get(object)));
        }
      } catch (IllegalAccessException e) {
        throw new IllegalStateException(
            "Unable to save field " + object.getClass().getSimpleName() + "." + fieldPair.getValue().getName(), e);
      }
    }
    return jsonObject;
  }
}
