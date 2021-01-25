package studio.craftory.core.blocks;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.executors.interfaces.Tickable;
import studio.craftory.core.utils.Log;

/** Class based on LogisticsCraft's Logistics-API (MIT) and the LogisticBlockCache class **/
public class CustomBlockManager {

  private CustomBlockRegister blockRegister;
  private PluginManager pluginManager;
  private AsyncExecutionManager asyncExecutionManager;
  private SyncExecutionManager syncExecutionManager;

  private Map<Chunk,Map<Location, BaseCustomBlock>> customBlocks;
  private Map<World, WorldStorage> worldStorage;

  @Inject
  public CustomBlockManager (CustomBlockRegister blockRegister, PluginManager pluginManager, AsyncExecutionManager asyncExecutionManager,
      SyncExecutionManager syncExecutionManager) {
    this.blockRegister = blockRegister;
    this.pluginManager = pluginManager;
    this.syncExecutionManager = syncExecutionManager;
    this.asyncExecutionManager = asyncExecutionManager;

    this.customBlocks = new ConcurrentHashMap<>();
    this.worldStorage = new ConcurrentHashMap<>();
  }

  /**
   * Loads a Custom Block, this method should be called only when a new block is placed or when
   * a stored block is loaded from the disk.
   *
   * @param block the block
   * @throws IllegalArgumentException if the given block location isn't loaded
   */
  public void loadCustomBlock(@NonNull final BaseCustomBlock block) {
    if (!blockRegister.isBlockRegistered(block)) {
      throw new IllegalArgumentException("The class " + block.getClass().getName() + " is not registered!");
    }
    Location location = block.getSafeBlockLocation().getLocation()
                             .orElseThrow(() -> new IllegalArgumentException("The provided block must be loaded!"));
    Chunk chunk = location.getChunk();
    //pluginManager.callEvent(new CustomBlockLoadEvent(location, block));
    if (customBlocks.computeIfAbsent(chunk, k -> new ConcurrentHashMap<>())
                      .putIfAbsent(location, block) == null) {
      addToExecutorSchedule(block);
      Log.debug("Block loaded: " + location.toString());
    } else {
      Log.warn("Trying to load a block at occupied location: " + location.toString());
    }
  }

  /**
   * Unloads a CustomBlock, this method should be called only when a block is destroyed or when
   * a chunk is unloaded.
   *
   * @param location the block location
   * @param save     if the block should be saved
   * @throws IllegalArgumentException if the given location isn't loaded
   */
  @Synchronized
  public void unloadCustomBlock(@NonNull final Location location, boolean save) {
    Chunk chunk = location.getChunk();
    if (!chunk.isLoaded()) {
      throw new IllegalArgumentException("The provided location must be loaded!");
    }
    Map<Location, BaseCustomBlock> loadedBlocksInChunk = customBlocks.get(chunk);
    if (loadedBlocksInChunk == null) {
      Log.warn("Attempt to unregister an unloaded CustomBlock: " + location.toString());
      return;
    }
    BaseCustomBlock customBlock = loadedBlocksInChunk.get(location);
    if (customBlock == null) {
      Log.warn("Attempt to unregister an unknown CustomBlock: " + location.toString());
      return;
    }

    if (Tickable.class.isAssignableFrom(customBlock.getClass())) {
      asyncExecutionManager.removeTickableObject((Tickable) customBlock);
      syncExecutionManager.removeTickableObject((Tickable) customBlock);
    }



    if (save) {
      //pluginManager.callEvent(new CustomBlockSaveEvent(location, customBlock));
      //worldStorage.get(location.getWorld()).saveCustomBlock(customBlock);
    } else {
      //pluginManager.callEvent(new CustomBlockUnloadEvent(location, customBlock));
      //worldStorage.get(location.getWorld()).removeCustomBlock(customBlock);
    }
    customBlocks.get(location.getChunk()).remove(location);
  }

  private void addToExecutorSchedule(@NonNull final BaseCustomBlock block) {
    if (Tickable.class.isAssignableFrom(block.getClass())) {
      asyncExecutionManager.addTickableObject((Tickable) block);
      syncExecutionManager.addTickableObject((Tickable) block);
    }
  }

  /**
   * Get the CustomBlock at the given LOADED location
   *
   * @param location the location
   * @return the CustomBlock to retrieve
   * @throws IllegalArgumentException if the given location isn't loaded
   */
  //Method from Logistics API
  @Synchronized
  public Optional<BaseCustomBlock> getLoadedCustomBlockAt(@NonNull final Location location) {
    Chunk chunk = location.getChunk();
    if (!chunk.isLoaded()) {
      throw new IllegalArgumentException("The provided location must be loaded!");
    }
    Map<Location, BaseCustomBlock> loadedBlockInChunk = getLoadedCustomBlocksInChunk(chunk);
    if (loadedBlockInChunk == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(loadedBlockInChunk.get(location));
  }

  /**
   * Get the CustomBlock in the given LOADED chunk
   *
   * @param chunk the chunk
   * @return the CustomBlocks in chunk
   * @throws IllegalArgumentException if the given chunk isn't loaded
   */
  //Method from Logistics API
  @Synchronized
  public Map<Location, BaseCustomBlock> getLoadedCustomBlocksInChunk(@NonNull final Chunk chunk) {
    if (!chunk.isLoaded()) {
      throw new IllegalArgumentException("The provided chunk must be loaded!");
    }
    if (!customBlocks.containsKey(chunk)) {
      return Collections.emptyMap();
    }
    return Collections.unmodifiableMap(customBlocks.get(chunk));
  }

}
