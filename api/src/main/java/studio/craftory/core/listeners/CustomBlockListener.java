package studio.craftory.core.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.Craftory;
import studio.craftory.core.data.CraftoryDirection;

public class CustomBlockListener {

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

  private CraftoryDirection getDirection(Player player) {
    int degrees = (Math.round(player.getLocation().getYaw()) + 270) % 360;
    if (degrees > 315 || degrees <= 45) return CraftoryDirection.NORTH;
    if (degrees <= 135) return CraftoryDirection.EAST;
    if (degrees <= 225) return CraftoryDirection.SOUTH;
    return CraftoryDirection.WEST;
  }
}
