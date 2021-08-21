package studio.craftory.core.blocks.storage;

import org.bukkit.NamespacedKey;
import studio.craftory.core.Craftory;

public class StorageKeys {
  public static final NamespacedKey directionKey = new NamespacedKey(Craftory.getInstance(), "direction");
  public static final NamespacedKey blockType = new NamespacedKey(Craftory.getInstance(), "type");
  public static final NamespacedKey customBlocksKey = new NamespacedKey(Craftory.getInstance(), "customblocks");
}
