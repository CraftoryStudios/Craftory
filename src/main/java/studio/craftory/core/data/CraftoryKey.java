package studio.craftory.core.data;

import java.io.Serializable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.bukkit.plugin.Plugin;

@Value
@AllArgsConstructor
public class CraftoryKey implements Serializable {

  private String namespace;
  private String name;

  public CraftoryKey(@NonNull final Plugin plugin, @NonNull final String name) {
    this.namespace = plugin.getName();
    this.name = name;
  }

  public CraftoryKey(@NonNull final String key) {
    String[] keySections = key.split(":",2);
    if (keySections.length == 2) {
      this.namespace = keySections[0];
      this.name = keySections[1];
    } else {
      this.namespace = "Unknown";
      this.name = key.replace(":","");
    }
  }

  public Optional<Plugin> getPlugin() {
    return new SafePlugin(this.namespace).getPlugin();
  }

}
