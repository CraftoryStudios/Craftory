package studio.craftory.core.listeners;

import javax.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import studio.craftory.core.blocks.CustomBlockManager;

public class ChunkListener implements Listener {

  @Inject
  private CustomBlockManager customBlockManager;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChunkLoad(ChunkLoadEvent event) {
    customBlockManager.getDataStorageManager().loadSavedBlocks(event.getChunk());
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onChunkUnload(ChunkUnloadEvent event) {
    customBlockManager.unloadCustomChunk(event.getChunk(), true);
  }
}
