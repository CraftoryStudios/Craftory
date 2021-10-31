package studio.craftory.core.containers.keys;

import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class ItemDataKey {

  private final NamespacedKey namespacedKey;
  private final PersistentDataType dataType;

  public ItemDataKey(@NonNull Plugin plugin, @NonNull String name, @NonNull PersistentDataType dataType) {
    this.dataType = dataType;
    this.namespacedKey = new NamespacedKey(plugin, name);
  }

  public ItemDataKey(NamespacedKey namespacedKey, PersistentDataType dataType) {
    this.namespacedKey = namespacedKey;
    this.dataType = dataType;
  }

  public NamespacedKey getNamespacedKey() {return this.namespacedKey;}

  public PersistentDataType getDataType() {return this.dataType;}

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ItemDataKey)) {
      return false;
    }
    final ItemDataKey other = (ItemDataKey) o;
    final Object this$namespacedKey = this.getNamespacedKey();
    final Object other$namespacedKey = other.getNamespacedKey();
    if (this$namespacedKey == null ? other$namespacedKey != null : !this$namespacedKey.equals(other$namespacedKey)) {
      return false;
    }
    final Object this$dataType = this.getDataType();
    final Object other$dataType = other.getDataType();
    if (this$dataType == null ? other$dataType != null : !this$dataType.equals(other$dataType)) {
      return false;
    }
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $namespacedKey = this.getNamespacedKey();
    result = result * PRIME + ($namespacedKey == null ? 43 : $namespacedKey.hashCode());
    final Object $dataType = this.getDataType();
    result = result * PRIME + ($dataType == null ? 43 : $dataType.hashCode());
    return result;
  }

  public String toString() {return "ItemDataKey(namespacedKey=" + this.getNamespacedKey() + ", dataType=" + this.getDataType() + ")";}
}
