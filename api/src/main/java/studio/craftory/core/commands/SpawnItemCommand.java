package studio.craftory.core.commands;

import java.util.Optional;
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
      Optional<ItemStack> itemStack = CustomItemAPI.getCustomItemOrDefault(itemName);
      if(itemStack.isPresent()) {
        ItemStack item = itemStack.get();
        int amount = 1;
        if (args.length > 1) {
          try {
            amount = Integer.parseInt(args[1]);
          } catch (NumberFormatException ignored) {/* Ignore */}
        }
        item.setAmount(amount);
        player.getInventory().addItem(item);
      }
    }
    return false;
  }
}
