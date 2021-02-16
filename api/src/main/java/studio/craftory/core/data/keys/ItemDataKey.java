package studio.craftory.core.data.keys;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

@Value
@AllArgsConstructor
public class ItemDataKey {

  NamespacedKey namespacedKey;
  PersistentDataType<Object,Object> dataType;

  public ItemDataKey(@NonNull Plugin plugin, @NonNull String name, @NonNull PersistentDataType<Object,Object> dataType) {
    this.dataType = dataType;
    this.namespacedKey = new NamespacedKey(plugin, name);
  }

}
