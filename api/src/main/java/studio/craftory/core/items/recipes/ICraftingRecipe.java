package studio.craftory.core.items.recipes;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.plugin.Plugin;

public interface ICraftingRecipe {

  boolean hasPermission(Player player);

  void handlePrepareItemCraft(PrepareItemCraftEvent e);

  NamespacedKey getNamespacedKey();

  void register(Plugin plugin);

}
