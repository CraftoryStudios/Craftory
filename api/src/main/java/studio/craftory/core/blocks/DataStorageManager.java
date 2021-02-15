package studio.craftory.core.blocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
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
  private final ObjectMapper mapper;

  private Map<World, WorldDataStorage> worldsStorage;

  public DataStorageManager(CustomBlockManager customBlockManager, CustomBlockRegistry blockRegister) {
    this.customBlockManager = customBlockManager;
    this.blockRegister = blockRegister;
    this.worldsStorage = new ConcurrentHashMap<>();
    this.mapper = new ObjectMapper();
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
    for (WorldDataStorage worldDataStorage : worldsStorage.values()) {
      worldDataStorage.save();
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
    WorldDataStorage currentWorld = new WorldDataStorage(world, blockRegister, mapper);
    worldsStorage.put(world, currentWorld);
    for (Chunk chunk : world.getLoadedChunks()) {
      loadSavedBlocks(chunk);
    }
  }

  @Synchronized
  public void loadSavedBlocks(@NonNull Chunk chunk) {
    WorldDataStorage storage = worldsStorage.get(chunk.getWorld());
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
      WorldDataStorage storage = worldsStorage.get(world);
      storage.save();
      worldsStorage.remove(world);
    }
  }
}
