package studio.craftory.core.blocks.storage;

import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import lombok.NonNull;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.Craftory;

/*
 * Made by mfnalex / JEFF Media GbR (Donation link https://paypal.me/mfnalex)
 * Altered and adapted by Brett Saunders
 */
public class CustomBlockData implements PersistentDataContainer {
  private static final NamespacedKey customBlockKey = new NamespacedKey(Craftory.getInstance(), "customblocks");

  private final PersistentDataContainer container;
  private final PersistentDataContainer customBlocksContainer;
  private final Chunk chunk;
  private final NamespacedKey key;

  /**
   * Gets the PersistentDataContainer associated with the given block and plugin
   *  @param blockLocation  Block location
   * @param plugin Plugin
   */
  public CustomBlockData(final @NonNull Location blockLocation, final @NonNull Plugin plugin) {
    this.chunk = blockLocation.getChunk();
    this.key = new NamespacedKey(plugin, getKey(blockLocation));
    this.customBlocksContainer = getCustomBlockContainer();
    this.container = getPersistentDataContainer(this.customBlocksContainer);
  }

  /**
   * Gets a NamespacedKey that consists of the block's relative coordinates within its chunk
   *
   * @param blockLocation block location
   * @return NamespacedKey consisting of the block's relative coordinates within its chunk
   */
  @NonNull
  private static String getKey(@NonNull Location blockLocation) {
    final int x = blockLocation.getBlockX() & 0x000F;
    final int y = blockLocation.getBlockY() & 0x00FF;
    final int z = blockLocation.getBlockZ() & 0x000F;
    return String.format("%d-%d-%d", x, y, z);
  }

  /**
   * Removes all custom block data
   */
  public void clear() {
    if(container.has(key, PersistentDataType.TAG_CONTAINER)) {
      container.remove(key);
    }
    save();
  }

  /**
   * Gets the PersistentDataContainer associated with this block.
   *
   * @return PersistentDataContainer of this block
   */
  @NonNull
  private PersistentDataContainer getPersistentDataContainer(PersistentDataContainer customBlockContainer) {
    final PersistentDataContainer blockContainer;
    if (customBlockContainer.has(key, PersistentDataType.TAG_CONTAINER)) {
      blockContainer = customBlockContainer.get(key, PersistentDataType.TAG_CONTAINER);
      return blockContainer;
    }
    blockContainer = customBlockContainer.getAdapterContext().newPersistentDataContainer();
    customBlockContainer.set(key, PersistentDataType.TAG_CONTAINER, blockContainer);
    return blockContainer;
  }

  @NonNull
  private PersistentDataContainer getCustomBlockContainer() {
    final PersistentDataContainer customBlockContainer;
    final PersistentDataContainer chunkContainer = chunk.getPersistentDataContainer();
    if (chunkContainer.has(customBlockKey, PersistentDataType.TAG_CONTAINER)) {
      customBlockContainer = chunkContainer.get(customBlockKey, PersistentDataType.TAG_CONTAINER);
      return customBlockContainer;
    }
    customBlockContainer = chunkContainer.getAdapterContext().newPersistentDataContainer();
    chunkContainer.set(customBlockKey, PersistentDataType.TAG_CONTAINER, customBlockContainer);
    return customBlockContainer;
  }

  /**
   * Saves the block's PersistentDataContainer inside the chunk's PersistentDataContainer
   */
  private void save() {
    if (container.isEmpty()) {
      chunk.getPersistentDataContainer().get(customBlockKey, PersistentDataType.TAG_CONTAINER).remove(key);
    } else {
      PersistentDataContainer chunkContainer = chunk.getPersistentDataContainer();
      PersistentDataContainer blockContainer = chunkContainer.get(customBlockKey, PersistentDataType.TAG_CONTAINER);
      blockContainer.set(key, PersistentDataType.TAG_CONTAINER, container);
      chunkContainer.set(customBlockKey, PersistentDataType.TAG_CONTAINER, blockContainer);
    }
  }

  @Override
  public <T, Z> void set(final @NonNull NamespacedKey namespacedKey, final @NonNull PersistentDataType<T, Z> persistentDataType, final @NonNull Z z) {
    container.set(namespacedKey, persistentDataType, z);
    save();
  }

  @Override
  public <T, Z> boolean has(final @NonNull NamespacedKey namespacedKey, final @NonNull PersistentDataType<T, Z> persistentDataType) {
    return container.has(namespacedKey, persistentDataType);
  }


  @Override
  public <T, Z> Z get(final @NonNull NamespacedKey namespacedKey, final @NonNull PersistentDataType<T, Z> persistentDataType) {
    return container.get(namespacedKey, persistentDataType);
  }

  @NonNull
  @Override
  public <T, Z> Z getOrDefault(final @NonNull NamespacedKey namespacedKey, final @NonNull PersistentDataType<T, Z> persistentDataType, final @NonNull Z z) {
    return container.getOrDefault(namespacedKey, persistentDataType, z);
  }

  @NonNull
  @Override
  public Set<NamespacedKey> getKeys() {
    return container.getKeys();
  }

  @Override
  public void remove(final @NonNull NamespacedKey namespacedKey) {
    container.remove(namespacedKey);
    save();
  }

  @Override
  public boolean isEmpty() {
    return container.isEmpty();
  }

  @NonNull
  @Override
  public PersistentDataAdapterContext getAdapterContext() {
    return container.getAdapterContext();
  }
}
