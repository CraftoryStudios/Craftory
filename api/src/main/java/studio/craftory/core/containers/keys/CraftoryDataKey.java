package studio.craftory.core.containers.keys;

import java.io.Serializable;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.Craftory;
import studio.craftory.core.containers.safecontainers.SafePlugin;

public final class CraftoryDataKey implements Serializable {

  private final String namespace;
  private final String name;
  private final Class<?> dataClass;

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
    String[] keySections = key.split(":", 2);
    if (keySections.length == 2) {
      this.namespace = keySections[0];
      this.name = keySections[1];
    } else {
      this.namespace = "Unknown";
      this.name = key.replace(":", "");
    }
    register();
  }

  private void register() {
    Craftory.blockRegistry().registerDataKey(this.toString(), this);
  }


  public Optional<Plugin> getPlugin() {
    return new SafePlugin(this.namespace).getPlugin();
  }

  @Override
  public String toString() {
    return namespace + ":" + name;
  }

  public String getNamespace() {return this.namespace;}

  public String getName() {return this.name;}

  public Class<?> getDataClass() {return this.dataClass;}

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof CraftoryDataKey)) {
      return false;
    }
    final CraftoryDataKey other = (CraftoryDataKey) o;
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
    final Object this$dataClass = this.getDataClass();
    final Object other$dataClass = other.getDataClass();
    if (this$dataClass == null ? other$dataClass != null : !this$dataClass.equals(other$dataClass)) {
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
    final Object $dataClass = this.getDataClass();
    result = result * PRIME + ($dataClass == null ? 43 : $dataClass.hashCode());
    return result;
  }
}
