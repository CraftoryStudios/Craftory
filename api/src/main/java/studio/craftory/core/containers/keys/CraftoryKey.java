package studio.craftory.core.containers.keys;

import java.io.Serializable;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.containers.safecontainers.SafePlugin;

public final class CraftoryKey implements Serializable {

  private final String namespace;
  private final String name;

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

  public CraftoryKey(String namespace, String name) {
    this.namespace = namespace;
    this.name = name;
  }

  public Optional<Plugin> getPlugin() {
    return new SafePlugin(this.namespace).getPlugin();
  }

  @Override
  public String toString() {return namespace + ":" + name;}

  public String getNamespace() {return this.namespace;}

  public String getName() {return this.name;}

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof CraftoryKey)) {
      return false;
    }
    final CraftoryKey other = (CraftoryKey) o;
    final Object this$namespace = this.getNamespace();
    final Object other$namespace = other.getNamespace();
    if (this$namespace == null ? other$namespace != null : !this$namespace.equals(other$namespace)) {
      return false;
    }
    final Object this$name = this.getName();
    final Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
      return false;
    }
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $namespace = this.getNamespace();
    result = result * PRIME + ($namespace == null ? 43 : $namespace.hashCode());
    final Object $name = this.getName();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    return result;
  }
}
