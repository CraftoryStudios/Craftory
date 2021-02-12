package studio.craftory.core.blocks;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import studio.craftory.core.blocks.templates.BaseCustomBlock;

public class DataStorageManager {

  private final CustomBlockManager customBlockManager;
  private final CustomBlockRegistry blockRegister;

  private Map<World, WorldContainer> worldsStorage;

  public DataStorageManager(CustomBlockManager customBlockManager, CustomBlockRegistry blockRegister) {
    this.customBlockManager = customBlockManager;
    this.blockRegister = blockRegister;
    this.worldsStorage = new ConcurrentHashMap<>();
  }


  public void writeChunkAndSave(Chunk chunk, Collection<BaseCustomBlock> blocks) {
    World world = chunk.getWorld();
    worldsStorage.get(world).writeChunk(chunk, blocks);
    worldsStorage.get(world).save();
  }

  public void writeChunk(Chunk chunk, Collection<BaseCustomBlock> blocks) {
    World world = chunk.getWorld();
    worldsStorage.get(world).writeChunk(chunk, blocks);
  }

  public void writeAll() {
    Map<Chunk, Map<Location, BaseCustomBlock>> customBlocks = customBlockManager.getCustomBlocks();
    for (Entry<Chunk, Map<Location, BaseCustomBlock>> chunkData : customBlocks.entrySet()) {
      writeChunk(chunkData.getKey(), chunkData.getValue().values());
    }

  }

  public void saveAll() {
    for (WorldContainer worldContainer : worldsStorage.values()) {
      worldContainer.save();
    }
  }

  public void writeBlockAndSave(BaseCustomBlock customBlock) {
    World world = customBlock.getLocation().getWorld();
    worldsStorage.get(world).writeCustomBlock(customBlock);
  }

  public void removeBlockAndSave(BaseCustomBlock customBlock) {
    World world = customBlock.getLocation().getWorld();
    worldsStorage.get(world).removeCustomBlock(customBlock);
  }

  @Synchronized
  public void registerWorld(@NonNull World world) {
    WorldContainer currentWorld = new WorldContainer(world, blockRegister);
    worldsStorage.put(world, currentWorld);
    for (Chunk chunk : world.getLoadedChunks()) {
      loadSavedBlocks(chunk);
    }
  }

  @Synchronized
  public void loadSavedBlocks(@NonNull Chunk chunk) {
    WorldContainer storage = worldsStorage.get(chunk.getWorld());
    if (storage == null) {
      return;
    }
    storage.getSavedBlocksInChunk(chunk)
           .ifPresent(blocks -> blocks.forEach(customBlockManager::loadCustomBlock));
  }

  @Synchronized
  public void unregisterWorld(@NonNull World world) {
    if (worldsStorage.containsKey(world)) {
      customBlockManager.getCustomChunksInWorld(world)
                        .forEach(chunk -> customBlockManager.unloadCustomChunk(chunk, true));
      WorldContainer storage = worldsStorage.get(world);
      storage.save();
      worldsStorage.remove(world);
    }
  }
}
