package studio.craftory.core.listeners;

import javax.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import studio.craftory.core.blocks.CustomBlockManager;

public class WorldListener implements Listener {

  @Inject
  private CustomBlockManager customBlockManager;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onWorldLoad(WorldLoadEvent event) {
    customBlockManager.getDataStorageManager().registerWorld(event.getWorld());
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onWorldUnload(WorldUnloadEvent event) {
    customBlockManager.getDataStorageManager().unregisterWorld(event.getWorld());

    //TODO PER WORLD
  }
}
