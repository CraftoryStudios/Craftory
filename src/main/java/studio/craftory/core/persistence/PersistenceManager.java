package studio.craftory.core.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import studio.craftory.core.annotations.Persistent;
import studio.craftory.core.utils.Reflections;

public class PersistenceManager {
  ObjectMapper mapper = new ObjectMapper();
  Map<Class<?>, ArrayList<Field>> persistedFields = new HashMap<>();

  public void registerPersistedClass(Class<?> startClass, Class<?> endClass) {
    if (persistedFields.containsKey(startClass)) return;

    persistedFields.put(startClass, new ArrayList<>(Reflections.getFieldsRecursively(startClass, endClass).stream()
                                    .filter(field -> field.getAnnotation(Persistent.class) != null)
                                    .collect(Collectors.toList())));
  }

  public void saveFields(@NonNull Object object, @NonNull ObjectNode node) {
    if (!persistedFields.containsKey(object.getClass())) {
      registerPersistedClass(object.getClass(), Object.class);
    }

    ArrayList<Field> fields = persistedFields.get(object.getClass());
    for (Field field : fields) {
      field.setAccessible(true);

      try {
        if (field.get(object) != null) {
          node.set(field.getName(), mapper.convertValue(field.get(object), JsonNode.class)) ;
        }
      } catch (IllegalAccessException e) {
        throw new IllegalStateException(
            "Unable to save field " + object.getClass().getSimpleName() + "." + field.getName(), e);
      }
    }
  }
}
