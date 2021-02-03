package studio.craftory.core.blocks;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.keys.CustomBlockKey;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.utils.Log;

/** Class based on LogisticsCraft's Logistics-API (MIT) and the LogisticBlockCache class **/
public class CustomBlockManager {

  private CustomBlockRegister blockRegister;
  private AsyncExecutionManager asyncExecutionManager;
  private SyncExecutionManager syncExecutionManager;

  private Map<Chunk,Map<Location, BaseCustomBlock>> customBlocks;
  private Map<World, WorldStorage> worldStorage;

  @Inject
  public CustomBlockManager (CustomBlockRegister blockRegister, AsyncExecutionManager asyncExecutionManager, SyncExecutionManager syncExecutionManager) {
    this.blockRegister = blockRegister;
    this.syncExecutionManager = syncExecutionManager;
    this.asyncExecutionManager = asyncExecutionManager;

    this.customBlocks = new ConcurrentHashMap<>();
    this.worldStorage = new ConcurrentHashMap<>();
  }

  /**
   * Loads a Custom Block, into memory and executor when a new block is placed or loaded from file
   *
   * @param customBlock which should be loaded into memory
   */
  public boolean loadCustomBlock(@NonNull final BaseCustomBlock customBlock) {
    Location location = customBlock.getLocation().getLocation()
                                   .orElseThrow(() -> new IllegalArgumentException("The provided customBlock must be loaded!"));
    //pluginManager.callEvent(new CustomBlockLoadEvent(location, customBlock));

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

    removeFromExecutorSchedule(customBlock);


    if (save) {
      //pluginManager.callEvent(new CustomBlockSaveEvent(location, customBlock));
      //worldStorage.get(location.getWorld()).saveCustomBlock(customBlock);
    } else {
      //pluginManager.callEvent(new CustomBlockUnloadEvent(location, customBlock));
      //worldStorage.get(location.getWorld()).removeCustomBlock(customBlock);
    }

    customBlocks.get(location.getChunk()).remove(location);
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

  /**
   * Place a custom block at a given location and then load into memory
   * and also into the executor
   *
   * @param customBlockKey key representing the type of custom block to place
   * @param location in the world to place the block
   * @param direction the block is facing
   * @return instance of the newly created Custom Block or Optional.empty if failed
   */
  public Optional<BaseCustomBlock> placeCustomBlock(@NonNull CustomBlockKey customBlockKey, @NonNull Location location,
  @NonNull CraftoryDirection direction) {
    Optional<BaseCustomBlock> customBlock = blockRegister.getNewCustomBlockInstance(customBlockKey, location, direction);

    if (!customBlock.isPresent()) {
      Log.warn("Unable to place CustomBlock: " +customBlockKey.getName() + " at location: " + location);
      return Optional.empty();
    }

    if (loadCustomBlock(customBlock.get())) {
      //TODO Render Custom Block
      location.getBlock().setType(Material.GLASS);
      return customBlock;
    }

    return Optional.empty();
  }


  @Synchronized
  public void registerWorld(@NonNull World world) {
    WorldStorage currentWorld = new WorldStorage(world);
    worldStorage.put(world, currentWorld);
    for (Chunk chunk : world.getLoadedChunks()) {
      currentWorld.getSavedBlocksInChunk(chunk).ifPresent(blocks ->
          blocks.forEach(this::loadCustomBlock));
    }
  }

  @Synchronized
  public Set<Chunk> getChunksWithCustomBlocksInWorld(@NonNull World world) {
    HashSet<Chunk> chunks = customBlocks.keySet().stream().filter(chunk -> chunk.getWorld().equals(world))
                                          .collect(Collectors.toCollection(HashSet::new));
    return Collections.unmodifiableSet(chunks);
  }

  @Synchronized
  public void loadSavedBlocks(@NonNull Chunk chunk) {
    WorldStorage storage = worldStorage.get(chunk.getWorld());
    if (storage == null) {
      return;
    }
    storage.getSavedBlocksInChunk(chunk)
           .ifPresent(blocks -> blocks.forEach(this::loadCustomBlock));
  }

  @Synchronized
  public void unregisterWorld(@NonNull World world) {
    if (worldStorage.containsKey(world)) {
      getChunksWithCustomBlocksInWorld(world).forEach(chunk ->
          getLoadedCustomBlocksInChunk(chunk).forEach((key, value) -> unloadCustomBlock(key, true)));
      WorldStorage storage = worldStorage.get(world);
      try {
        storage.save();
      } catch (IOException e) {
        e.printStackTrace();
      }
      worldStorage.remove(world);
    }
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
