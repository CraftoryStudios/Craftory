package studio.craftory.core.containers.keys;

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
  PersistentDataType dataType;

  public ItemDataKey(@NonNull Plugin plugin, @NonNull String name, @NonNull PersistentDataType dataType) {
    this.dataType = dataType;
    this.namespacedKey = new NamespacedKey(plugin, name);
  }

}
