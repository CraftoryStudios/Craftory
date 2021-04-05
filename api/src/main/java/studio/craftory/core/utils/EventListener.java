package studio.craftory.core.utils;

import javax.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import studio.craftory.core.blocks.BlockRenderManager;
import studio.craftory.core.data.events.ResourcePackBuilt;
import studio.craftory.core.items.CustomItemManager;

public class EventListener implements Listener {

  @Inject
  private BlockRenderManager blockRenderManager;

  @Inject
  private CustomItemManager customItemManager;

  @EventHandler
  public void onResourcePackBuilt(ResourcePackBuilt e) {
    blockRenderManager.onResourcePackBuilt(e);
    customItemManager.onResourcePackBuilt(e);
  }

}
