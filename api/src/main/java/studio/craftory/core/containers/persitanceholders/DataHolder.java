package studio.craftory.core.containers.persitanceholders;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import studio.craftory.core.containers.keys.CraftoryDataKey;

/** Class inspired by LogisticsCraft's Logistics-API and the DataHolder class **/
@NoArgsConstructor
public class DataHolder {

  //Data container for component data
  @Getter
  private Map<CraftoryDataKey, Object> data = new HashMap<>();

  /**
   * Set the property value with the given key.
   *
   * @param key   the property key
   * @param value the value
   * @param <T>   the value type
   */
  public <T> void set(@NonNull CraftoryDataKey key, T value) {
    data.put(key, value);
  }

  /**
   * Removes the property with the given key.
   *
   * @param key the property key
   */
  public void remove(@NonNull CraftoryDataKey key) {
    data.remove(key);
  }

  /**
   * Get the property value with the given key.
   *
   * @param key  the property key
   * @return the saved value
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<T> get(@NonNull CraftoryDataKey key) {
    return (Optional<T>) Optional.ofNullable(key.getDataClass().cast(data.get(key)));
  }
}
