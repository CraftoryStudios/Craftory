package studio.craftory.core.blocks;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Chunk;
import org.bukkit.World;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.utils.Log;

public class DataStorageManager {

  private Map<World, WorldContainer> worldsStorage;

  public DataStorageManager() {
    this.worldsStorage = new ConcurrentHashMap<>();
  }


  public void writeChunkAndSave(Chunk chunk, Collection<BaseCustomBlock> blocks) {
    World world = chunk.getWorld();
    worldsStorage.get(world).writeChunk(chunk, blocks);
    worldsStorage.get(world).save();
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
    worldStorage.put(world, currentWorld);
    for (Chunk chunk : world.getLoadedChunks()) {
      currentWorld.getSavedBlocksInChunk(chunk).ifPresent(blocks ->
          blocks.forEach(this::loadCustomBlock));
    }
  }

  @Synchronized
  public void loadSavedBlocks(@NonNull Chunk chunk) {
    WorldContainer storage = worldStorage.get(chunk.getWorld());
    if (storage == null) {
      return;
    }
    storage.getSavedBlocksInChunk(chunk)
           .ifPresent(blocks -> blocks.forEach(this::loadCustomBlock));
  }

  @Synchronized
  public void unregisterWorld(@NonNull World world) {
    if (worldStorage.containsKey(world)) {
      getChunksWithCustomBlocksInWorld(world).forEach(chunk -> unloadCustomChunk(chunk, true));
      WorldContainer storage = worldStorage.get(world);
      try {
        storage.save();
      } catch (IOException e) {
        e.printStackTrace();
      }
      worldStorage.remove(world);
    }
  }
}
