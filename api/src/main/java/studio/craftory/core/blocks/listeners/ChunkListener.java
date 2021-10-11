package studio.craftory.core.blocks.listeners;

import java.util.Optional;
import javax.inject.Inject;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.blocks.BlockRegistry;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.CustomBlock;
import studio.craftory.core.blocks.storage.types.CraftoryBlockKeyDataType;
import studio.craftory.core.blocks.storage.types.CraftoryDirectionDataType;
import studio.craftory.core.blocks.storage.StorageKeys;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.containers.keys.CraftoryBlockKey;

public class ChunkListener implements Listener {
  @Inject
  private CustomBlockManager customBlockManager;
  @Inject
  private BlockRegistry blockRegistry;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChunkLoad(ChunkLoadEvent event) {
    loadCustomBlocks(event.getChunk());
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onChunkUnload(ChunkUnloadEvent event) {
    customBlockManager.unloadCustomChunk(event.getChunk(), true);
  }

  private void loadCustomBlocks(Chunk chunk) {
    if (!chunk.getPersistentDataContainer().has(StorageKeys.customBlocksKey, PersistentDataType.TAG_CONTAINER)) return;
    final PersistentDataContainer customBlocksContainer = chunk.getPersistentDataContainer().get(StorageKeys.customBlocksKey,
        PersistentDataType.TAG_CONTAINER);
    PersistentDataContainer customBlockContainer;

    for (NamespacedKey customBlockKey : customBlocksContainer.getKeys()) {
      customBlockContainer = customBlocksContainer.get(customBlockKey, PersistentDataType.TAG_CONTAINER);

      // Get Custom Block Location
      String[] coords = customBlockKey.getKey().split("-");
      Location loc = chunk.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])).getLocation();

      // Get Custom BLock Facing Direction
      CraftoryDirection direction = customBlockContainer.get(StorageKeys.directionKey, new CraftoryDirectionDataType());

      // Get Custom Block Type
      CraftoryBlockKey blockKey = customBlockContainer.get(StorageKeys.blockType, new CraftoryBlockKeyDataType());

      Optional<? extends CustomBlock> customBlockOptional = blockRegistry.getNewCustomBlockInstance(blockKey, loc, direction);
      customBlockManager.loadCustomBlock(customBlockOptional.get());
    }
  }
}
