package studio.craftory.core.data.keys;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.safecontainers.SafePlugin;

@Value
@AllArgsConstructor
public class CustomBlockKey {

  String namespace;
  String name;

  public CustomBlockKey(Plugin plugin, Class<?> block) {
    this.namespace = plugin.getName();
    this.name = block.getSimpleName();
  }

  public CustomBlockKey(String key) {
    String[] keySplit = key.split(":");
    this.namespace = keySplit[0];
    this.name = keySplit[1];
  }

  public Optional<Plugin> getPlugin() {
    return new SafePlugin(this.namespace).getPlugin();
  }

  @Override
  public String toString() {
    return namespace + ":" + name;
  }
}
