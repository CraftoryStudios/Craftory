package studio.craftory.core.data.persitanceholders;

import java.util.HashMap;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import studio.craftory.core.data.CraftoryKey;

@NoArgsConstructor
public class DataHolder {

  //Data container for component data
  //@Persistent
  private HashMap<CraftoryKey, Object> data = new HashMap<>();

  /**
   * Set the property value with the given key.
   *
   * @param key   the property key
   * @param value the value
   * @param <T>   the value type
   */
  public <T> void set(@NonNull CraftoryKey key, T value) {
    data.put(key, value);
  }

  /**
   * Removes the property with the given key.
   *
   * @param key the property key
   */
  public void remove(@NonNull CraftoryKey key) {
    data.remove(key);
  }

  /**
   * Get the property value with the given key.
   *
   * @param key  the property key
   * @param type the expected object type
   * @return the saved value
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<T> get(@NonNull CraftoryKey key, Class<T> type) {
    return Optional.ofNullable(type.cast(data.get(key)));
  }
}
