package studio.craftory.core.blocks;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.containers.keys.CraftoryBlockKey;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.utils.Log;

public class CustomBlockManager {

  private final BlockRegistry blockRegister;
  private final AsyncExecutionManager asyncExecutionManager;
  private final SyncExecutionManager syncExecutionManager;
  private final BlockRenderer blockRenderer;

  @Getter
  private final Map<Chunk,Map<Location, CustomBlock>> customBlocks;

  @Inject
  public CustomBlockManager (BlockRegistry blockRegister, AsyncExecutionManager asyncExecutionManager,
      SyncExecutionManager syncExecutionManager, BlockRenderer blockRenderer) {
    this.blockRegister = blockRegister;
    this.syncExecutionManager = syncExecutionManager;
    this.asyncExecutionManager = asyncExecutionManager;
    this.blockRenderer = blockRenderer;

    this.customBlocks = new ConcurrentHashMap<>();
  }

  /**
   * Loads a Custom Block, into memory and executor when a new block is placed or loaded from file
   *
   * @param customBlock which should be loaded into memory
   */
  public boolean loadCustomBlock(@NonNull final CustomBlock customBlock) {
    Location location = customBlock.getLocation();

    if (customBlocks.computeIfAbsent(location.getChunk(), k -> new ConcurrentHashMap<>())
                    .putIfAbsent(location, customBlock) == null) {

      if (location.getChunk().isLoaded()) {
        addToExecutorSchedule(customBlock);
      }

      Log.debug("Block loaded: " + location);
      return true;
    } else {
      Log.warn("Trying to load a customBlock at occupied location: " + location);
    }
    return false;
  }

  /**
   * Unloads a chunk of custom blocks
   *
   * @param chunk the block location
   * @param save if the block should be saved
   *
   * @throws IllegalArgumentException if the given location isn't loaded
   */
  @Synchronized
  public void unloadCustomChunk(@NonNull final Chunk chunk, boolean save) {
    Map<Location, CustomBlock> customBlockMap = getLoadedCustomBlocksInChunk(chunk);
    if (customBlockMap.isEmpty()) return;

    for (CustomBlock block : customBlockMap.values()) {
      removeFromExecutorSchedule(block);
    }
    customBlocks.remove(chunk);
  }

  /**
   * Unloads a CustomBlock from memory
   *
   * @param location the block location
   * @param save if the block should be saved
   *
   * @throws IllegalArgumentException if the given location isn't loaded
   */

  @Synchronized
  public void unloadCustomBlock(@NonNull final Location location, boolean save) {

    Optional<CustomBlock> customBlockOptional = Optional.ofNullable(customBlocks.get(location.getChunk()).get(location));
    customBlockOptional.ifPresent(customBlock -> unloadCustomBlock(customBlock, save));
  }

  /**
   * Unloads a CustomBlock from memory
   *
   * @param customBlock the block
   * @param save if the block should be saved
   *
   * @throws IllegalArgumentException if the given blocks location isn't loaded
   */
  @Synchronized
  public void unloadCustomBlock(@NonNull final CustomBlock customBlock, boolean save) {
    Location location = customBlock.getLocation();

    if (!location.getChunk().isLoaded()) {
      throw new IllegalArgumentException("The provided location must be loaded!");
    }

    removeFromExecutorSchedule(customBlock);

    customBlocks.get(location.getChunk()).remove(location);
  }


  /**
   * Get the CustomBlock at the given loaded location
   *
   * @param location the location
   *
   * @return the CustomBlock to retrieve
   * @throws IllegalArgumentException if the given location isn't loaded
   */
  //Method from Logistics API
  @Synchronized
  public Optional<CustomBlock> getLoadedCustomBlockAt(@NonNull final Location location) {
    Chunk chunk = location.getChunk();
    if (!chunk.isLoaded()) {
      throw new IllegalArgumentException("The provided location must be loaded!");
    }
    Map<Location, CustomBlock> loadedBlockInChunk = getLoadedCustomBlocksInChunk(chunk);
    if (loadedBlockInChunk == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(loadedBlockInChunk.get(location));
  }

  /**
   * Check if location contains custom block
   */
  @Synchronized
  public boolean containsCustomBlock(@NonNull final Location location) {
    if (location.getChunk().isLoaded()) {
      Map<Location, CustomBlock> chunkData = customBlocks.get(location.getChunk());
      if (chunkData != null) {
        return chunkData.containsKey(location);
      }
    }
    return false;
  }

  /**
   * Get the CustomBlock in the given loaded chunk
   *
   * @param chunk the chunk
   *
   * @return the CustomBlocks in chunk
   * @throws IllegalArgumentException if the given chunk isn't loaded
   */
  //Method from Logistics API
  @Synchronized
  public Map<Location, CustomBlock> getLoadedCustomBlocksInChunk(@NonNull final Chunk chunk) {
    if (!chunk.isLoaded()) {
      throw new IllegalArgumentException("The provided chunk must be loaded!");
    }
    if (!customBlocks.containsKey(chunk)) {
      return Collections.emptyMap();
    }
    return Collections.unmodifiableMap(customBlocks.get(chunk));
  }

  /**
   * Place a custom block at a given location and then load into memory and also into the executor
   *
   * @param craftoryBlockKey key representing the type of custom block to place
   * @param location in the world to place the block
   * @param direction the block is facing
   *
   * @return instance of the newly created Custom Block or Optional.empty if failed
   */
  public Optional<CustomBlock> placeCustomBlock(@NonNull CraftoryBlockKey craftoryBlockKey, @NonNull Location location,
  @NonNull CraftoryDirection direction) {
    Optional<CustomBlock> customBlock = blockRegister.getNewCustomBlockInstance(craftoryBlockKey, location, direction);

    if (customBlock.isEmpty()) {
      Log.warn("Unable to place CustomBlock: " + craftoryBlockKey.getName() + " at location: " + location);
      return Optional.empty();
    }

    if (loadCustomBlock(customBlock.get())) {
      blockRenderer.renderCustomBlock(craftoryBlockKey, location.getBlock(), direction);

      return customBlock;
    }

    return Optional.empty();
  }

  @Synchronized
  public Set<Chunk> getCustomChunksInWorld(@NonNull World world) {
    return customBlocks.keySet().stream().filter(chunk -> chunk.getWorld().equals(world)).collect(Collectors.toUnmodifiableSet());
  }

  private void addToExecutorSchedule(@NonNull final CustomBlock block) {
    asyncExecutionManager.addTickableObject( block);
    syncExecutionManager.addTickableObject(block);
  }

  private void removeFromExecutorSchedule(@NonNull final CustomBlock block) {
    asyncExecutionManager.removeTickableObject(block);
    syncExecutionManager.removeTickableObject(block);
  }


}
