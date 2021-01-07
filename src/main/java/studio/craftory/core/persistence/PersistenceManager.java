package studio.craftory.core.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import studio.craftory.core.annotations.Persistent;
import studio.craftory.core.data.Pair;
import studio.craftory.core.data.keys.CraftoryDataKey;
import studio.craftory.core.utils.Reflections;

public class PersistenceManager {
  @Getter
  Gson gson = new Gson();
  Map<Class<?>, ArrayList<Pair<String,Field>>> persistedFields = new HashMap<>();

  public static Map<String, CraftoryDataKey> craftoryDataKeyMap = new HashMap<>();

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
