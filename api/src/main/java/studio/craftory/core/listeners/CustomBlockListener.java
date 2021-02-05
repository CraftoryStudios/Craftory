package studio.craftory.core.listeners;

import java.util.Optional;
import javax.inject.Inject;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.CraftoryDirection;

public class CustomBlockListener implements Listener {

  @Inject
  private CustomBlockManager customBlockManager;
  private NamespacedKey blockItemKey = new NamespacedKey(Craftory.getInstance(), "blockItemKey");

  @EventHandler
  public void onCustomBlockPlace(BlockPlaceEvent blockPlaceEvent) {

    //Check is Custom Block Being Placed
    if (!blockPlaceEvent.getItemInHand().hasItemMeta()) return;
      PersistentDataContainer dataHolder = blockPlaceEvent.getItemInHand().getItemMeta().getPersistentDataContainer();
    if (!dataHolder.has(blockItemKey, PersistentDataType.STRING)) return;

    //Get Custom Block Data
    String blockID = dataHolder.get(blockItemKey, PersistentDataType.STRING);
    CraftoryDirection facing = getDirection(blockPlaceEvent.getPlayer());


    //Create Custom Block

    //Render Custom Block
  }

  @EventHandler
  public void onCustomBlockClick(PlayerInteractEvent playerInteractEvent) {
    Optional<BaseCustomBlock> customBlock = customBlockManager.getLoadedCustomBlockAt(playerInteractEvent.getClickedBlock().getLocation());
    customBlock.ifPresent(baseCustomBlock -> baseCustomBlock.onPlayerClick(playerInteractEvent));
  }

  private CraftoryDirection getDirection(Player player) {
    int degrees = (Math.round(player.getLocation().getYaw()) + 270) % 360;
    if (degrees > 315 || degrees <= 45) return CraftoryDirection.NORTH;
    if (degrees <= 135) return CraftoryDirection.EAST;
    if (degrees <= 225) return CraftoryDirection.SOUTH;
    return CraftoryDirection.WEST;
  }
}
