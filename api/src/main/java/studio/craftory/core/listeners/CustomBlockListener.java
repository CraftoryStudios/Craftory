package studio.craftory.core.listeners;

import java.util.Optional;
import javax.inject.Inject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import studio.craftory.core.utils.Constants;
import studio.craftory.core.utils.Constants.BlockLists;
import studio.craftory.core.utils.Constants.Keys;
import studio.craftory.core.utils.Log;

public class CustomBlockListener implements Listener {

  @Inject
  private CustomBlockManager customBlockManager;
  @Inject
  private CustomBlockRegistry blockRegistry;

  @EventHandler
  public void onCustomBlockClick(PlayerInteractEvent playerInteractEvent) {
    if (playerInteractEvent.getAction() != Action.LEFT_CLICK_BLOCK && playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) return;
    Optional<BaseCustomBlock> customBlock = customBlockManager.getLoadedCustomBlockAt(playerInteractEvent.getClickedBlock().getLocation());
    customBlock.ifPresent(baseCustomBlock -> baseCustomBlock.onPlayerClick(playerInteractEvent));
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

  private boolean isStandingInsideBlock(final Player player, final Location blockLocation) {
    final Location playerLocation = player.getLocation();
    return playerLocation.getBlockX() == blockLocation.getBlockX()
        && (playerLocation.getBlockY() == blockLocation.getBlockY()
        || playerLocation.getBlockY() + 1 == blockLocation.getBlockY())
        && playerLocation.getBlockZ() == blockLocation.getBlockZ();
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onNoteBlockInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
      return;

    // Cancel block interaction if custom block of type notebook
    if (event.hasBlock() && event.getClickedBlock().getType() == Material.NOTE_BLOCK && customBlockManager.containsCustomBlock(event.getClickedBlock().getLocation())) {
      event.setCancelled(true);
    }

    // Check is Custom Block Being Placed
    final ItemStack item = event.getItem();
    if (item == null && !item.hasItemMeta()) return;
    if (!item.getItemMeta().getPersistentDataContainer().has(Keys.BLOCK_ITEM_KEY, PersistentDataType.STRING)) return;

    // Get Custom Block Data
    final String blockKey = item.getItemMeta().getPersistentDataContainer().get(Keys.BLOCK_ITEM_KEY, PersistentDataType.STRING);
    final Optional<CraftoryBlockKey> blockKeyOptional = blockRegistry.getBlockKey(blockKey);

    // Place Custom Block
    if (blockKeyOptional.isPresent()) {
      final CraftoryDirection direction = CraftoryDirection.getCraftoryDirection(event.getBlockFace().getOppositeFace());

      // Determine block place location
      final Location target;
      if (BlockLists.REPLACEABLE_BLOCKS.contains(event.getClickedBlock().getType())) {
        target = event.getClickedBlock().getLocation();
      } else {
        target = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
      }

      if (isStandingInsideBlock(event.getPlayer(), target)) return;

      customBlockManager.placeCustomBlock(blockKeyOptional.get(), target, direction);
      event.setCancelled(true);
    } else {
      Log.warn("Unable to place custom block");
    }
  }
}
