package studio.craftory.core.containers.keys;

import java.io.Serializable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.containers.safecontainers.SafePlugin;

@Value
@AllArgsConstructor
public class CraftoryKey implements Serializable {

  String namespace;
  String name;

  public CraftoryKey(@NonNull final Plugin plugin, @NonNull final String name) {
    this.namespace = plugin.getName();
    this.name = name;
  }

  public CraftoryKey(@NonNull final String key) {
    String[] keySections = key.split(":", 2);
    if (keySections.length == 2) {
      this.namespace = keySections[0];
      this.name = keySections[1];
    } else {
      this.namespace = "Unknown";
      this.name = key.replace(":", "");
    }
  }

  public Optional<Plugin> getPlugin() {
    return new SafePlugin(this.namespace).getPlugin();
  }

  @Override
  public String toString() {return namespace + ":" + name;}

}
