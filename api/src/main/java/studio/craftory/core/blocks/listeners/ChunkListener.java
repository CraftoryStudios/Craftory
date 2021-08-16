package studio.craftory.core.blocks.listeners;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.BlockRegistry;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.CustomBlock;
import studio.craftory.core.blocks.storage.CraftoryBlockKeyDataType;
import studio.craftory.core.blocks.storage.CraftoryDirectionDataType;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.containers.keys.CraftoryBlockKey;

public class ChunkListener implements Listener {
  @Inject
  private CustomBlockManager customBlockManager;
  @Inject
  private BlockRegistry blockRegistry;

  private static final NamespacedKey customBlocksKey = new NamespacedKey(Craftory.getInstance(), "customblocks");
  private static final NamespacedKey directionKey = new NamespacedKey(Craftory.getInstance(), "facing");
  private static NamespacedKey blockType = new NamespacedKey(Craftory.getInstance(), "type");

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChunkLoad(ChunkLoadEvent event) {
    load(event.getChunk());
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onChunkUnload(ChunkUnloadEvent event) {
    customBlockManager.unloadCustomChunk(event.getChunk(), true);
  }

  private void load(Chunk chunk) {
    if (!chunk.getPersistentDataContainer().has(customBlocksKey, PersistentDataType.TAG_CONTAINER)) return;

    final PersistentDataContainer customBlocksContainer = chunk.getPersistentDataContainer().get(customBlocksKey, PersistentDataType.TAG_CONTAINER);

    for (NamespacedKey customBlockKey : customBlocksContainer.getKeys()) {
      final PersistentDataContainer customBlockContainer = customBlocksContainer.get(customBlockKey, PersistentDataType.TAG_CONTAINER);

      // Get Custom Block Location
      String[] coords = customBlockKey.getKey().split("-");
      Location loc = chunk.getBlock(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])).getLocation();

      // Get Custom BLock Facing Direction
      CraftoryDirection direction = customBlockContainer.get(directionKey, new CraftoryDirectionDataType());

      // Get Custom Block Type
      CraftoryBlockKey blockKey = customBlockContainer.get(blockType, new CraftoryBlockKeyDataType());

      Optional<? extends CustomBlock> customBlockOptional = blockRegistry.getNewCustomBlockInstance(blockKey, loc, direction);
      customBlockManager.loadCustomBlock(customBlockOptional.get());
    }
  }
}
