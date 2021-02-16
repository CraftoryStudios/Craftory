package studio.craftory.core.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import studio.craftory.core.api.CustomItemAPI;

public class SpawnItemCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player && args.length > 0) {
      Player player = (Player)sender;
      String itemName = args[0];
      ItemStack item = CustomItemAPI.getCustomItemOrDefault(itemName);
      int amount = 1;
      if (args.length > 1) {
        try {
          amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException ignored) {/* Ignore */}
      }
      item.setAmount(amount);
      player.getInventory().addItem(item);
      return item.getType() != Material.AIR;
    }
    return false;
  }
}
