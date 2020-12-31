package studio.craftory.core.data.keys;

import java.io.Serializable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.components.fluid.CraftoryFluid;
import studio.craftory.core.data.SafePlugin;

@Value
public class CraftoryDataKey implements Serializable {

  String namespace;
  String name;
  Class<?> dataClass;

  @NonNull
  public CraftoryDataKey(final Plugin plugin, final String name, final Class<?> dataClass) {
    this.namespace = plugin.getName();
    this.name = name;
    this.dataClass = dataClass;
  }

  @NonNull
  public CraftoryDataKey(final String namespace, final String name, final Class<?> dataClass) {
    this.namespace = namespace;
    this.name = name;
    this.dataClass = dataClass;
  }

  @NonNull
  public CraftoryDataKey(final String key, final Class<?> dataClass) {
    this.dataClass = dataClass;
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
