package studio.craftory.core.listeners;

import java.util.Optional;
import javax.inject.Inject;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.CustomBlockRegistry;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.keys.CraftoryBlockKey;
import studio.craftory.core.utils.Log;

public class CustomBlockListener implements Listener {

  @Inject
  private CustomBlockManager customBlockManager;
  @Inject
  private CustomBlockRegistry blockRegistry;
  private NamespacedKey blockItemKey = new NamespacedKey(Craftory.getInstance(), "blockItemKey");

  @EventHandler
  public void onCustomBlockPlace(BlockPlaceEvent blockPlaceEvent) {
    //Check is Custom Block Being Placed
    //if (!blockPlaceEvent.getItemInHand().hasItemMeta()) return;

    PersistentDataContainer dataHolder = blockPlaceEvent.getItemInHand().getItemMeta().getPersistentDataContainer();
    //if (!dataHolder.has(blockItemKey, PersistentDataType.STRING)) return;

    //Get Custom Block Data
    CraftoryDirection direction = getDirection(blockPlaceEvent.getPlayer());
    String blockKey = dataHolder.get(blockItemKey, PersistentDataType.STRING);

    Optional<CraftoryBlockKey> blockKeyOptional = blockRegistry.getBlockKey("CraftoryExample:SimpleGenerator");
    if (blockKeyOptional.isPresent()) {
      customBlockManager.placeCustomBlock(blockKeyOptional.get(), blockPlaceEvent.getBlock().getLocation(), direction);
    } else {
      Log.warn("Unable to place custom block");
    }
  }

  @EventHandler
  public void onCustomBlockClick(PlayerInteractEvent playerInteractEvent) {
    if (playerInteractEvent.getAction() != Action.LEFT_CLICK_BLOCK && playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) return;
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
