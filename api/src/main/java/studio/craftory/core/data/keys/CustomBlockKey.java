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

  public CustomBlockKey(Plugin plugin, Class<? extends BaseCustomBlock> block) {
    this.namespace = plugin.getName();
    this.name = block.getSimpleName();
  }

  public Optional<Plugin> getPlugin() {
    return new SafePlugin(this.namespace).getPlugin();
  }

  @Override
  public String toString() {
    return namespace + ":" + name;
  }
}
