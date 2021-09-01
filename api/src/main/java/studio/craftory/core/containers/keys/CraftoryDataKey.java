package studio.craftory.core.containers.keys;

import java.io.Serializable;
import java.util.Optional;
import lombok.NonNull;
import lombok.Value;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.Craftory;
import studio.craftory.core.containers.safecontainers.SafePlugin;

@Value
public class CraftoryDataKey implements Serializable {
  String namespace;
  String name;
  Class<?> dataClass;

  public CraftoryDataKey(@NonNull final Plugin plugin, @NonNull final String name, @NonNull final Class<?> dataClass) {
    this.namespace = plugin.getName();
    this.name = name;
    this.dataClass = dataClass;
    register();
  }

  public CraftoryDataKey(@NonNull final String namespace, @NonNull final String name, @NonNull final Class<?> dataClass) {
    this.namespace = namespace;
    this.name = name;
    this.dataClass = dataClass;
    register();
  }

  public CraftoryDataKey(final String key, @NonNull final Class<?> dataClass) {
    this.dataClass = dataClass;
    String[] keySections = key.split(":",2);
    if (keySections.length == 2) {
      this.namespace = keySections[0];
      this.name = keySections[1];
    } else {
      this.namespace = "Unknown";
      this.name = key.replace(":","");
    }
    register();
  }

  private void register() {
    Craftory.getCustomBlockAPI().registerDataKey(this);
  }

  
  public Optional<Plugin> getPlugin() {
    return new SafePlugin(this.namespace).getPlugin();
  }

  @Override
  public String toString() {
    return namespace + ":" + name;
  }

}
