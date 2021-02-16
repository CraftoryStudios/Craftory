package studio.craftory.core.blocks;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.keys.CraftoryBlockKey;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.utils.Log;

public class CustomBlockManager {

  private CustomBlockRegistry blockRegister;
  private AsyncExecutionManager asyncExecutionManager;
  private SyncExecutionManager syncExecutionManager;
  private BlockRenderManager blockRenderManager;
  @Getter
  private DataStorageManager dataStorageManager;

  @Getter
  private Map<Chunk,Map<Location, BaseCustomBlock>> customBlocks;

  @Inject
  public CustomBlockManager (CustomBlockRegistry blockRegister, AsyncExecutionManager asyncExecutionManager, SyncExecutionManager syncExecutionManager) {
    this.blockRegister = blockRegister;
    this.syncExecutionManager = syncExecutionManager;
    this.asyncExecutionManager = asyncExecutionManager;
    this.dataStorageManager = new DataStorageManager(this, blockRegister);
    this.blockRenderManager = new BlockRenderManager(blockRegister);

    this.customBlocks = new ConcurrentHashMap<>();
  }

  /**
   * Loads a Custom Block, into memory and executor when a new block is placed or loaded from file
   *
   * @param customBlock which should be loaded into memory
   */
  public boolean loadCustomBlock(@NonNull final BaseCustomBlock customBlock) {
    Location location = customBlock.getLocation();

    if (customBlocks.computeIfAbsent(location.getChunk(), k -> new ConcurrentHashMap<>())
                      .putIfAbsent(location, customBlock) == null) {

      if (location.getChunk().isLoaded()) {
        addToExecutorSchedule(customBlock);
      }

      Log.debug("Block loaded: " + location.toString());
      return true;
    } else {
      Log.warn("Trying to load a customBlock at occupied location: " + location.toString());
    }
    return false;
  }

  /**
   * Unloads a chunk of custom blocks
   *
   * @param chunk the block location
   * @param save     if the block should be saved
   * @throws IllegalArgumentException if the given location isn't loaded
   */
  @Synchronized
  public void unloadCustomChunk(@NonNull final Chunk chunk, boolean save) {
    Map<Location, BaseCustomBlock> customBlockMap = getLoadedCustomBlocksInChunk(chunk);
    if (customBlockMap.isEmpty()) return;

    if (save) {
      Collection<BaseCustomBlock> blocks = customBlockMap.values();
      dataStorageManager.writeChunkAndSave(chunk, blocks);
    }

    for (BaseCustomBlock block : customBlockMap.values()) {
      removeFromExecutorSchedule(block);
    }
    customBlocks.remove(chunk);
  }

  /**
   * Unloads a CustomBlock from memory
   *
   * @param location the block location
   * @param save     if the block should be saved
   * @throws IllegalArgumentException if the given location isn't loaded
   */
  @Synchronized
  public void unloadCustomBlock(@NonNull final Location location, boolean save) {

    Optional<BaseCustomBlock> customBlockOptional = Optional.ofNullable(customBlocks.get(location.getChunk()).get(location));
    customBlockOptional.ifPresent(customBlock -> unloadCustomBlock(customBlock, save));
  }

  /**
   * Unloads a CustomBlock from memory
   *
   * @param customBlock the block
   * @param save     if the block should be saved
   * @throws IllegalArgumentException if the given blocks location isn't loaded
   */
  @Synchronized
  public void unloadCustomBlock(@NonNull final BaseCustomBlock customBlock, boolean save) {
    Location location = customBlock.getLocation();

    if (!location.getChunk().isLoaded()) {
      throw new IllegalArgumentException("The provided location must be loaded!");
    }

    removeFromExecutorSchedule(customBlock);
    if (save) {
      dataStorageManager.writeBlockAndSave(customBlock);
    } else {
      dataStorageManager.removeBlockAndSave(customBlock);
    }

    customBlocks.get(location.getChunk()).remove(location);
  }



  /**
   * Get the CustomBlock at the given loaded location
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
   * Get the CustomBlock in the given loaded chunk
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

  /**
   * Place a custom block at a given location and then load into memory
   * and also into the executor
   *
   * @param craftoryBlockKey key representing the type of custom block to place
   * @param location in the world to place the block
   * @param direction the block is facing
   * @return instance of the newly created Custom Block or Optional.empty if failed
   */
  public Optional<BaseCustomBlock> placeCustomBlock(@NonNull CraftoryBlockKey craftoryBlockKey, @NonNull Location location,
  @NonNull CraftoryDirection direction) {
    Optional<BaseCustomBlock> customBlock = blockRegister.getNewCustomBlockInstance(craftoryBlockKey, location, direction);

    if (!customBlock.isPresent()) {
      Log.warn("Unable to place CustomBlock: " + craftoryBlockKey.getName() + " at location: " + location);
      return Optional.empty();
    }

    if (loadCustomBlock(customBlock.get())) {
      blockRenderManager.renderCustomBlock(craftoryBlockKey, location.getBlock(), direction);

      return customBlock;
    }

    return Optional.empty();
  }

  @Synchronized
  public Set<Chunk> getCustomChunksInWorld(@NonNull World world) {
    HashSet<Chunk> chunks = customBlocks.keySet().stream().filter(chunk -> chunk.getWorld().equals(world))
                                          .collect(Collectors.toCollection(HashSet::new));
    return Collections.unmodifiableSet(chunks);
  }

  private void addToExecutorSchedule(@NonNull final BaseCustomBlock block) {
    asyncExecutionManager.addTickableObject( block);
    syncExecutionManager.addTickableObject(block);
  }

  private void removeFromExecutorSchedule(@NonNull final BaseCustomBlock block) {
    asyncExecutionManager.removeTickableObject(block);
    syncExecutionManager.removeTickableObject(block);
  }


}
