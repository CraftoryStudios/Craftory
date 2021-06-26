package studio.craftory.core.listeners;

import java.util.Optional;
import javax.inject.Inject;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.blocks.CustomBlockManager;
import studio.craftory.core.blocks.CustomBlockRegistry;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.containers.keys.CraftoryBlockKey;
import studio.craftory.core.items.CustomItemUtils;
import studio.craftory.core.utils.Constants.Keys;
import studio.craftory.core.utils.Log;

public class CustomBlockListener implements Listener {

  @Inject
  private CustomBlockManager customBlockManager;
  @Inject
  private CustomBlockRegistry blockRegistry;

  @EventHandler
  public void onCustomBlockPlace(BlockPlaceEvent blockPlaceEvent) {
    //Check is Custom Block Being Placed
    if (!blockPlaceEvent.getItemInHand().hasItemMeta()) return;
    ItemStack itemStack = blockPlaceEvent.getItemInHand();
    CustomItemUtils.validateItemStackMeta(itemStack);
    PersistentDataContainer dataHolder = itemStack.getItemMeta().getPersistentDataContainer();
    if (!dataHolder.has(Keys.BLOCK_ITEM_KEY, PersistentDataType.STRING)) return;

    //Get Custom Block Data
    CraftoryDirection direction = getDirection(blockPlaceEvent.getPlayer());
    String blockKey = dataHolder.get(Keys.BLOCK_ITEM_KEY, PersistentDataType.STRING);

    Optional<CraftoryBlockKey> blockKeyOptional = blockRegistry.getBlockKey(blockKey);
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

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onCustomBlockPhysics(BlockPhysicsEvent event) {
    Material type = event.getChangedType();
    if ((type == Material.MUSHROOM_STEM || type == Material.BROWN_MUSHROOM_BLOCK || type == Material.RED_MUSHROOM_BLOCK
        || type == Material.NOTE_BLOCK) && customBlockManager.containsCustomBlock(event.getBlock().getLocation())) {
      event.setCancelled(true);
      event.getBlock().getState().update(true, false);
    }
  }

  @EventHandler
  public void onNoteBlockTwo(BlockFadeEvent e) {
    if (e.getBlock().getType() != Material.GRASS_BLOCK) return;
    for (BlockFace face : BlockFace.values()) {
      if (e.getBlock().getRelative(face).getType() == Material.NOTE_BLOCK && customBlockManager.containsCustomBlock(e.getBlock().getRelative(face).getLocation())) {
        e.setCancelled(true);
        e.getBlock().setType(Material.DIRT, false);
        return;
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onNoteBlockInteract(PlayerInteractEvent event) {
    if (event.hasBlock() && event.getClickedBlock().getType() == Material.NOTE_BLOCK && customBlockManager.containsCustomBlock(event.getClickedBlock().getLocation())) {
      event.setCancelled(true);
    }
  }
}
